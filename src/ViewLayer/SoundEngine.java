package ViewLayer;

import ControlLayer.SharedResources;
import ModelLayer.FileLoaders.AudioFileLoader;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Responsible for playing sounds and music on request.
 */
public class SoundEngine
{
    /**
     * Clip that plays the background music file.
     */
    private Clip _MusicClip;
    /**
     * Clip that plays the sound when a car impacts to a non-car object.
     */
    private Clip _CarImpactClip;
    /**
     * Clip that plays the sound when two car crashes.
     */
    private Clip _CarCrashClip;
    /**
     * Clip that plays the sound when a car accelerates.
     */
    private Clip _MotorPowerUpClip;

    /**
     * Responsible for playing sounds and music on request.
     */
    public SoundEngine()
    {
        try
        {
            //Get the clips from the OS
            _MusicClip = AudioSystem.getClip();
            _CarImpactClip = AudioSystem.getClip();
            _CarCrashClip = AudioSystem.getClip();
            _MotorPowerUpClip = AudioSystem.getClip();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Open the sound files as audio streams
        AudioInputStream _StreamBgMusic = AudioFileLoader.GetBackgroundMusic();
        AudioInputStream _CarImpactSound = AudioFileLoader.GetCarImpactSound();
        AudioInputStream _CarCrashSound = AudioFileLoader.GetCarCrashSound();
        AudioInputStream _CarMotorPowerUpSound = AudioFileLoader.GetMotorPowerUpSound();

        try
        {
            //Assign audio streams to clips
            _MusicClip.open(_StreamBgMusic);
            _CarImpactClip.open(_CarImpactSound);
            _CarCrashClip.open(_CarCrashSound);
            _MotorPowerUpClip.open(_CarMotorPowerUpSound);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //Start the background music by default if the music is enabled.
        if (SharedResources.DGO_Default_Music_On)
        {
            SetBackgroundMusic(true);
        }
    }


    /**
     * Starts or stops the background music.
     *
     * @param play True to start the background music. False to stop it.
     */
    public void SetBackgroundMusic(Boolean play)
    {
        if (play)
        {
            //StartBackgroundMusic();
        }
        else
        {
            StopBackGroundMusic();
        }
    }

    /**
     * Plays the sound when a car hits a non-car object.
     */
    public void PlayCarImpactSound()
    {
        _CarImpactClip.setFramePosition(0);
        _CarImpactClip.start();
    }

    /**
     * Plays the sound when two cars crashing into each other.
     */
    public void PlayCarCrashSound()
    {
        _CarCrashClip.setFramePosition(0);
        _CarCrashClip.start();
    }

    /**
     * Plays or stops playing the sound when a car accelerates.
     * @param play True to play the sound when the car is accelerating. False to stop it.
     */
    public void SetCarAccelerateSound(Boolean play)
    {
        if (play)
        {
            if (!_MotorPowerUpClip.isActive())
            {
                _MotorPowerUpClip.setFramePosition(0);
                _MotorPowerUpClip.start();
            }
        }
        else
        {
            _MotorPowerUpClip.stop();
        }
    }

    /**
     * Stops the background music.
     */
    private void StopBackGroundMusic()
    {
        _MusicClip.stop();
    }

    /**
     * Starts the background music.
     */
    private void StartBackgroundMusic()
    {
        if (!_MusicClip.isRunning())
        {
                _MusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

}
