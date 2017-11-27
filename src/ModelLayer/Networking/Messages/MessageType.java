package ModelLayer.Networking.Messages;

/**
 * Used by the Networking classes.
 * This describes the type of message received and therefore allows proper casting.
 * This class is used like an enumeration instead of an actual enumeration. The reason for this is that
 * the serialization process handles enumerations inefficiently and also found to be prone to errors.
 */
public class MessageType {
    /**
     * First message between server and client. Sent straight after a socket opened.
     */
    public static final int HELLO = 0;
    /**
     * Represents the last message between the server and client. After this, the socket can be closed.
     */
    public static final int GOODBYE = 1;
    /**
     * Client sends this message to the server indicating that it looks for an another player to play with.
     */
    public static final int LOOKINGFOROPPONENT = 2;
    /**
     * Cancel a LOOKINGFOROPPONENT request.
     */
    public static final int CANCELLOOKINGFOROPPONENT = 3;
    /**
     * Server responds to the client when it finds an opponent with the same map interest.
     * Represents the fact that the game can be started.
     */
    public static final int OPPONENTFOUNDSTARTGAME = 4;
    /**
     * Sent during the ongoing game. The player provides its updated speed and position.
     */
    public static final int INGAMEPOSITIONUPDATE = 6;
    /**
     * Sent during the ongoing game. Sent when one car crashes with the other one. Represents end of the game.
     */
    public static final int INGAMECRASH = 7;
    /**
     * Sent during the ongoing game. When one of the players drops out (exits the game) the other one gets notified.
     */
    public static final int PLAYERDROPPED = 8;
    /**
     * When the match has ended for unspecified reason.
     */
    public static final int MATCHHASENDED = 9;
    /**
     * When server shuts down, tries to send this message to the clients.
     */
    public static final int SERVERDOWN = 10;
}
