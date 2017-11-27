package ModelLayer.Networking.Messages;

import java.io.Serializable;

/**
 * Contains information about the status of the remote car.
 * This can be used by the local application to update the remote car's representation on the screen
 */
public class MessageCarStatusUpdate extends Message implements Serializable {

    /**
     * Represents the current angle of the car.
     */
    private float _CurrentAngle;

    /**
     * The absolute X location of the car on the screen
     */
    private int _Location_X;

    /**
     * The absolute Y location of the car on the screen
     */
    private int _Location_Y;

    /**
     * The actual virtual speed of the car for the Heads Up Display
     */
    private int _VirtualSpeed;

    /**
     * True if the car is currently accelerating. In this case a sound effect may need to be played.
     */
    private boolean _IsAccelerating;

    /**
     * True if a sound effect of car crashing to a non-car object should be played.
     */
    private boolean _CarImpactSoundToPlay;

    /**
     * A message (information) sent between a client and a server.
     *
     * @param type The type of the information or request the message represents.
     */
    public MessageCarStatusUpdate(int type) {
        super(type);
    }


    public float get_CurrentAngle() {
        return _CurrentAngle;
    }

    public void set_CurrentAngle(float _CurrentAngle) {
        this._CurrentAngle = _CurrentAngle;
    }

    public int get_Location_X() {
        return _Location_X;
    }

    public void set_Location_X(int _Location_X) {
        this._Location_X = _Location_X;
    }

    public int get_Location_Y() {
        return _Location_Y;
    }

    public void set_Location_Y(int _Location_Y) {
        this._Location_Y = _Location_Y;
    }

    public int get_VirtualSpeed() {
        return _VirtualSpeed;
    }

    public void set_VirtualSpeed(int _VirtualSpeed) {
        this._VirtualSpeed = _VirtualSpeed;
    }

    public boolean is_IsAccelerating() {
        return _IsAccelerating;
    }

    public void set_IsAccelerating(boolean _IsAccelerating) {
        this._IsAccelerating = _IsAccelerating;
    }

    public boolean is_CarImpactSoundToPlay() {
        return _CarImpactSoundToPlay;
    }

    public void set_CarImpactSoundToPlay(boolean _CarImpactSoundToPlay) {
        this._CarImpactSoundToPlay = _CarImpactSoundToPlay;
    }


}
