package ModelLayer.Networking.Messages;

import java.io.Serializable;

/**
 * Sent by the client to the server to request an opponent to play with.
 * The Server puts the client into a lobby as a response to this.
 */
public class MessageMapRequest extends Message implements Serializable {

    /**
     * The name of the map where the client wants to play on.
     * An another client for the same map needs to be found by the server.
     */
    protected String _MapName;

    /**
     * The car type (design) index the user selected to play with.
     * This will be used by the other client to display the appropriate design for the opponent.
     */
    protected int _CarImageFileIndex;

    /**
     * A message (information) sent between a client and a server.
     *
     * @param type The type of the information or request the message represents.
     */
    public MessageMapRequest(int type) {
        super(type);
    }

    public String get_MapName() {
        return _MapName;
    }

    public void set_MapName(String _MapName) {
        this._MapName = _MapName;
    }

    public int get_CarImageFileIndex() {
        return _CarImageFileIndex;
    }

    public void set_CarImageFileIndex(int _CarImageFileIndex) {
        this._CarImageFileIndex = _CarImageFileIndex;
    }
}
