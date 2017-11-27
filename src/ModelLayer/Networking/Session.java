package ModelLayer.Networking;

import ControlLayer.SharedResources;
import ModelLayer.Networking.Messages.Message;
import ModelLayer.Networking.Messages.MessageMapRequest;
import ModelLayer.Networking.Messages.MessageType;
import ViewLayer.Screens.ServerScr.LogTerminal;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * Represents a session between the server and a client.
 * Each session instance is "executed" by the Server on a separate thread.
 */
class Session implements Runnable {

    /**
     * Socket towards the connected client.
     */
    private final Socket _ClientSocket;

    /**
     * Terminal to display logs, messages.
     */
    private final LogTerminal _Terminal;
    /**
     * Lobby for clients waiting for opponent to play on a specific map. This is shared between the threads.
     * The session is listening to the changes of this Map.
     */
    private final ObservableMap<Socket, String> _Lobby;
    /**
     * The active matches (games) between clients. This is shared between the threads.
     * The session is listening to the changes of this List.
     */
    private final ObservableList<ActiveMatch> _Matches;
    /**
     * The OutputStream that is provided by the socket.
     */
    private volatile ObjectOutputStream _ObjOut;
    /**
     * The InputStream that is provided by the socket.
     */
    private volatile ObjectInputStream _ObjIn;
    /**
     * Set to true when major exception occurs and the main Run method should stop.
     */
    private boolean isMajorExceptionOccurred = false;
    /**
     * Represents the match the player is currently in. Null if not part of any match.
     */
    private ActiveMatch _CurrentMatch = null;
    /**
     * The last received Map request message from the client (sent from the client's launch screen)
     */
    private MessageMapRequest _LastMapRequestMsg = null;
    /**
     * Set to be true once the client sends a goodbye message, which means it finished the communication.
     * This will stop the sessions's Run if turns true.
     */
    private boolean _GoodbyeReceived = false;
    /**
     * True when the server decides on shutting down itself. If this true, the session stops the Run.
     */
    private boolean _IsServerShutsDown = false;

    /**
     * Counts the number of listening related exceptions raised in a row.
     * Receiving a valid message zeroes out this counter.
     * This is used to manage fault tolerance.
     * Hitting a number of exceptions in a row will cause stop listening and shut the session.
     */
    private int _ExceptionsInARowCounter = 0;
    /**
     * Counts the number of sending related exceptions raised in a row.
     * Sending a valid message zeroes out this counter.
     * This is used to manage fault tolerance.
     * Hitting a number of exceptions in a row will cause stop listening and shut the session.
     */
    private int _SendExceptionsInARowCounter = 0;

    /**
     * Represents the player number within a match. This could be either 1 or 2 when valid.
     */
    private int _AssignedPlayerNumber = -1;

    /**
     * Signals the associated ActiveMatch that this session (player) is ready to start the game.
     */
    private final Runnable ReadyToStartGame = () ->
            _CurrentMatch.MatchIsReadyToStart(_AssignedPlayerNumber, _LastMapRequestMsg.get_CarImageFileIndex());


    /**
     * Represents a session between the server and a client.
     * Each session instance is "executed" by the Server on a separate thread.
     *
     * @param _ClientSocket Socket towards the client.
     * @param _Terminal     Terminal to display logs, messages.
     * @param _Lobby        Lobby for clients waiting for opponent to play on a specific map.
     * @param _Matches      The observable list of matches. The Session is listening to this to see if it become a member of a match.
     */
    public Session(Socket _ClientSocket, LogTerminal _Terminal, ObservableMap<Socket, String> _Lobby, ObservableList<ActiveMatch> _Matches) {
        this._ClientSocket = _ClientSocket;
        this._Terminal = _Terminal;
        this._Lobby = _Lobby;
        this._Matches = _Matches;

        //Adding listener / event handler to the _Matches observable list
        _Matches.addListener(this::HandleMatchesListChange);
    }

    /**
     * Handles when change happens in the _Matches shared observable list
     * @param c The change that occurred
     */
    private void HandleMatchesListChange(ListChangeListener.Change<? extends ActiveMatch> c) {

        while (c.next()) {
            //If a new match has been created.
            if (c.wasAdded()) {
                //Check if this player (socket or session) is part of that match
                ActiveMatch newMatch = c.getAddedSubList().get(0);
                if (newMatch.HasThisPlayer(_ClientSocket)) {
                    //Retrieve player number
                    _AssignedPlayerNumber = newMatch.WhichPlayerNumberIsThisSocket(_ClientSocket);
                    //Assign this session to the socket within the new ActiveMatch object
                    newMatch.AssignSessionToSocket(_AssignedPlayerNumber, this);
                    //Set the new match be the current match
                    _CurrentMatch = newMatch;

                    SessionLog("Match found for this player.");

                    //Send "ready to start game" message on a seperate thread to the ActiveMath
                    Thread readySignal = new Thread(ReadyToStartGame);
                    readySignal.start();

                }
            }

            if (c.wasRemoved()) {
                //This event is only relevant if currently this session was in game.
                if (_CurrentMatch != null) {
                    //Check if the removed match is the one this session participates in.
                    if (c.getRemoved().get(0).HasThisPlayer(_ClientSocket)) {
                        SessionLog("Match has ended.");

                        //Needs to notify the client that the match has been closed.
                        Message msg = new Message(MessageType.MATCHHASENDED);
                        if (!SendMessage(msg))
                            SessionLog("ERROR while sending match ended message.");
                    }
                }
            }
        }
    }

    /**
     * Sends a message to the connected client.
     * Will not send message if:
     * - Client already said goodbye
     * - The socket is closed
     * - The server is in the process of shutting down
     * - Too many sending exceptions happened in a row (in which case the session will also close completely)
     *
     * @param msg The message to deliver.
     * @return Returns true if operation was successful. Returns false if it failed.
     */
    public synchronized boolean SendMessage(Message msg) {
        //Check if the client already said goodbye and won't receive messages anymore.
        if (_GoodbyeReceived)
            return false;

        //Once the socket is closed, this session will destroyed soon.
        //Any unsent message (coming from the Match instance) will be disregarded.
        if (_ClientSocket.isClosed())
            return false;

        //When the server initiates shutdown, stop all sending out
        if (_IsServerShutsDown)
            return false;

        //If sending exceptions are over the treshold, then refuse sending.
        if (_SendExceptionsInARowCounter > SharedResources.SRV_MAX_SESSION_EXCEPTION_INAROW) {
            isMajorExceptionOccurred = true;
            return false;
        }


        boolean result = true;
        try {
            _ObjOut.writeObject(msg);
            _ObjOut.flush();
            _SendExceptionsInARowCounter = 0;
        } catch (Exception e) {
            _SendExceptionsInARowCounter++;
            SessionLog("SendMessage ERROR: " + e.getMessage() +
                    " [Message type: " + Integer.toString(msg.getType()) +
                    "]. Threshold status: " + Integer.toString(_SendExceptionsInARowCounter) +
                    "/" + Integer.toString(SharedResources.SRV_MAX_SESSION_EXCEPTION_INAROW));
            result = false;
        }
        return result;
    }

    /**
     * Opens object input and object output streams of the provided socket.
     *
     * @return Returns true if operation was successful. Returns false if not.
     */
    public boolean OpenStreams() {
        boolean result = true;
        try {
            _ObjIn = new ObjectInputStream(_ClientSocket.getInputStream());
            _ObjOut = new ObjectOutputStream(_ClientSocket.getOutputStream());
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * The main client listening method.
     * Listens to the messages sent by the client and
     * follows it up with the appropriate action based on the message's type.
     * <p>
     * Will listen until:
     * - Server shuts down
     * - Client sends goodbye message
     * - Socket gets closed
     * - Too many exceptions happened in a row.
     */
    @Override
    public void run() {
        //Check if streams are available. If not, then end running.
        if (_ObjIn == null || _ObjOut == null)
            return;


        //Streams are ready, wait for requests.
        SessionLog("Session has started. Waiting for messages.");
        Message msg; //Latest received messages will be stored here.


        while (!_ClientSocket.isClosed() && !_IsServerShutsDown && !isMajorExceptionOccurred) {
            try {
                //Read the message object from the stream
                msg = (Message) _ObjIn.readObject();

                //If no exceptions occurred, then zero out the exceptions counter
                _ExceptionsInARowCounter = 0;

                //If Hello message sent, then print out to the console.
                if (msg.getType() == MessageType.HELLO) {
                    SessionLog("Hello message received from the client.");
                    continue;
                }

                //If the client is looking for an opponent to play with on a specific map
                if (msg.getType() == MessageType.LOOKINGFOROPPONENT) {
                    _LastMapRequestMsg = (MessageMapRequest) msg;
                    SessionLog("Map request received from the client.");
                    //Check if this is not a duplicate request. If not, add to the lobby.
                    if (!_Lobby.containsKey(_ClientSocket)) {
                        _Lobby.put(_ClientSocket, _LastMapRequestMsg.get_MapName());
                    }
                    continue;
                }

                //If it is a car status update or crash message, the notify the other player.
                if (msg.getType() == MessageType.INGAMEPOSITIONUPDATE || msg.getType() == MessageType.INGAMECRASH) {
                    if (_CurrentMatch != null) {
                        _CurrentMatch.TransmitMessage(_AssignedPlayerNumber, msg);
                    }
                    continue;
                }


                //If player dropped message arrives then remove the the session from the launch lobby
                if (msg.getType() == MessageType.PLAYERDROPPED) {
                    SessionLog("Player dropped message received from the client.");
                    //Check if the player was in a lobby
                    if (_Lobby.containsKey(_ClientSocket)) {
                        _Lobby.remove(_ClientSocket);
                        _LastMapRequestMsg = null;
                    }
                    continue;
                }

                //If GOODBYE message arrives then remove from the lobby
                //or notify the other player about this player leaving the match.
                //Finally close the socket and session.
                if (msg.getType() == MessageType.GOODBYE) {
                    _GoodbyeReceived = true;

                    //Check if the player was in a lobby
                    if (_Lobby.containsKey(_ClientSocket)) {
                        _Lobby.remove(_ClientSocket);
                        _LastMapRequestMsg = null;
                    }
                    if (_CurrentMatch != null) {
                        _CurrentMatch.PlayerHasLeft(_AssignedPlayerNumber);
                    }

                    //Close connection. This terminates the loop as well.
                    _ClientSocket.close();
                    SessionLog("Goodbye message received.");
                }


            } catch (Exception e) {
                //Errors can be ignored if the server is shutting down or the client sent goodbye already
                // or the socket is already become closed. Write these out to the console.
                boolean isExpected = false;
                isExpected = (isExpected || _IsServerShutsDown);
                isExpected = (isExpected || _GoodbyeReceived);
                isExpected = (isExpected || _ClientSocket.isClosed());
                if (isExpected) {
                    SessionLog("An exception captured during listening to the client. " +
                            "However this was expected and don't need to follow up. Message: " + e.getMessage());
                }

                //If this is an unexpected exception, but still within the threshold: print out the error, but do nothing.
                if (!isExpected && _ExceptionsInARowCounter < SharedResources.SRV_MAX_SESSION_EXCEPTION_INAROW) {
                    _ExceptionsInARowCounter++;
                    SessionLog("Major exception, but within the threshold limit (" +
                            Integer.toString(_ExceptionsInARowCounter) + "/"
                            + Integer.toString(SharedResources.SRV_MAX_SESSION_EXCEPTION_INAROW) + "). " +
                            "Message: " + e.getMessage());
                }

                //If this is an unexpected exception and over the threshold, then print stack trace
                // and notify the opponent if there is.
                if (!isExpected && _ExceptionsInARowCounter >= SharedResources.SRV_MAX_SESSION_EXCEPTION_INAROW) {
                    isMajorExceptionOccurred = true;
                    e.printStackTrace();
                    SessionLog("Error. Too much unexpected exceptions happened during listening to the client. Message: " + e.getMessage());
                    if (_CurrentMatch != null)
                        _CurrentMatch.PlayerHasLeft(_AssignedPlayerNumber);
                }

            }
        }

        //Try to close the client socket if it is not closed yet.
        if (_ClientSocket != null)
            if (!_ClientSocket.isClosed()) {
                SessionLog("Trying to close client socket.");
                try {
                    _ClientSocket.close();
                    SessionLog("Client socket is closed.");
                } catch (IOException e) {
                    SessionLog("Could not close the client socket: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        SessionLog("Session is closed.");
    }


    /**
     * Displays a text in a terminal. Also places the timestamp
     * and the session as the sender. If already in game, then displays the player number too.
     * @param text The text to display in the terminal.
     */
    private void SessionLog(String text) {
        final LocalDateTime now = LocalDateTime.now();
        String time = now.toLocalTime().toString();

        String sender = "(SESSION " + _ClientSocket.getInetAddress().toString();
        if (_AssignedPlayerNumber != -1)
            sender += " - Player " + Integer.toString(_AssignedPlayerNumber) + ". - ): ";
        else
            sender += "): ";

        String msg = time + ", " + sender + text;
        _Terminal.Log(msg);
    }


    /**
     * Called by the server when it shuts down. Notifies the session to ignore exceptions.
     */
    public void ServerShutDownNotification() {
        _IsServerShutsDown = true;
    }


}
