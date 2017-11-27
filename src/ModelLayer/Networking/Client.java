package ModelLayer.Networking;

import ControlLayer.SharedResources;
import ModelLayer.CarRemote;
import ModelLayer.Networking.Messages.*;
import ViewLayer.Screens.ServerScr.LogTerminal;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;


/**
 * Sends and receives data from the server using TCP socket and serialised objects.
 * Uses separate threads, therefore it is not blocking the caller.
 */
public class Client {
    /**
     * Any client side messages will be output here
     */
    private final LogTerminal _Terminal;

    /**
     * The socket towards the server.
     */
    private volatile Socket _Connection;

    /**
     * The OutputStream that is provided by the socket.
     */
    private volatile ObjectOutputStream _OutStream;

    /**
     * The InputStream that is provided by the socket.
     */
    private volatile ObjectInputStream _In;


    /**
     * True after the server sends a "server down" message. Sets false after each new successful connection.
     */
    private volatile boolean _ServerDownMessageReceived = false;

    /**
     * This thread waits for the server's response after a map/opponent request has been sent.
     */
    private Thread _MapRequesterThread;

    /**
     * The map name which the player wants to play on and the server needs to find opponents for.
     * It is empty when no map is being requested or game already goes on.
     */
    private String _CurrentlyRequestedMapName = "";

    /**
     * Set to be true when a separate thread is already waiting for the server to find a partner to play with.
     * This is used during the launch game screen, when user clicks on connect to the server.
     */
    private volatile boolean _IsThreadWaitingForMapResponse = false;

    /**
     * True if the socket connection is closed by the client on purpose (e.g. controller requested it)
     * This allows to suppress exception errors that may occur when a socket gets closed and other threads are using it.
     */
    private volatile boolean _IsConnectionClosedOnPurpose = false;
    /**
     * Server's response on successful map request.
     */
    private MessageMapResponse _MapResponse;

    /**
     * Contains the most recont error message that occurred during transferring a message to the server.
     */
    private volatile String _LastSendingErrorMessage = "";
    /**
     * Waits for the server to respond to the MessageType.LOOKINGFOROPPONENT message on a separate thread.
     */
    private final Runnable WaitForOpponentRequest = () ->
    {
        Message srvResponse;
        try {
            _IsThreadWaitingForMapResponse = true;
            _MapResponse = null;
            srvResponse = (Message) _In.readObject();

            if (srvResponse.getType() == MessageType.OPPONENTFOUNDSTARTGAME) {
                _CurrentlyRequestedMapName = "";
                _MapResponse = (MessageMapResponse) srvResponse;
                _IsThreadWaitingForMapResponse = false;

                ClientLog("Success. Server found an opponent. Player number: " + Integer.toString(_MapResponse.get_GivenPlayerNumber()));
                //Now call back the controller on the EDT thread
                SwingUtilities.invokeLater(() ->
                        SharedResources.MainController.OpponentFoundCallback());


            } else {
                _IsThreadWaitingForMapResponse = false;

                if (srvResponse.getType() == MessageType.SERVERDOWN) {
                    _ServerDownMessageReceived = true;
                    ClientLog("Whops. The server went down. Try to restart the server and connect again.");
                    _Connection.close();
                    _Connection = null;
                } else {
                    ClientLog("ERROR. Please try to connect again. Unexpected message type from the server: " + Integer.toString(srvResponse.getType()));
                }
            }

        } catch (SocketException se) {
            if (!_IsConnectionClosedOnPurpose)
                ClientLog("Socket has been closed. Stopped waiting for opponent.");
        } catch (Exception e) {
            if (!_IsConnectionClosedOnPurpose)
                ClientLog("ERROR: " + e.getMessage());
        }
    };
    /**
     * The last status message that has been sent to the server.
     */
    private volatile MessageCarStatusUpdate _LastUpdateSent;
    /**
     * The remote car object on the local machine, which will consume the car status updates received from the server.
     */
    private CarRemote _CarRemoteToHandleIncomingUpdates;
    /**
     * True while the client is listening to incoming car status updates.
     */
    private volatile boolean _ListenToIncomingStatusUpdates = false;
    /**
     * The "status update messages listening" is running on this thread.
     */
    private Thread _MainStatusListenerThread;
    /**
     * Listens to car status updates from the server and calls back the CarRemote object. Runs on separate thread.
     * Listens until it receives a crash message or been directed to stop.
     */
    private final Runnable MyUpdateListener = () ->
    {
        while (_ListenToIncomingStatusUpdates && !_ServerDownMessageReceived) {
            Message msg = null;
            try {
                msg = (Message) _In.readObject();

            } catch (Exception e) {
                msg = null;
                //The socket has become closed. Check if this happened on purpose.
                // If not, signal the game controller which will stop the game.
                if (_ListenToIncomingStatusUpdates && !_ServerDownMessageReceived) {
                    e.printStackTrace();
                    _ListenToIncomingStatusUpdates = false;
                    SwingUtilities.invokeLater(() -> SharedResources.MainController.get_GameEngine().HandleErrorWhenSendingStatusUpdateToSrv(e.getMessage()));
                }
            }


            if (msg != null) {
                //If opponent's car update message received (this is the most common).
                if (msg.getType() == MessageType.INGAMEPOSITIONUPDATE) {
                    MessageCarStatusUpdate updateMsg = (MessageCarStatusUpdate) msg;
                    _CarRemoteToHandleIncomingUpdates.CarUpdateCallback(updateMsg);
                }

                //If car crash message received then stop listening to new messages and notify the Gameengine
                if (msg.getType() == MessageType.INGAMECRASH) {
                    _ListenToIncomingStatusUpdates = false;
                    SwingUtilities.invokeLater(() -> _CarRemoteToHandleIncomingUpdates.CarCrashCallback());
                }

                //If the opponent dropped message received then stop listening and notify GameEngine
                if (msg.getType() == MessageType.PLAYERDROPPED) {
                    _ListenToIncomingStatusUpdates = false;
                    SwingUtilities.invokeLater(() -> SharedResources.MainController.get_GameEngine().OpponentLeftTheGameCallback());
                }

                //If server down message received then stop listening and notify GameEngine
                if (msg.getType() == MessageType.SERVERDOWN) {
                    _ServerDownMessageReceived = true;
                    _ListenToIncomingStatusUpdates = false;
                    SwingUtilities.invokeLater(() -> SharedResources.MainController.get_GameEngine().ServerDownCallback());
                }
            }
        }
    };
    /**
     * Sends the local cars' update information to the server.
     * Only sends it if the server did not send Shutdown message and listing on this side is also allowed.
     */
    private final Runnable SendStatus = () ->
    {
        if (!_ServerDownMessageReceived) {
            if (_ListenToIncomingStatusUpdates) {
                if (_LastUpdateSent != null) {
                    //Sending out the message and checking for error
                    if (!SendOut(_LastUpdateSent)) {
                        //If error occurred
                        _ListenToIncomingStatusUpdates = false;
                        SwingUtilities.invokeLater(() -> SharedResources.MainController.get_GameEngine().HandleErrorWhenSendingStatusUpdateToSrv(_LastSendingErrorMessage));
                    }
                }
            }
        }
    };
    /**
     * Constructor.
     * Sends and receives data from the server using TCP socket and serialised objects.
     *
     * @param logTerminal The terminal to display or log connection events.
     */
    public Client(LogTerminal logTerminal) {
        _Terminal = logTerminal;
    }

    /**
     * Sends out a message through the outputstream.
     * This is the only method that accessed the output stream for sending.
     * @param msg The message to send to the server.
     * @return Returns true if no issues happened during sending. Returns false if there was an error.
     */
    private synchronized boolean SendOut(Message msg)
    {
        boolean result = false;
        try {
            _OutStream.writeObject(msg);
            result = true;
        } catch (Exception e) {
            _LastSendingErrorMessage = e.getMessage().toString();
            ClientLog("Message sending error: " + e.getMessage());
        }
        return result;
    }

    /**
     * Tries to close the output stream.
     * @return An empty string if no error occurred or the the error message if it happened.
     */
    private synchronized String CloseOut()
    {
        if (_OutStream != null) {
            try {
                _OutStream.close();
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return "";
    }

    /**
     * Tries to open the output stream.
     * @return An empty string if no error occurred or the the error message if it happened.
     */
    private synchronized String OpenOut() {
        try {
            _OutStream = new ObjectOutputStream(_Connection.getOutputStream());
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    /**
     * Opens a socket towards a specific server address and port number.
     * Tries to close the current socket first if it exists and open.
     *
     * @param serverAddress The IP address or hostname of the server.
     * @param portNumber    The port number to use for connecting.
     * @return Returns true if the operation was successful.
     */
    public boolean ConnectToServer(String serverAddress, int portNumber) {
        boolean result = true;

        if (TryCloseCurrentConnection(true)) {
            try {
                _Connection = new Socket(serverAddress, portNumber);
                _Connection.setSoTimeout(0);
                ClientLog("Connected to the server successfully.");

                //Getting input and output streams
                String isOpenOutOk = OpenOut();
                if (isOpenOutOk.equals("")) {
                    _In = new ObjectInputStream(_Connection.getInputStream());

                    //Sending a hello message
                    if (SendOut(new Message(MessageType.HELLO))) {
                        ClientLog("Hello sent to server.");
                        _ServerDownMessageReceived = false;
                        _IsConnectionClosedOnPurpose = false;
                    } else {
                        result = false;
                        ClientLog("Error when tried to send Hello message: " + _LastSendingErrorMessage);
                    }
                } else {
                    result = false;
                    ClientLog("Could not open output stream. Trying to close the socket.");
                    _Connection.close();
                    _Connection = null;
                    ClientLog("Connection closed");
                }

            } catch (UnknownHostException uh) {
                _Connection = null;
                result = false;
                ClientLog("Unknown host. Cannot connect.");
            } catch (ConnectException ce) {
                _Connection = null;
                result = false;
                ClientLog("ERROR. Server refused the connection.");
            } catch (IOException e) {
                _Connection = null;
                result = false;
                e.printStackTrace();
            }

        } else {
            result = false;
        }

        return result;
    }

    /**
     * Asks the server to provide an opponent to play with on the selected map.
     *
     * @param mapName Name of the map.
     */
    public void RequestOpponent(String mapName, int selectedCarTypeIndex) {
        if (_MapRequesterThread != null) {
            if (_MapRequesterThread.isAlive()) {
                if (_CurrentlyRequestedMapName == mapName) {
                    //This is a repeated request. Server is already searching. Will not send new requests.
                    return;
                } else {
                    //A request already open, but the user wants a different map. Cancel the current request and thread.
                    ClientLog("Cancelling current request for map \"" + _CurrentlyRequestedMapName + "\"...");
                    _MapRequesterThread.interrupt();

                    //Send cancel message to the server
                    Message cancel = new Message(MessageType.CANCELLOOKINGFOROPPONENT);

                    if (!SendOut(cancel)) {
                        ClientLog("Error while cancelling map request:" + _LastSendingErrorMessage);
                    }
                }
            }
        }

        MessageMapRequest mapMsg = new MessageMapRequest(MessageType.LOOKINGFOROPPONENT);
        mapMsg.set_MapName(mapName);
        mapMsg.set_CarImageFileIndex(selectedCarTypeIndex);
        _CurrentlyRequestedMapName = mapName;

        if (!SendOut(mapMsg)) {
            ClientLog("Error when sending out opponent request: " + _LastSendingErrorMessage);
        } else {
            ClientLog("Looking for opponent on map \"" + mapName + "\". Please wait.");
            //Wait for the server's response on a separate thread.
            _MapRequesterThread = new Thread(WaitForOpponentRequest);
            _MapRequesterThread.start();
        }
    }

    /**
     * Tries to close the currently open connection (socket) and streams if exist.
     *
     * @param sayGoodBye True to send a goodbye message to the server before closing the connection.
     * @return Returns true if the operation was successful. False if not.
     */
    public boolean TryCloseCurrentConnection(boolean sayGoodBye) {
        boolean result = true;
        if (_Connection != null) {
            if (!_Connection.isClosed()) {


                if (sayGoodBye) {
                    if (SendOut(new Message(MessageType.GOODBYE)))
                        ClientLog("Goodbye sent to the server.");
                    else
                        ClientLog("Could not say Goodbye to the server.");
                }

                //If a thread waits for map response, it needs to end.
                if (_IsThreadWaitingForMapResponse) {
                    _IsThreadWaitingForMapResponse = false;
                    _IsConnectionClosedOnPurpose = true;
                    _MapRequesterThread.interrupt();
                }


                String isClosingOk = CloseOut();
                if (!isClosingOk.equals(""))
                    ClientLog("Could not close the object output stream.");


                try {
                    _In.close();
                } catch (Exception e) {
                    result = false;
                }
                try {
                    _Connection.close();
                } catch (Exception e) {
                    result = false;
                    ClientLog("Could  not close the current socket.");
                }


                _Connection = null;
            }
        }
        return result;
    }

    /**
     * Sends player dropped message to the server.
     */
    public void SendPlayerDroppedMessage() {
        if (_Connection != null) {
            if (!_Connection.isClosed()) {
                if (!SendOut(new Message(MessageType.PLAYERDROPPED)))
                    ClientLog("Could not send drop message.");
            }
        }
    }

    /**
     * Displays a message (with date and time, client as the sender) in the General Message Log in the UI.
     *
     * @param text The text to display.
     */
    private synchronized void ClientLog(String text) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (_Terminal != null) {
                    final LocalDateTime now = LocalDateTime.now();
                    String time = now.toLocalTime().toString();

                    final String sender = "CLIENT: ";

                    String msg = time + ", " + sender + text;
                    _Terminal.Log(msg);
                }
            }
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * Getter for MapResponse
     *
     * @return Returns the map (opponent) responnse received from the server.
     */
    public MessageMapResponse get_MapResponse() {
        return _MapResponse;
    }

    /**
     * Sends a status message to the server to allow the remote client to update itself.
     * Message is sent on a separate thread.
     *
     * @param msg The message describing the cars' current status.
     */
    public void SendStatusUpdateMessage(MessageCarStatusUpdate msg) {
        _LastUpdateSent = msg;
        Thread messenger = new Thread(SendStatus);
        messenger.start();
    }

    /**
     * Assigns the incoming car status update message handler to this client. This object will be called back
     * when new status object is available.
     *
     * @param carRemote The event handler carRemote object.
     */
    public void NotifyMeOfCarUpdates(CarRemote carRemote) {
        _CarRemoteToHandleIncomingUpdates = carRemote;
    }

    /**
     * Stops listening for in game updates.
     */
    public void StopListeningForIngameUpdates() {
        _ListenToIncomingStatusUpdates = false;
        if (_MainStatusListenerThread != null) {
            if (_MainStatusListenerThread.isAlive()) {
                _MainStatusListenerThread.interrupt();
            }
            _MainStatusListenerThread = null;
        }
    }

    /**
     * Starts listening for car status updates.
     */
    public void StartListenForIngameUpdates() {
        if (_CarRemoteToHandleIncomingUpdates != null) {
            _ListenToIncomingStatusUpdates = true;
            _MainStatusListenerThread = new Thread(MyUpdateListener);
            _MainStatusListenerThread.start();
        }
    }

    /**
     * Sends a car crashed message to the remote client. Does not use seperate thread.
     */
    public void SendCarCrashedMessage() {
        Message msg = new Message(MessageType.INGAMECRASH);
        _ListenToIncomingStatusUpdates = false;

        if (!SendOut(msg))
            ClientLog("Error. Could not send out the car crashed message.");
    }


    /**
     * Returns if a thread is already waiting for the server to send map request repsone (find opponent)
     *
     * @return True if a thread is already running. False if not.
     */
    public boolean is_IsThreadWaitingForMapResponse() {
        return _IsThreadWaitingForMapResponse;
    }

    /**
     * Notifies the user through the client log to wait for the server to find an opponent.
     * This normally happens if the user clicks "start" multiple times on the LaunchScreen
     */
    public void NotifyUserThatAlreadyWaitingForOpponent() {
        ClientLog("I am already waiting for the server to find an opponent. Please be patient.");
    }
}
