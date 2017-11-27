package ModelLayer.Networking;

import ControlLayer.ServerEngine;
import ModelLayer.Networking.Messages.Message;
import ModelLayer.Networking.Messages.MessageType;
import ViewLayer.Screens.ServerScr.LogTerminal;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import javax.swing.*;
import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Uses a server socket to receive incoming TCP connections.
 * Spawns a session for each incoming connection on a separate thread.
 * Places players (sockets) into a lobby and matches them if they want to play on the same map.
 * A match is represented by an ActiveMatch object.
 * The clients and server are communicating by sending serialised Message objects to each other.
 */
public class Server {
    /**
     * Reference to the server controller.
     */
    private final ServerEngine _ServerEngine;
    /**
     * List of open sockets. One socket for each client.
     */
    private final ArrayList<Socket> _ClientSocket = new ArrayList<>();
    /**
     * Contains clients (as sockets) who are waiting to find an opponent to play with on a specific map.
     * The map is specified as string (the map's name: e.g. "Easy")
     */
    private final Map<Socket, String> _PreGameLobby = new HashMap<>();
    /**
     * Adds observability to the _PreGameLobby Map. This allows separate threads writing this data collection
     * and notify the main thread about changes (e.g. new player is waiting in the lobby).
     */
    private final ObservableMap<Socket, String> _ObservablePreGameLobby = FXCollections.observableMap(_PreGameLobby);
    /**
     * Contains all sessions. Each session represents a client (socket) server connection.
     */
    private final ArrayList<Session> _ClientSessions = new ArrayList<>();
    /**
     * Represents a match (game) between two players (socket) on a selected map.
     */
    private final ArrayList<ActiveMatch> _Matches = new ArrayList<>();
    /**
     * Adds observability to the _Matches array list. Represents a match (game) between two players (socket) on a selected map.
     */
    private final ObservableList<ActiveMatch> _ObservableMatches = FXCollections.observableArrayList(_Matches);
    /**
     * Is the server alive and waiting for connection/data
     */
    private volatile boolean _IsRunning = false;
    /**
     * UI Output for messages and general logs.
     */
    private LogTerminal _MessagesTerminal;
    /**
     * The one and only server socket reference. Clients are connecting to this socket.
     */
    private ServerSocket _ServerSocket;
    /**
     * Thread pool. One thread for each client.
     */
    private ExecutorService _ClientThreads;
    /**
     * This thread runs the ServerSocket.Accept() loop.
     */
    private Thread _MainConnectionAcceptor;
    /**
     * The connection acceptor thread looks at this variable to decide if incoming connections can be accepted.
     */
    private boolean _IsIncomingConnectionsEnabled = false;
    /**
     * Accepts new client connections and creates runnable Session objects.
     * Session objects will be run on a separate thread from the thread pool (executor service).
     * This is run on the separate _MainConnectionAcceptor thread.
     */
    private final Runnable AcceptClientConnections = () ->
    {
        while (_IsIncomingConnectionsEnabled && _IsRunning) {
            try {
                Socket aClient = _ServerSocket.accept();
                GMLog("Client connection accepted: " + aClient.getInetAddress().toString());
                _ClientSocket.add(aClient);

                //Create a new Session. Check if the session can open the in/out streams for the socket.
                //If yes, then launch the Session's run on a separate thread.
                Session session = new Session(aClient, _MessagesTerminal, _ObservablePreGameLobby, _ObservableMatches);
                if (session.OpenStreams()) {
                    _ClientSessions.add(session);
                    _ClientThreads.submit(session);
                } else {
                    //Could not open the in/out streams for the socket. Close the socket and remove from the list.
                    GMLog("Could not get the in or out stream for the connection. Connection is closed");
                    aClient.close();
                    _ClientSocket.remove(aClient);
                }

            } catch (SocketException se) {
                //Check if the thread still supposed to accept connections. If true, then raise error.
                //If false, then the socket is probably closed already by the main thread (nothing to do).
                if (_IsIncomingConnectionsEnabled && _IsRunning) {
                    GMLogSync("ERROR: " + se.getMessage());
                }
            } catch (Exception e) {
                if (_IsIncomingConnectionsEnabled && _IsRunning) {
                    GMLogSync("ERROR during accepting a client connection: " + e.getMessage());
                }
            }

            if ((!_IsIncomingConnectionsEnabled) || (!_IsRunning)) {
                GMLogSync("Main Listening thread with server socket ends now.");
            }
        }

    };
    /**
     * Provides unique ID names for matches. This is used when writing out logs to refer to a match name.
     */
    private int _MatchIDCounter = 1;

    /**
     * Constructor of the Server.
     * Uses a server socket to receive incoming TCP connections.
     * Spawns a session for each incoming connection on a separate thread.
     * Places players (sockets) into a lobby and matches them if they want to play on the same map.
     * A match is represented by an ActiveMatch object.
     * The clients and server are communicating by sending serialised Message objects to each other.
     *
     * @param _MessagesTerminal Terminal where the log output can be placed. This will be used by the Sessions and Matches too.
     * @param _ServerEngine     The controller layer which this server belongs to. Controller will be called back up on major server events.
     */
    public Server(LogTerminal _MessagesTerminal, ServerEngine _ServerEngine) {
        this._MessagesTerminal = _MessagesTerminal;
        this._ServerEngine = _ServerEngine;

        //Add listener and event handler for the Observable Lobby changes
        _ObservablePreGameLobby.addListener(this::HandleLobbyChange);
    }

    /**
     * Callend when a change occurs in the waiting lobby (e.g. socket (player) gets added or removed)
     * @param change The change event that occurred
     */
    private void HandleLobbyChange(MapChangeListener.Change<? extends Socket, ? extends String> change) {
        if (change.wasAdded()) {
            GMLog("A new player has added to the lobby, waiting for \"" + change.getValueAdded() + "\" map.");

            //Check if there is a matching player.
            CheckLobbyForMatching(change.getValueAdded(), change.getKey());
        }

        if (change.wasRemoved()) {
            GMLog("A player has removed from the lobby: " + change.getKey().getInetAddress().toString());
        }

    }

    /**
     * Checks if there is a pair of players in the lobby waiting for the same type of map to game on.
     * If yes, a Match will be created for them and they get removed from the lobby.
     *
     * @param val The value (map name) to look for.
     * @param key This player already has a matching value as val. An another player needs to be found.
     */
    private void CheckLobbyForMatching(String val, Socket key) {
        boolean deleteFromLobby = false;
        Socket keyToDelete = null;

        //Check if there is more than 1 player in the lobby.
        if (_ObservablePreGameLobby.size() > 1) {
            //Start searching for a value that is the same as in the parameter but with different key
            for (Map.Entry<Socket, String> i : _ObservablePreGameLobby.entrySet()) {
                if (i.getValue().equals(val) && !i.getKey().equals(key)) {
                    //Match found between lobby players.
                    GMLog("Matching players found. Match \"" + Integer.toString(_MatchIDCounter) + "\" created.");

                    //Instantiating a new match and adding to the list.
                    // Note: the first requesting player becomes player 1
                    ActiveMatch match = new ActiveMatch(i.getKey(), key, val, _MatchIDCounter, this);
                    _ObservableMatches.add(match);
                    _MatchIDCounter++;

                    //Mark to read the matched keys
                    deleteFromLobby = true;
                    keyToDelete = i.getKey();

                    break;
                }
            }

            //As match found, delete the players from the waiting lobby.
            if (deleteFromLobby) {
                //Removing matched players from the lobby.
                _ObservablePreGameLobby.remove(key);
                _ObservablePreGameLobby.remove(keyToDelete);
            }

        }
    }


    /**
     * Is the server alive and waiting for connection/data
     *
     * @return True if the server is running. False if not
     */
    public boolean IsRunning() {
        return _IsRunning;
    }

    /**
     * Starts the server on a specified port number. The controller will got called back on successful start.
     * @param portNumber The port number to use to start the server.
     */
    public void StartServer(int portNumber) {
        if (!_IsRunning) {
            GMLog("Launching server...");

            //Open the new socket.
            if (CreateServerSocket(portNumber)) {
                GMLog("Server socket created on port " + Integer.toString(portNumber) + ".");
                CreateNewClientThreadPool();
                _IsIncomingConnectionsEnabled = true;

                _MainConnectionAcceptor = new Thread(AcceptClientConnections);
                _MainConnectionAcceptor.start();

                _IsRunning = true;
                _ServerEngine.ServerIsUp();
            } else {
                _IsRunning = false;
                GMLog("ERROR. Could not open the Server Socket. Could not start the server.");
            }

        } else {
            _IsRunning = false;
            GMLog("ERROR. Cannot launch server. Server is already running.");
        }
    }

    /**
     * Stops the server. Tries to send out client notifications prior.
     * Controller gets notified if the stopping was successful.
     */
    public void StopServer() {
        if (_IsRunning) {
            GMLog("Server is shutting down...");
            SendServerDownMessageToAllClients();
            _IsRunning = false;
            _IsIncomingConnectionsEnabled = false;


            if (TryCloseServerSocket()) {
                GMLog("Server socket is closed now.");
            } else {
                GMLog("Could not close the server socket.");
            }

            _Matches.clear();
            _ClientSessions.clear();
            _ClientSocket.clear();
            _PreGameLobby.clear();
            _IsRunning = false;
            _ServerEngine.ServerIsDown();
            GMLog("Server has stopped.");

        } else {
            GMLog("No need to shutdown. Server is not running.");
        }
    }

    /**
     * Tries to send a Server down message to all open client sockets.
     */
    private void SendServerDownMessageToAllClients() {
        Message msg = new Message(MessageType.SERVERDOWN);
        if (_ClientSocket != null) {
            for (Session _ClientSession : _ClientSessions) {

                if (_ClientSession.SendMessage(msg)) {
                    GMLog("The server down notification sent to client. ");
                } else {
                    GMLog("Could not send server down notification to a client.");
                }
                _ClientSession.ServerShutDownNotification();
            }
        }
    }

    /**
     * Instantiates a new client thread pool. This manages the Sessions.
     * Each Session instance runs on a seperate thread within the thread pool.
     */
    private void CreateNewClientThreadPool() {
        _ClientThreads = Executors.newCachedThreadPool();
    }



    /**
     * If the server socket exists (not null) and open, then it tries to close it.
     *
     * @return Returns false if error occurred during closing the socket. Returns true if successfully closed, or did not exists.
     */
    private boolean TryCloseServerSocket() {
        boolean result = true;

        if (_ServerSocket != null) {
            if (!_ServerSocket.isClosed()) {
                try {
                    _ServerSocket.close();
                } catch (IOException e) {
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     * Creates a new server socket on the specified port.
     *
     * @param port The port to listen to.
     * @return Returns true if the operation was successful. Returns false if it failed.
     */
    private boolean CreateServerSocket(int port) {
        boolean result = true;
        try {
            _ServerSocket = new ServerSocket(port);
        } catch (IOException e) {
            result = false;
        }
        return result;
    }


    /**
     * Displays a message (with date and time, server as the sender) in the General Message Log in the UI.
     *
     * @param text The text to display.
     */
    private void GMLog(String text) {
        final LocalDateTime now = LocalDateTime.now();
        String time = now.toLocalTime().toString();

        final String sender = "SERVER: ";

        String msg = time + ", " + sender + text;
        _MessagesTerminal.Log(msg);

    }


    /**
     * Displays a message (with date and time, server as the sender) in the General Message Log in the UI.
     * This is a synchronised version of the GMLog method, which is suited for access by the MainAcceptor thread.
     *
     * @param text The text to display.
     */
    private synchronized void GMLogSync(String text) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                GMLog(text);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }


    /**
     * Retrieves the local machines name.
     *
     * @return The machine's name.
     */
    public String GetLocalHostName() {
        String address = "";
        try {
            address = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            address = "Cannot detect.";
        }
        return address;
    }


    /**
     * Removes a match from the list of ongoing matches.
     * Called by the match itself, which identifies that the game is over.
     *
     * @param activeMatch The match to be removed from the list.
     * @param matchID     The id (name) of the match.
     */
    public void AMatchHasAnded(ActiveMatch activeMatch, int matchID) {
        GMLogSync("Match \"" + Integer.toString(matchID) + "\" has ended.");
        _Matches.remove(activeMatch);
    }


    /**
     * Getter for the terminal for the server logs.
     * @return A terminal where messages can be printed.
     */
    public LogTerminal getTerminal() {
        return _MessagesTerminal;
    }
}
