package ModelLayer;


import ControlLayer.SharedResources;
import ModelLayer.Networking.Messages.MessageCarStatusUpdate;
import ViewLayer.Screens.InGameScr.CarInGameDisplayLabel;

/**
 * Represents a car on a remote computer.
 * The data for its display is sourced from the server, rather than being controllable at the local computer.
 */
public class CarRemote extends Car {


    /**
     * Connects to the client and asks it to start to listen to status update messages from the server.
     * Also provides this instance to handle (callback) when new information is available.
     */
    public void StartSelfUpdating() {
        if (_Client == null)
            _Client = SharedResources.MainController.get_Client();

        _Client.NotifyMeOfCarUpdates(this);
        _Client.StartListenForIngameUpdates();
    }


    /**
     * Called from the client when the server sends new car status update information.
     * The received information is used for updating this instance's variables.
     *
     * @param msg The car update message sent by the server.
     */
    public void CarUpdateCallback(MessageCarStatusUpdate msg) {
        _CurrentVirtualSpeed = msg.get_VirtualSpeed();
        _CarDisplay.setLocation(msg.get_Location_X(), msg.get_Location_Y());

        //If car changed angle (turned) then turn here too
        if (_CurrentAngle != msg.get_CurrentAngle()) {
            _CurrentAngle = msg.get_CurrentAngle();
            int angleIndex = super.GetAngleIndexFromAngle(msg.get_CurrentAngle());
            _CarDisplay.SetImageToSpecificIndex(angleIndex);
        }

        //If start or stop the acceleration sound effect
        _IsAccellerating = msg.is_IsAccelerating();
        if (_IsAccellerating)
            SharedResources.MainController.get_GameEngine().CarAccelerationOccurred();
        else
            SharedResources.MainController.get_GameEngine().CarAccelerationStopped();

        //Play or stop playing car impact sound
        if (msg.is_CarImpactSoundToPlay())
            SharedResources.MainController.get_GameEngine().CarImpactOccurred();

    }

    /**
     * Called from the client when the server sends the cars crashed message.
     */
    public void CarCrashCallback() {
        SharedResources.MainController.get_GameEngine().CarCrashOccurred();
    }


    /**
     * Sets the associated View Layer representation of the car (CarInGameDisplayLabel)
     *
     * @param _CarDisplay View Layer representation of the car (CarInGameDisplayLabel)
     */
    @Override
    public void set_CarDisplay(CarInGameDisplayLabel _CarDisplay) {
        //Simplifying for a remote car by removing initialisations.
        this._CarDisplay = _CarDisplay;
    }

    /**
     * Returns the current virtual speed, which will be displayed on the HUD
     * @return The cars actual virtual speed.
     */
    @Override
    public int GetVirtualSpeed() {
        //This is sourced from the server instead of being calculated locally.
        return _CurrentVirtualSpeed;
    }


    //
    // Methods that got ignored by overwriting
    //

    @Override
    public void set_CurrentAngle(float _CurrentAngle) {
        //Current angle cannot be changed locally. Do nothing.
    }

    @Override
    public void HandlePressedKey(int keyCode) {
        //Remote car does not react to local key presses. Do nothing.
    }

    @Override
    public void HandleReleasedKey(int keyCode) {
        //Remote car does not react to local key releases. Do nothing.
    }

    @Override
    public void CalculateNextFrame() {
        //Remote car's display change is not triggered by the timer.
        //It is triggered by receiving new data from the server. Do nothing.
    }


}
