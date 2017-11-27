package ViewLayer.Screens.InGameScr;

import ControlLayer.SharedResources;
import ModelLayer.Car;
import ModelLayer.Player;

import javax.swing.*;
import java.awt.*;

/**
 * Builds on a JLabel to display images of a car (in different angles) on the screen.
 * Acts as the view layer of the car representation. Works together with the Car  or CarRemote class in the model layer.
 * Represents one car/player in the game.
 */
public class CarInGameDisplayLabel extends JLabel
{
    /**
     * An array of images of a car with different angles.
     */
    private Image[] _CarImages;

    /**
     * Image of a crashed car. Used when two cars collide with each other.
     */
    private Image _CrashedCarImage;

    /**
     * Index of the currently used car image from the _CarImages array.
     */
    private int _CurrentCarImage;

    /**
     * Currently used Image. (same as retrieving the Image with _CurrentCarImage index from the _CarImages array)
     */
    private ImageIcon _CurrentIcon;

    /**
     * The model layer representation of the car. This calculates the next location/position of this JLabel.
     */
    private Car _CarLogic;


    /**
     * Builds on a JLabel to display images of a car (in different angles) on the screen.
     * Acts as the view layer of the car representation. Works together with the Car class in the model layer.
     * Represents one car/player in the game.
     *
     * @param player The player object this car belongs to.
     */
    public CarInGameDisplayLabel(Player player)
    {
        _CarLogic = player.get_Car();
        _CarLogic.set_CarDisplay(this);

        GetCarImages();
        _CurrentCarImage = 0;
        setSize(SharedResources.CAR_Image_Size_X, SharedResources.CAR_Image_Size_Y);

        _CurrentIcon = new ImageIcon(_CarImages[_CurrentCarImage]);
        setIcon(_CurrentIcon);
        setLocation(0,0);

        setVisible(true);
    }

    /**
     * Asks the model layer car object to provide the car image files from the disk.
     */
    private void GetCarImages()
    {
         _CarImages = _CarLogic.LoadCarImages();
         _CrashedCarImage = _CarLogic.GetCrashedCarImage();
    }


    /**
     * Called when one of the control keys are pressed.
     * The pressing information passed to the model layer car object to be handled.
     * @param keyCode The keycode of the pressed key.
     */
    public void ControlKeyPressed(int keyCode)
    {
        _CarLogic.HandlePressedKey(keyCode);
    }

    /**
     * Called when one of the control keys are released from pressing.
     * The releasing information passed to the model layer car object to be handled.
     * @param keyCode The keycode of the released key.
     */
    public void ControlKeyReleased(int keyCode)
    {
        _CarLogic.HandleReleasedKey(keyCode);
    }

    /**
     * Turns the car either clockwise (right) or counter-clockwise (left)
     * @param isLeftDirection True to turn the car counter-clockwise. False to turn it clockwise.
     * @return Returns an array index of the newly used car image. This matches with the angle in SharedResources.CAR_Simulated_Angle_Values
     */
    public int Turn(boolean isLeftDirection)
    {
        _CurrentCarImage = GetNewCarImageIndexIfTurned(isLeftDirection);
        _CurrentIcon.setImage(_CarImages[_CurrentCarImage]);
        this.setVisible(true);

        return _CurrentCarImage;
    }

    /**
     * Calculates which car image to use after a turn.  Does not turn the car.
     * @param isLeftDirection True to turn the car counter-clockwise. False to turn it clockwise.
     * @return Returns an array index of the turned car. This matches with the angle in SharedResources.CAR_Simulated_Angle_Values
     */
    public int GetNewCarImageIndexIfTurned(boolean isLeftDirection)
    {
        int nImageIndex;
        if(isLeftDirection)
        {
            nImageIndex = _CurrentCarImage - 1;
            if(nImageIndex < 0)
            {
                nImageIndex += _CarImages.length;
            }
        }
        else
        {
            nImageIndex = _CurrentCarImage + 1;
            if(nImageIndex >= _CarImages.length)
            {
                nImageIndex -= _CarImages.length;
            }
        }
        return nImageIndex;
    }


    /**
     * Called each time the timer ticks.
     * Asks the model layer Car object to calculate the new location/angle for the car image.
     */
    public void RefreshForNextFrame()
    {
        _CarLogic.CalculateNextFrame();
    }

    /**
     * Sets the car angle to a specific value. This is used at the start of the game.
     * @param angle The angle value to set the car to. Must be a value from the SharedResources.CAR_Simulated_Angle_Values array.
     */
    public void SetStartImage(int angle)
    {
        int i;
        for (i = 0; i < SharedResources.CAR_Simulated_Angle_Values.length; i++)
        {
            if(SharedResources.CAR_Simulated_Angle_Values[i] == angle)
            {
                break;
            }
        }

        if(i < SharedResources.CAR_Simulated_Angle_Values.length)
        {
            _CurrentCarImage = i;
            _CurrentIcon.setImage(_CarImages[_CurrentCarImage]);
            _CarLogic.set_CurrentAngle(SharedResources.CAR_Simulated_Angle_Values[i]);
            this.setVisible(true);
        }

    }

    /**
     * Returns an image of the car for the Heads Up Display to be displayed.
     * This is the car image with the angle of the first element in SharedResources.CAR_Simulated_Angle_Values array.
     * @return An image of the car. This is one of the in-game car images with the same size too.
     */
    public Image GetFirstCarImageForHUD()
    {
        if(_CarImages != null )
        {
            return _CarImages[0];
        }
        else
        {
            return null;
        }
    }

    /**
     * Changes the current image to a crashed car image.
     * Used when two cars a crashing into each other.
     */
    public void SetImageToCrashedCar()
    {
        _CurrentIcon.setImage(_CrashedCarImage);
        setIcon(_CurrentIcon);
        setVisible(true);
    }


    /**
     * Sets the car angle to the specific index number (of angle images array).
     *
     * @param angleIndex The angle index within the loaded images. Index number, not a degree.
     */
    public void SetImageToSpecificIndex(int angleIndex) {
        _CurrentCarImage = angleIndex;
        _CurrentIcon.setImage(_CarImages[_CurrentCarImage]);
        this.setVisible(true);
    }
}
