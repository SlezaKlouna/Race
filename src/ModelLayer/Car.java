package ModelLayer;

import ControlLayer.CurrentGameSession;
import ControlLayer.SharedResources;
import ModelLayer.FileLoaders.ImageFileLoader;
import ModelLayer.Networking.Client;
import ModelLayer.Networking.Messages.MessageCarStatusUpdate;
import ModelLayer.Networking.Messages.MessageType;
import ViewLayer.Screens.InGameScr.CarInGameDisplayLabel;

import java.awt.*;
import java.util.HashSet;

/**
 * Manages a car's movement and calculates coordinates. Works together with the CarInGameDisplayLabel.
 * Does not deal with the visual/on-screen representation of the car, which is the CarInGameDisplayLabel's job.
 * The car's movement features: <br>
 * - Variable acceleration speeds, based on the current speed and direction (reverse or forward) <br>
 * - Variable speed limits, based on surface (road vs grass) and direction (reverse of forward) <br>
 * - Real speed (pixel/frame rate) to Virtual speed (0-100 mph scale) conversion. <br>
 * - Handling collisions (using the CollisionManager class)<br>
 */
public class Car
{

    /**
     * Represents the current virtual speed to be displayed on the HUD.
     */
    int _CurrentVirtualSpeed;

    /**
     * The current rotation of the car, where 0 means facing North.
     * The angle always matches one of the elements in the SharedResources.CAR_Simulated_Angle_Values array.
     */
    float _CurrentAngle;
    /**
     * The view layer object of the car representation. This JLabel displays the car's image.
     */
    CarInGameDisplayLabel _CarDisplay;
    /**
     * The Client communicates with the server. Used to get or send status updates.
     */
    Client _Client;
    /**
     * True when the car is accellerating, false if not.
     * This is used for turning on or off the accelerate sound effect. This also sent to the remote client.
     */
    boolean _IsAccellerating = false;
    /**
     * The current speed represented as the number of pixel moves on screen between two frame changes.
     * Example: 'speed = 3' means that the car needs to be moved 3 pixels
     * horizontally or vertically from its current position if the car is rotated with 0, 90, 180, 270 angles.
     * (if the car has a different angle, then trigonometric calculation applied to get the exact number)
     */
    private float _Speed;
    /**
     * True if the car is in reverse mode. False if it is in forward or standing mode.
     */
    private boolean _ReverseMode;
    /**
     * True if the car is currently on a grass surface (which impacts its speed limit).
     */
    private boolean _OnGrass;
    /**
     * True if the car hits the wall. This will result in the speed dropping to 0.
     */
    private boolean _LastMoveHitTheWall;
    /**
     * True if the car crashes with other car.
     */
    private boolean _LastMoveHitOtherCar;
    /**
     * The currently displayed car image based on its current angle.
     * This is an index number for the SharedResources.CAR_ImageFile_Angles array.
     */
    private int _CarImageFileIndex = 0; //Select the first car design by default.;
    /**
     * A lists of currently pressed control keys. If a key is released, then it gets removed from the list.
     * This can contain 0-4 values in total.
     */
    private HashSet<Integer> _CurrentlyPressedKeys;
    /**
     * True when car impact to a non-car object. In this case, a sound is being played.
     * This information is sent to the remote client.
     */
    private boolean _CarImpactSoundToPlay = false;

    /**
     * Becomes true when two car crashes. This will sent to the remote client.
     */
    private boolean _IsCarCrashed = false;


    /**
     * Sets the angle of the car.
     * @param _CurrentAngle The new angle. Must match with an element of the SharedResources.CAR_Simulated_Angle_Values array.
     */
    public void set_CurrentAngle(float _CurrentAngle)
    {
        this._CurrentAngle = _CurrentAngle;
    }

    /**
     * Sets the currently used car image file.
     * @param _CarImageFileIndex This is an index number of the SharedResources.CAR_Simulated_Angle_Values array.
     */
    public void set_CarImageFileIndex(int _CarImageFileIndex) {
        this._CarImageFileIndex = _CarImageFileIndex;
    }

    /**
     * Retrieves the in-game images of a car for all the angles.
     * @return Returns an array of images, where each image is the same car but with a rotated angle.
     */
    public Image[] LoadCarImages()
    {
        return ImageFileLoader.LoadCarImages(_CarImageFileIndex);
    }

    /**
     * Handles when a controlling key pressed during the game.
     * @param keyCode The keycode of the pressed key.
     */
    public void HandlePressedKey(int keyCode) {
        //If accelerate (up) button pressed then register it with the pressed keys
        if(keyCode == SharedResources.GCS_ControlKeys_Player_1[0] || keyCode == SharedResources.GCS_ControlKeys_Player_2[0] ) {
            _CurrentlyPressedKeys.add(keyCode); //No duplicates, as this is a hash set
            return;
        }
        //If break (backward) button pressed then register it with the pressed keys
        if(keyCode == SharedResources.GCS_ControlKeys_Player_1[1] || keyCode == SharedResources.GCS_ControlKeys_Player_2[1] ) {
            _CurrentlyPressedKeys.add(keyCode); //No duplicates, as this is a hash set
            return;
        }
        //If turning left button pressed call the appropriate handling method
        if(keyCode == SharedResources.GCS_ControlKeys_Player_1[2] || keyCode == SharedResources.GCS_ControlKeys_Player_2[2] ) {
            HandleTurnButton(keyCode, true);
            return;
        }
        //If turning right button pressed call the appropriate handling method
        if(keyCode == SharedResources.GCS_ControlKeys_Player_1[3] || keyCode == SharedResources.GCS_ControlKeys_Player_2[3] ) {
            HandleTurnButton(keyCode,false);
        }
    }


    /**
     * Handles the Key release event by removing the key from the list of currently pressed keys.
     * @param keyCode The keycode of the released key.
     */
    public void HandleReleasedKey(int keyCode)
    {
        _CurrentlyPressedKeys.remove(keyCode);
    }

    /**
     * Turns the car if turning won't cause collision with other object or car.
     *
     * @param key    The pressed turning control key's keycode.
     * @param isLeft True if the car needs to turn left (counter clockwise). False to turn right (clockwise).
     */
    private void HandleTurnButton(int key, Boolean isLeft)
    {
        if (!_CurrentlyPressedKeys.contains(key)) {
            if (!WouldTurnCauseCollision(_CarDisplay.GetNewCarImageIndexIfTurned(isLeft)))
            {
                _CurrentlyPressedKeys.add(key);
                int newAngleIndex = _CarDisplay.Turn(isLeft); //Asks the JLabel to change to a new image.
                _CurrentAngle = SharedResources.CAR_Simulated_Angle_Values[newAngleIndex];
                _CarImpactSoundToPlay = false;
            }
            else {
                //Notify the GameEngine controller about the collision, so it can play sound if needed.
                SharedResources.MainController.get_GameEngine().CarImpactOccurred();
                _CarImpactSoundToPlay = true;
            }
        }
    }

    /**
     * Check if the turning of the car would cause collision with other objects or car.
     * This does not turn the car, only checks.
     * @param supposedNewAngleIndex This is an index number of the SharedResources.CAR_Simulated_Angle_Values array.
     * @return Returns true if the car would collide using the new angle.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean WouldTurnCauseCollision(int supposedNewAngleIndex)
    {
        boolean result;
        Rectangle currentLoc = _CarDisplay.getBounds();

        //Check for crashing into other cars with the supposed turn.
        result = CurrentGameSession.get_CollisionManager().isCollidingWithOtherCars(this, currentLoc, supposedNewAngleIndex);
        if (result)
            return true;

        //Check for hitting edges/trees with a supposed turn
        result = CurrentGameSession.get_CollisionManager().IsCollidingWithImpassable(currentLoc, supposedNewAngleIndex);
        return result;
    }

    /**
     * Called with every new frame.
     * Updates the speed based on the current speed, pressed buttons, collisions, terrain.
     * Based on the updated speed calculates the new location and sends it back to the JLabel for refreshing.
     */
    public void CalculateNextFrame()
    {
        UpdateSpeed();
        CalculateNewLocation();

        if (!_IsCarCrashed)
            SendStatusUpdateToTheServer();
    }

    /**
     * Sends status updates to the server using the client.
     */
    private void SendStatusUpdateToTheServer() {
        if (_Client == null)
            _Client = SharedResources.MainController.get_Client();

        MessageCarStatusUpdate update = new MessageCarStatusUpdate(MessageType.INGAMEPOSITIONUPDATE);
        update.set_CurrentAngle(_CurrentAngle);
        update.set_Location_X(_CarDisplay.getX());
        update.set_Location_Y(_CarDisplay.getY());
        update.set_VirtualSpeed(_CurrentVirtualSpeed);
        update.set_IsAccelerating(_IsAccellerating);
        update.set_CarImpactSoundToPlay(_CarImpactSoundToPlay);
        _Client.SendStatusUpdateMessage(update);
    }

    /**
     * Calculates the new location for the car based on
     * its current position, current speed and direction (angle)
     */
    private void CalculateNewLocation()
    {
        Point loc = _CarDisplay.getLocation();

        //Check if there is no diagonal movement required, to simplify calculation
        switch (Math.round(_CurrentAngle)) {
            //Car facing upwards. Moving up on Y axis. (opposite if reversed)
            case 0:
                loc.translate(0,ReverseModeCheck(0 - Math.round(_Speed) ));
                break;

            //Car facing downwards. Moving down on Y axis. (opposite if reversed)
            case 180:
                loc.translate(0, ReverseModeCheck(Math.round(_Speed)));
                break;

            //Car facing towards west. Moving up on X axis. (opposite if reversed)
            case 90:
                loc.translate(ReverseModeCheck(Math.round(_Speed)),0);
                break;

            //Car facing towards east. Moving down on Y axis. (opposite if reversed)
            case 270:
                loc.translate(ReverseModeCheck(0 - Math.round(_Speed)), 0);
                break;
            default:
                //Use trigonometric functions to calculate the new location
                loc = NewLocationByTrigonometric(loc);
                break;
        }

        CheckForCollisionAndMove(loc); //Send the new proposed location for collision checking.
    }

    /**
     * Checks for collisions based on the provided new location as a parameter.
     * If no collision would occur on the new location, then moves the car to the location (by asking the JLabel).
     * If collision would occur, then handles the situation based on the type of the collision: <br>
     * - If the car collides with grass, then this will be noted and speed changes might occur with the next frame. <br>
     * - If the car collides with other car, the GameEngine will be notified
     * - If the car hits the edge of the map/tree, then zeroes down the speed and will not move the car.
     *
     * @param nLocation The new location where the car needs to be moved and collision needs to be checked.
     */
    private void CheckForCollisionAndMove(Point nLocation) {
        Rectangle r = GetBoundsAsRectangle(nLocation);

        //Checking for collision with other car.
        _LastMoveHitOtherCar = CurrentGameSession.get_CollisionManager().isCollidingWithOtherCars(this, r, GetAngleIndexFromAngle(_CurrentAngle));
        if (_LastMoveHitOtherCar) {
            _Speed = 0;
            _IsCarCrashed = true;
            _Client.SendCarCrashedMessage(); // Notify the remote client.
            SharedResources.MainController.get_GameEngine().CarCrashOccurred(); //Notify game engine.
            return;
        }

        //Check if the car would hit a wall/rock and the speed needed to be zeroed
        _LastMoveHitTheWall = CurrentGameSession.get_CollisionManager().IsCollidingWithImpassable(r, GetAngleIndexFromAngle(_CurrentAngle));
        if (!_LastMoveHitTheWall) {
            _CarImpactSoundToPlay = false;
            _CarDisplay.setLocation(nLocation); //No collision. Set the JLabel about the new location.
        } else {
            //Hitting the edge of the racing track or a tree.
            _Speed = 0;
            SharedResources.MainController.get_GameEngine().CarImpactOccurred(); //Notify game engine.
            _CarImpactSoundToPlay = true;
            return;
        }

        //Check if the car hits a grass area and slowing down would be needed from the next frame
        _OnGrass = CurrentGameSession.get_CollisionManager().IsCollidingWithGrass(r, GetAngleIndexFromAngle(_CurrentAngle));
    }

    /**
     * Returns the current angle represented as an index of the SharedResources.CAR_Simulated_Angle_Values array.
     * @return The current angle represented as an index of the SharedResources.CAR_Simulated_Angle_Values array.
     */
    public int GetCurrentAngleIndex()
    {
        return GetAngleIndexFromAngle(_CurrentAngle);
    }

    /**
     * Searches within the SharedResources.CAR_Simulated_Angle_Values.length array to find the angle in the parameter.
     * @param angle The angle to be found in the array.
     * @return The index number where the angle can be found in the array.
     */
    int GetAngleIndexFromAngle(float angle) {
        //This expects the value to be found 100% of the times.
        int i;
        for(i = 0; i < SharedResources.CAR_Simulated_Angle_Values.length; i++)
        {
            if (SharedResources.CAR_Simulated_Angle_Values[i] == angle)
                return i;
        }
        return i;
    }

    /**
     * Retrieves the car's current location as a rectangle. This is based on the image file.
     * @return Rectangle representing car's on-screen location. The size of the rectangle matches the image's size.
     */
    public Rectangle GetBoundsAsRectangle() { return _CarDisplay.getBounds();
    }

    /**
     * Retrieves a car's hypothetical location as a rectangle.
     * This is done by creating a rectangle with the same size as the car's image and
     * placing it into the location specified in the parameter.
     * @param startLocation The top left coordinate for the rectangle.
     * @return Returns a rectangle car sized rectangle at the startLocation point.
     */
    private Rectangle GetBoundsAsRectangle(Point startLocation)
    {
        return new Rectangle(startLocation.x, startLocation.y, SharedResources.CAR_Image_Size_X ,SharedResources.CAR_Image_Size_Y);
    }


    /**
     * Inverts the newSpeed parameter's value to negative if the reverse mode is on.
     * @param newSpeed The speed to check and change against reverse mode.
     * @return Returns newSpeed if not in reverse mode. Returns (0-newSpeed) if reverse mode is true.
     */
    private int ReverseModeCheck(int newSpeed)
    {
        if (!_ReverseMode)
        {
            return newSpeed;
        }
        else
        {
            return (0 - newSpeed);
        }
    }

    /**
     * The Hypotenuse of the triangle (the speed) and the angle are known.
     * Calculating the Opposite side of the triangle (X axis) by using Sine.
     * Calculating the Adjacent side of the triangle (Y axis) by using Cosine
     * @param currentLoc The location where the car would move from.
     * @return Returns the new location based on the current speed and angle.
     */
    private Point NewLocationByTrigonometric(Point currentLoc)
    {
        //The angle in radians
        double angleRad = Math.toRadians(_CurrentAngle);

        //sin * speed = X axis
        double sin = Math.sin(angleRad);
        double dx = _Speed * sin;

        //cos * speed = Y axis
        double cos = Math.cos(angleRad);
        double dy = 0- (_Speed * cos);

        currentLoc.translate(ReverseModeCheck((int) Math.round(dx)), ReverseModeCheck((int) Math.round(dy)));
        return currentLoc;
    }

    /**
     * Changes the current speed based on the currently pressed buttons.
     */
    private void UpdateSpeed()
    {
        boolean isForwardPressed = _CurrentlyPressedKeys.contains(SharedResources.GCS_ControlKeys_Player_1[0]) || _CurrentlyPressedKeys.contains(SharedResources.GCS_ControlKeys_Player_2[0]);
        boolean isBackwardPressed = _CurrentlyPressedKeys.contains(SharedResources.GCS_ControlKeys_Player_1[1]) || _CurrentlyPressedKeys.contains(SharedResources.GCS_ControlKeys_Player_2[1]);
        boolean isBackAndForwardPressed = isForwardPressed && isBackwardPressed;
        boolean isNonOfBackOrForwardPressed = !(isForwardPressed || isBackwardPressed);
        boolean isCarStopped = (_Speed == 0);

        if(isBackAndForwardPressed || isNonOfBackOrForwardPressed)
        {
            SlowDown();
            return;
        }

        if (isForwardPressed && !_ReverseMode)
        {
            Accelerate();
            return;
        }

        if (isForwardPressed && _ReverseMode)
        {
            if (isCarStopped)
            {
                _ReverseMode = false;
                Accelerate();
                return;
            }
            else
            {
                SlowDown();
                return;
            }
        }

        if (isBackwardPressed && _ReverseMode)
        {
            Accelerate(true, _OnGrass);
            return;
        }

        if (isBackwardPressed && !_ReverseMode)
        {
            if (isCarStopped)
            {
                _ReverseMode = true;
                Accelerate(true, _OnGrass);
            }
            else
            {
                SlowDown();
            }
        }
    }

    /**
     * Accelerate the car (changes speed) using variable rate acceleration.
     * Checks for speed limit and slows down if overreached.
     * Picks the slowest/lowest speed limit based the input parameters.
     * @param useReverseSpeedLimit True to use a reverse (slower) speed limit. False to use standard speed limit.
     * @param useGrassSpeedLimit True to use a off-road (slower) speed limit. False to use standard speed limit.
     */
    private void Accelerate(boolean useReverseSpeedLimit, boolean useGrassSpeedLimit)
    {
        //Select different limit for reversing and forwarding
        float speedLimit;
        if (!useReverseSpeedLimit)
            speedLimit = SharedResources.GCS_Maximum_Car_Speed;
        else
            speedLimit = SharedResources.GCS_Maximum_Car_Reverse_Speed;

        //Set different speed if the car is on grass. This overrides the reverse/forward speed (and it is the slowest)
        if (useGrassSpeedLimit)
            speedLimit = SharedResources.GCS_Maximum_Car_Grass_Speed;

        //If the limit is reached, no reason to accelerate
        if (_Speed == speedLimit)
        {
            SharedResources.MainController.get_GameEngine().CarAccelerationStopped();
            _IsAccellerating = false;
            return;
        }

        //Accelerates if speed limit is not hit
        if(_Speed < speedLimit)
        {
            //If the car is just starting, give a fix boost
            if (_Speed == 0) {
                _Speed = SharedResources.GCS_Maximum_Car_Speed * SharedResources.GCS_Car_ColdStart_Acceleration_Percentage;
                SharedResources.MainController.get_GameEngine().CarAccelerationOccurred();
                _IsAccellerating = true;
            }
            //Radical acceleration on low speeds
            else if (_Speed < (SharedResources.GCS_Maximum_Car_Speed * SharedResources.GCS_Car_RadicalAcceleration_SpeedLimit)) {
                _Speed *= SharedResources.GCS_Car_RadicalAcceleration_Ratio;
                SharedResources.MainController.get_GameEngine().CarAccelerationOccurred();
                _IsAccellerating = true;
            }
            //Normal acceleration on medium speeds
            else if (_Speed < (SharedResources.GCS_Maximum_Car_Speed * SharedResources.GCS_Car_NormalAcceleration_SpeedLimit)) {
                _Speed *= SharedResources.GCS_Car_NormalAcceleration_Ratio;
                SharedResources.MainController.get_GameEngine().CarAccelerationOccurred();
                _IsAccellerating = true;
            }
            //Slow acceleration on high speeds
            else {
                _Speed *= SharedResources.GCS_Car_HighSpeedAcceleration_Ratio;
                SharedResources.MainController.get_GameEngine().CarAccelerationOccurred();
                _IsAccellerating = true;
            }

            //preventing over accelerating speed limit
            if(_Speed > speedLimit)
            {
                _Speed = speedLimit;
                SharedResources.MainController.get_GameEngine().CarAccelerationStopped();
                _IsAccellerating = false;
            }
        }

        //If currently the speed is higher then allowed, then slow down (e.g. from road to grass)
        if(_Speed > speedLimit)
        {
            SharedResources.MainController.get_GameEngine().CarAccelerationStopped();
            _IsAccellerating = false;
            SlowDown();
        }
    }


    /**
     * Accelerates a forwarding car using variable rate acceleration.
     */
    private void Accelerate()
    {
        Accelerate(false, _OnGrass);
    }


    /**
     * Slows down the car by reducing the _Speed. If the _Speed drops below the threshold, the car gets stopped.
     */
    private void SlowDown()
    {
        _IsAccellerating = false;
        //Lower the speed, based on current speed.
        if(_Speed <= SharedResources.GCS_CarNoAcceleration_Stop_Threshold)
        {
            _Speed = 0;
            _ReverseMode = false;
        }
        else
        {
            _Speed *= SharedResources.GCS_Car_NoAcceleration_Slowdown_Ratio;
        }
    }

    /**
     * Retrieves the JLabel associated with this car object.
     * @return The JLabel associated with this car object.
     */
    public CarInGameDisplayLabel get_CarDisplay()
    {
        return _CarDisplay;
    }


    /**
     * Associates the JLabel with this car object.
     * Also resets to the default values.
     * @param _CarDisplay The JLabel to cooperate with.
     */
    public void set_CarDisplay(CarInGameDisplayLabel _CarDisplay) {
        this._CarDisplay = _CarDisplay;
        _ReverseMode = false;
        _OnGrass = false;
        _LastMoveHitTheWall = false;
        _LastMoveHitOtherCar = false;
        _Speed = 0;
        _CurrentAngle = 0;
        _CurrentlyPressedKeys = new HashSet<>(20); //Leaving enough space for the possible keys
    }

    /**
     * Returns the current speed of the car.
     * This speed is not an actual speed, but rather a visualised speed on a scale.
     * @return The current virtual speed, which is larger or equal to 0.
     */
    public int GetVirtualSpeed()
    {
        /*
      The ratio to be used when converting actual speed (pixel/coordinate change) to virtual speed (0-100mph).
     */
        float _VirtualSpeedRatio = SharedResources.GCS_Car_Virtual_Speed_Max / SharedResources.GCS_Maximum_Car_Speed;
        int result = (int)(_Speed * _VirtualSpeedRatio);

        //Allowing to reach virtual max speed (removes rounding inaccuracy)
        if(_Speed >= SharedResources.GCS_Maximum_Car_Speed)
            result = SharedResources.GCS_Car_Virtual_Speed_Max;

        //Show virtual zero speed when the car hit a wall
        if (_LastMoveHitTheWall)
            result = 0;

        //When the speed is low number, the car will not be moved as the
        //speed is not enough to move it by 1 pixel. However, the actual value of the
        // _Speed variable is above 0.
        // This inconsistency should be masked from the HUD, otherwise a still car will be shown with speed.
        if (_Speed <= SharedResources.HUD_StillCar_Speed_VirtualValue_Masking_Threshold)
            result = 0;

        _CurrentVirtualSpeed = result;
        return result;
    }

    /**
     * Retrieves an image from disk representing a crashed car.
     * @return Retrieves an image from disk representing a crashed car.
     */
    public Image GetCrashedCarImage()
    {
        return ImageFileLoader.LoadCrashedCarImage();
    }
}
