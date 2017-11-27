package ModelLayer.Networking.Messages;

import java.io.Serializable;

/**
 * The server answers to the client with this message after it received the MessageMapRequest.
 * This message represents that the server has found an opponent for the player.
 * The server also assigns a player number (either 1 or 2) to the client.
 */
public class MessageMapResponse extends MessageMapRequest implements Serializable {

    /**
     * Player number selected by the server. Either 1 or 2.
     */
    private int _GivenPlayerNumber;

    /**
     * A message (information) sent between a client and a server.
     *
     * @param type The type of the information or request the message represents.
     */
    public MessageMapResponse(int type) {
        super(type);
    }


    public int get_GivenPlayerNumber() {
        return _GivenPlayerNumber;
    }

    public void set_GivenPlayerNumber(int _GivenPlayerNumber) {
        this._GivenPlayerNumber = _GivenPlayerNumber;
    }
}
