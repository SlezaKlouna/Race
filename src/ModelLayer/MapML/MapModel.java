package ModelLayer.MapML;

import ModelLayer.CollisionManagement.MapObject;
import ModelLayer.FileLoaders.ImageFileLoader;

import java.awt.*;
import java.util.ArrayList;

/**
 * Super class for all the maps' model representation.
 * By itself, does not describe any sensible map. A child must be used for that purpose.
 */
public class MapModel
{
    /**
     * Contains the MapObjects which are used for collision detection.
     * MapObjects are objects of a map a car can collide with (edge of the racing track, grass, etc)
     */
    static ArrayList<MapObject> _MapObjects;

    /**
     * Represents the cars starting angle, whereby 0 is a car facing north.
     * This number must match with one of the elements of the SharedResources.CAR_Simulated_Angle_Values array.
     */
    int CAR_Starting_Angle = 0;
    /**
     * The X coordinate of the starting point for the player 1 car.
     */
    int CAR_StartingPoint_X_Player1 = 0;
    /**
     * The Y coordinate of the starting point for the player 1 car.
     */
    int CAR_StartingPoint_Y_Player1 = 0;
    /**
     * The X coordinate of the starting point for the player 2 car.
     */
    int CAR_StartingPoint_X_Player2 = 0;
    /**
     * The Y coordinate of the starting point for the player 2 car.
     */
    int CAR_StartingPoint_Y_Player2 = 0;
    /**
     * The file path for the texture file (background image) of the map.
     */
    String MAP_TextureFile = "";

    /**
     * Getter for the cars starting angle.
     *
     * @return Returns the cars starting angle, whereby 0 is a car facing north.
     */
    public int getCAR_Starting_Angle() {
        return CAR_Starting_Angle;
    }

    /**
     * Getter for the player 1 car's starting point's X coordinate.
     * @return The X coordinate of the starting point for the player 1 car.
     */
    public int getCAR_StartingPoint_X_Player1() {
        return CAR_StartingPoint_X_Player1;
    }

    /**
     * Getter for the player 1 car's starting point's Y coordinate.
     * @return The Y coordinate of the starting point for the player 1 car.
     */
    public int getCAR_StartingPoint_Y_Player1() {
        return CAR_StartingPoint_Y_Player1;
    }

    /**
     * Getter for the player 2 car's starting point's X coordinate.
     * @return The X coordinate of the starting point for the player 2 car.
     */
    public int getCAR_StartingPoint_X_Player2() {
        return CAR_StartingPoint_X_Player2;
    }

    /**
     * Getter for the player 2 car's starting point's Y coordinate.
     * @return The Y coordinate of the starting point for the player 2 car.
     */
    public int getCAR_StartingPoint_Y_Player2() {
        return CAR_StartingPoint_Y_Player2;
    }

    /**
     * Returns the maps' background texture as an Image.
     * @return Returns the image representing the enhanced arena.
     */
    public Image GetMapTextureImage() {
        return ImageFileLoader.ImgFileRead(MAP_TextureFile);
    }

    /**
     * Gets the list of MapObjects which are used for collision detection.
     * @return An ArrayList of MapObjects.
     */
    public  ArrayList<MapObject> GetMapObjects()
    {
        CreateMapObjects();
        return _MapObjects;
    }

    /**
     * Instantiates the MapObjects and adds them to the _MapObjects array list.
     */
    void CreateMapObjects() {
        //This function must be implemented by the child classes.
    }
}
