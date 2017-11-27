package ModelLayer.CollisionManagement;

import ModelLayer.Enumerations.InGameObjectType;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Represents an object or part of an object on the map, which is used for collision detection.
 * This class is used as a simple structure.
 */
public class MapObject
{
    /**
     * Represents the object or part of the object as a line. Could be null.
     */
    public Line2D Line;

    /**
     * The type of the object represents (end of the map, grass area, etc.)
     */
    public InGameObjectType Type;

    /**
     * Represents the object or part of the object as a Rectangle. Could be null.
     */
    public Rectangle Rec;
}
