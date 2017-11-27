package ModelLayer.FileLoaders;

import ControlLayer.SharedResources;
import sun.tools.jar.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Loads in files as Images.
 */
public class ImageFileLoader
{

    /**
     * Used as a cache: This prevents reading the same crash image file from disk twice for both cars.
     */
    private static Image _Cache_Crashed_Car = null;

    /**
     * Loads the background image for the main menu. Returns null in case of error.
     * @return The background image for the main menu. Also could be used on tha launch screen.
     */
    public static Image LoadBackgroundImage()
    {
        return ImgFileRead(SharedResources.MMS_BackgroundImageFile);
    }

    /**
     * Return the image file containing the keyboard layout for the player.
     *
     * @return Returns the image representing the control keys.
     */
    public static Image LoadKeyboardLayoutImage()
    {
        Image result;
        String FileName = SharedResources.CLI_LayoutImage_FilePath + SharedResources.CLI_ImageFileName_NoSubFix + Integer.toString(SharedResources.PLAYER_1) + SharedResources.CLI_ImageFile_FileExtension;
        result = ImgFileRead(FileName);
        return result;
    }

    /**
     * Loads the maps thumbnails. Used on the Launch Screen when user selects map.
     * @param mapName The name of the map must be match with an element of the SharedResources.MSP_Maps array to be consistent with the file name on disk.
     * @return Returns the image representing the thumbnail view of a selectable map.
     */
    public static Image LoadMapSelectionImage(String mapName)
    {
        Image result;
        String FileName = SharedResources.MSP_MapImage_FilePath + SharedResources.MSP_ImageFileName_NoNamePrefix + mapName + SharedResources.MSP_ImageFile_FileExtension;
        result = ImgFileRead(FileName);
        return result;
    }

    /**
     * Loads the thumbnail of a car. Used on the Launch Screen when user selects a car.     *
     * @param carId An index number starting from 0. This is used as a filename prefix.
     * @return Returns the thumbnail image of the requested car.
     */
    public static Image LoadCarImageFromFileForLaunchScreen(int carId)
    {
        Image result;
        String fileName = SharedResources.CSP_Selectable_Car_Image_FileNamePath + Integer.toString(carId) + SharedResources.CSP_Selectable_Car_Image_FileName_NoPrefix ;
        result = ImgFileRead(fileName);
        return result;
    }

    /**
     * Loads the arrow icon used in the LaunchScreen when selecting car or map.
     * @param LeftToRightFacing True to load the arrow facing from left to right. False for the opposite direction.
     * @return Returns the image of the arrow icon with the selected direction.
     */
    public static Image LoadSelectorArrowImage(boolean LeftToRightFacing)
    {
        Image result;
        String fileName;
        if(LeftToRightFacing)
        {
            fileName = SharedResources.LS_SelectorArrow_LtoR_Image_Filename;
        }
        else
        {
            fileName = SharedResources.LS_SelectorArrow_RtoL_Image_Filename;
        }
        result = ImgFileRead(fileName);
        return result;
    }

    /**
     * Reads a file as an image using the provided file path in the parameter.
     * @param fileNameWithPath The path of the file to read.
     * @return Returns the file as an Image or returns null if the operation was unsuccessful.
     */
    public static Image ImgFileRead(String fileNameWithPath)
    {
        Image result = null;
        try {
            InputStream inputStream = Main.class.getResourceAsStream(fileNameWithPath);
            result = ImageIO.read(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Loads the car images in all its angles for in-game display.
     * @param carImageIndex The index number of the selected car. Starts from 0.
     * @return Returns an array of Images with all the angles represented in the SharedResources.CAR_ImageFile_Angles array
     */
    public static Image[] LoadCarImages(int carImageIndex)
    {
        ArrayList<Image> resultAsAL = new ArrayList<>();
        String mainPath = SharedResources.CAR_ImageFile_RootPath + Integer.toString(carImageIndex) + "/" + SharedResources.CAR_ImageFileName_Prefix;

        Stream.of(SharedResources.CAR_ImageFile_Angles).forEach(s ->
        {
            String f = mainPath + s + SharedResources.CAR_ImageFileName_Extension;
            resultAsAL.add(ImgFileRead(f));
        });

        return resultAsAL.toArray(new Image[resultAsAL.size()]);
    }

    /**
     * Returns the image of the crashed car. Used in-game when two car crashes to each other.
     * @return The image of the crashed car.
     */
    public static Image LoadCrashedCarImage()
    {
        if(_Cache_Crashed_Car == null)
        {
            _Cache_Crashed_Car = ImgFileRead(SharedResources.CAR_Crashed_ImageFileNameWithPath);
        }

        return _Cache_Crashed_Car;
    }
}
