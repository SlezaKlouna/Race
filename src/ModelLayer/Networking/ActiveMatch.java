package ModelLayer.Networking;

import ControlLayer.SharedResources;
import ModelLayer.Networking.Messages.Message;
import ModelLayer.Networking.Messages.MessageMapResponse;
import ModelLayer.Networking.Messages.MessageType;
import ViewLayer.Screens.ServerScr.LogTerminal;

import java.net.Socket;
import java.time.LocalDateTime;


/**
 * Represents an ongoing match between to players (sessions).
 * The two players (sessions) are passing the messages to each other using this object.
 * This object also watches out for the end of the game and notifies the server when it has ended.
 */
class ActiveMatch {

    /**
     * Represents this match's name (ID). This is given by the server and serves only logging purpose.
     */
    private final int _MatchID;
    /**
     * Terminal to display logs.
     */
    private final LogTerminal _Terminal;
    /**
     * Socket towards player 1
     */
    private final Socket _SocketPlayer1;
    /**
     * Socket towards player 2
     */
    private final Socket _SocketPlayer2;
    /**
     * The only instance of the server object. This match belongs to the server.
     * Server gets called back when the match has ended.
     */
    private final Server _ServerInstance;
    /**
     * Name of the map (e.g. "Easy")
     */
    private final String _MapName;
    /**
     * Represents if the Sessions are ready to start the game.
     * When both of them are true, the game start message gets sent to the game.
     */
    private boolean _ReadyToStartPlayer1 = false;
    private boolean _ReadyToStartPlayer2 = false;
    /**
     * Represents the session assigned to Socket Player 1.
     */
    private Session _SessionPlayer1;
    /**
     * The selected car image file index for player 1.
     */
    private int _SelectedCarIndexPlayer1 = 0;
    /**
     * Represents if Player 1 left the game.
     */
    private boolean _IsLeftTheGamePlayer1 = false;
    /**
     * Represents the session assigned to Socket Player 2.
     */
    private Session _SessionPlayer2;
    /**
     * The selected car image file index for player 1.
     */
    private int _SelectedCarIndexPlayer2 = 0;
    /**
     * Represents if Player 2 left the game.
     */
    private boolean _IsLeftTheGamePlayer2 = false;
    /**
     * Prevents sending car crash message to be transmitted twice.
     * Car crash messages are transmitted until this is smaller than 1;
     */
    private volatile int _CarCrashMessageCounter = 0;

    /**
     * Becomes true when both players received the game start comment in form of a MessageMapResponse message.
     * Until this becomes true, the players cannot exchange data with each other.
     */
    private volatile boolean _IsGameStartMsgSentOutToBothPlayers = false;


    /**
     * Sends message to both of the participant clients as both of the Sessions became ready.
     * This is called by the MatchIsReadyToStart .
     */
    private final Runnable SendStartGameMessages = () ->
    {
        MessageMapResponse msgToP1 = new MessageMapResponse(MessageType.OPPONENTFOUNDSTARTGAME);
        MessageMapResponse msgToP2 = new MessageMapResponse(MessageType.OPPONENTFOUNDSTARTGAME);

        //Telling the server assigned player number.
        msgToP1.set_GivenPlayerNumber(SharedResources.PLAYER_1);
        msgToP2.set_GivenPlayerNumber(SharedResources.PLAYER_2);

        //Telling the information of the opponents selected car type (inverse assignment)
        msgToP1.set_CarImageFileIndex(_SelectedCarIndexPlayer2);
        msgToP2.set_CarImageFileIndex(_SelectedCarIndexPlayer1);

        _SessionPlayer1.SendMessage(msgToP1);
        _SessionPlayer2.SendMessage(msgToP2);
        _IsGameStartMsgSentOutToBothPlayers = true;
    };



    /**
     * Constructor.
     * Represents an ongoing match between to players (sessions).
     * The two players (sessions) are passing the messages to each other using this object.
     * This object also watches out for the end of the game and notifies the server when it has ended.
     *
     * @param _SocketPlayer1 The socket that is used by Player 1
     * @param _SocketPlayer2 The socket that is used by Player 2
     * @param _MapName       The name of the map (e.g.: "Easy")
     * @param matchID        The name of this match (as an incremental integer). Only serves logging purposes.
     * @param serverInstance The only instance of the server object that hosts all the matches. This will be called back when the match has ended.
     */
    public ActiveMatch(Socket _SocketPlayer1, Socket _SocketPlayer2, String _MapName, int matchID, Server serverInstance) {
        this._SocketPlayer1 = _SocketPlayer1;
        this._SocketPlayer2 = _SocketPlayer2;
        this._MapName = _MapName;
        _MatchID = matchID;
        _ServerInstance = serverInstance;
        _Terminal = serverInstance.getTerminal();
    }

    /**
     * Tells if the player (socket) in parameter is in this match
     *
     * @param player The player (socket)
     * @return Returns true if the player in parameter is one of the two players.
     */
    public boolean HasThisPlayer(Socket player) {
        return (_SocketPlayer1.equals(player) || _SocketPlayer2.equals(player));
    }

    /**
     * Checks if the socket in the parameter is assigned to any of the players.
     *
     * @param socket The socket to check against the players.
     * @return Returns 1 if the socket is assigned to player 1. Returns 2 if assigned to player 2. Returns -1 if unknown socket.
     */
    public int WhichPlayerNumberIsThisSocket(Socket socket) {
        if (socket.equals(_SocketPlayer1))
            return SharedResources.PLAYER_1;

        if (socket.equals(_SocketPlayer2))
            return SharedResources.PLAYER_2;

        return -1;
    }

    /**
     * Assigns a socket to the belonging session.
     *
     * @param playerNumer The player number to make the assignment to. Either 1 or 2.
     * @param session     The related socket
     */
    public void AssignSessionToSocket(int playerNumer, Session session) {
        if (playerNumer == SharedResources.PLAYER_1)
            _SessionPlayer1 = session;

        if (playerNumer == SharedResources.PLAYER_2)
            _SessionPlayer2 = session;
    }

    /**
     * This is called by both of the participating session when they are ready.
     * Once both session called this method it will initiate sending message to the clients about game start
     *
     * @param selectedCarIndex The car type (design) index number of the player.
     * @param playerNumber The player's assigned number. (either 1 or 2)
     */
    public synchronized void MatchIsReadyToStart(int playerNumber, int selectedCarIndex) {
        if (playerNumber == SharedResources.PLAYER_1) {
            _ReadyToStartPlayer1 = true;
            _SelectedCarIndexPlayer1 = selectedCarIndex;
        }

        if (playerNumber == SharedResources.PLAYER_2) {
            _ReadyToStartPlayer2 = true;
            _SelectedCarIndexPlayer2 = selectedCarIndex;
        }

        //Both players are ready to start. Send start mesages on a separate thread.
        if (_ReadyToStartPlayer1 && _ReadyToStartPlayer2) {
            MatchLog("Game is starting. Messaging both players.");
            Thread messageSender = new Thread(SendStartGameMessages);
            messageSender.start();
        }
    }

    /**
     * Sends a message received from one player to the another using a new thread.
     * Counts Game Crash messages prevents status update or crash message sending after the first
     * crash message has been sent.
     * Only sends messages once both players are informed about the game is being started.
     *
     * @param assignedPlayerNumber The sender player session
     * @param msg                  The received message
     */
    public void TransmitMessage(int assignedPlayerNumber, Message msg) {
        if (_IsGameStartMsgSentOutToBothPlayers) {
            if (_CarCrashMessageCounter < 1) {
                MyDispatcher dispatcher = null;
                if (assignedPlayerNumber == SharedResources.PLAYER_1) {
                    dispatcher = new MyDispatcher(msg, _SessionPlayer2);
                }

                if (assignedPlayerNumber == SharedResources.PLAYER_2) {
                    dispatcher = new MyDispatcher(msg, _SessionPlayer1);
                }

                if (dispatcher != null)
                    dispatcher.start();
            }

            if (msg.getType() == MessageType.INGAMECRASH) {
                _CarCrashMessageCounter++;
                MatchLog("Crash message received from player " + Integer.toString(assignedPlayerNumber));
            }
        }
    }


    /**
     * If one of the players has left, then it tries to notify the other opponent.
     * If both players leave the game, then the server gets notified that the match has been ended.
     *
     * @param assignedPlayerNumber The player number of the player who left.
     */
    public synchronized void PlayerHasLeft(int assignedPlayerNumber) {

        //If player 1 dropped
        if (assignedPlayerNumber == SharedResources.PLAYER_1) {
            _IsLeftTheGamePlayer1 = true;
            //Notify player 2, if he is not out yet
            if (!_IsLeftTheGamePlayer2) {
                //Notify about leaving only if car crash notification did not happen (it is unnecessary then)
                if (_CarCrashMessageCounter < 1) {
                    MatchLog("Player 1 has left the game. Notifying player 2.");
                    Message msg = new Message(MessageType.PLAYERDROPPED);
                    Thread droppedThread = new Thread(new MyDispatcher(msg, _SessionPlayer2));
                    droppedThread.start();
                }
            }
        }

        //If player 2 dropped
        if (assignedPlayerNumber == SharedResources.PLAYER_2) {
            _IsLeftTheGamePlayer2 = true;

            //Notify player 1, if he is not out yet
            if (!_IsLeftTheGamePlayer1) {
                //Notify about leaving only if car crash notification did not happen (it is unnecessary then)
                if (_CarCrashMessageCounter < 1) {
                    MatchLog("Player 2 has left the game. Notifying player 1.");
                    Message msg = new Message(MessageType.PLAYERDROPPED);
                    Thread droppedThread = new Thread(new MyDispatcher(msg, _SessionPlayer1));
                    droppedThread.start();
                }
            }
        }


        //If both players left the match, then call back the server to destroy this instance.
        if (_IsLeftTheGamePlayer1 && _IsLeftTheGamePlayer2) {
            MatchLog("Both players have left the game.");
            _ServerInstance.AMatchHasAnded(this, _MatchID);
        }
    }

    /**
     * Displays a text in a terminal. Also places the timestamp and the session as the sender.
     *
     * @param text The text to display in the terminal.
     */
    private void MatchLog(String text) {
        final LocalDateTime now = LocalDateTime.now();
        String time = now.toLocalTime().toString();

        final String sender = "MATCH " + _MatchID + ": ";

        String msg = time + ", " + sender + text;
        _Terminal.Log(msg);
    }

    /**
     * A small inner class used to send one message on a separate thread.
     */
    private class MyDispatcher extends Thread {
        final Message _ToDeliver;
        final Session _Carrier;

        /**
         * A small inner class used to send one message on a separate thread.
         *
         * @param msg     The message to be send
         * @param Carrier The session (socket) to deliver the message to.
         */
        public MyDispatcher(Message msg, Session Carrier) {
            _ToDeliver = msg;
            _Carrier = Carrier;
        }

        public void run() {
            _Carrier.SendMessage(_ToDeliver);
        }
    }

}
