package ModelLayer.Networking.Messages;


import java.io.Serializable;

/**
 * A message (information) sent between a client and a server.
 */
public class Message implements Serializable {

    /**
     * Used by Serializable to identify the object's version
     */
    private static final long serialVersionUID = 1L;

    /**
     * The type of the message.
     */
    protected final int Type;

    /**
     * A message (information) sent between a client and a server.
     *
     * @param messageType The type of the information or request the message represents.
     */
    public Message(int messageType) {
        Type = messageType;
    }

    /**
     * Returns the integer that represents the type of the message.
     *
     * @return The type of the message.
     */
    public int getType() {
        return Type;
    }
}
