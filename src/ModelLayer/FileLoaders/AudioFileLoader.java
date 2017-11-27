package ModelLayer.FileLoaders;

import ControlLayer.SharedResources;
import sun.tools.jar.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Loads in music and sound files from disk and returns them AudioInputStreams
 */
public class AudioFileLoader
{

    /**
     * Loads in the background music of the game.
     *
     * @return AudioInputStream of the background music file.
     */
    public static AudioInputStream GetBackgroundMusic()
    {
        return GetStream(SharedResources.SND_BackgroundMusic_FilenameWithPath);
    }

    /**
     * Loads in the sound effect played when a car hits an another non-car object.
     * @return AudioInputStream of the impact sound file.
     */
    public static AudioInputStream GetCarImpactSound()
    {
        return GetStream(SharedResources.SND_CarImpactSound_FilenameWithPath);
    }

    /**
     * Loads in the sound effect played when two cars crash into each other.
     * @return AudioInputStream of the car crash sound file.
     */
    public static AudioInputStream GetCarCrashSound()
    {
        return GetStream(SharedResources.SND_CarCrash_FilenameWithPath);
    }

    /**
     * Loads in the sound effect played when the car is accelerating.
     * @return AudioInputStream of the accelerating car.
     */
    public static AudioInputStream GetMotorPowerUpSound()
    {
        return GetStream(SharedResources.SND_CarPowerUp_FilenameWithPath);
    }

    /**
     * Loads in an audio file as an AudioInputStream.
     * @param f The file to load in.
     * @return Return the file as an AudioInputStream. Returns null if loading failed.
     */
    private static AudioInputStream GetStream(String f)
    {
        AudioInputStream result = null;
        try
        {
            InputStream is = Main.class.getResourceAsStream(f);
            BufferedInputStream bf = new BufferedInputStream(is);
            result = AudioSystem.getAudioInputStream(bf);
        } catch (UnsupportedAudioFileException | IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }


}
