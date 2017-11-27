package ModelLayer.CollisionManagement;

import ControlLayer.SharedResources;
import ModelLayer.Car;
import ModelLayer.Enumerations.InGameObjectType;
import ModelLayer.Player;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Calculates collisions between map objects and cars.
 */
public class CollisionManager
{
    private final ArrayList<Player> _Players; //Reference to the list of Players
    private final ArrayList<MapObject> _MapObjects; //The list of map objects the players' cars can collide with

    /**
     * Calculates collisions between map objects and cars.
     *
     * @param Players    The list of Players currently in game
     * @param MapObjects //The list of map objects the players' cars can collide with
     */
    public CollisionManager(ArrayList<Player> Players, ArrayList<MapObject> MapObjects)
    {
        this._Players = Players;
        this._MapObjects = MapObjects;
    }


    /**
     * Checks collision between a car and impassable map objects, such as map edges or trees.
     * @param r The car represented as a rectangle.
     * @param angleIndex The current angle of the car. This is used to provide more accurate calculation.
     * @return True if the car collides any of the impassable in-map objects. False if not.
     */
    public boolean IsCollidingWithImpassable(Rectangle r, int angleIndex)
    {
        boolean result;
        Stream<MapObject> impassables =  _MapObjects.stream().filter(m -> (m.Type == InGameObjectType.OUTOFMAP || m.Type == InGameObjectType.TREE) );
        result = impassables.anyMatch(i -> (isIntersecting(r, angleIndex, i.Rec) || isIntersecting(r, angleIndex, i.Line)));
        return  result;
    }

    /**
     * Check collisions between a car and grass area.
     * @param r The car represented as a rectangle.
     * @param angleIndex The current angle of the car. This is used to provide more accurate calculation.
     * @return True if the car collides with grass area. False if not.
     */
    public boolean IsCollidingWithGrass(Rectangle r, int angleIndex)
    {
        boolean result;
        Stream<MapObject> grasses =  _MapObjects.stream().filter(m -> (m.Type == InGameObjectType.GRASS) );
        result = grasses.anyMatch(i -> (isIntersecting(r, angleIndex, i.Rec) || isIntersecting(r, angleIndex, i.Line)));
        return  result;
    }

    /**
     * Check collisions between two cars.
     * @param requester The requested car object. This is used for preventing self-compare.
     * @param position The car represented as a rectangle.
     * @param angleIndex The current angle of the car. This is used to provide more accurate calculation.
     * @return True if the car collides with other car. False if not.
     */
    public boolean isCollidingWithOtherCars(Car requester, Rectangle position, int angleIndex)
    {
        boolean result;
        Stream<Player> otherPlayers = _Players.stream().filter(p -> p.get_Car() != requester);
        result = otherPlayers.anyMatch(p -> isIntersecting(position, angleIndex, p.get_Car().GetBoundsAsRectangle(), p.get_Car().GetCurrentAngleIndex()));
        return result;
    }

    /**
     * Checks if the two input cars are colliding with each other.
     * @param car1 The first car to compare.
     * @param angle1 The current angle of the first car.
     * @param car2 The second car to compare.
     * @param angle2 The current angle of the second car.
     * @return Returns true if the two cars are colliding. False if not.
     */
    private boolean isIntersecting(Rectangle car1, int angle1, Rectangle car2, int angle2)
    {
        boolean result = false;
        Line2D[] carLines1 = SharedResources.CAR_Simulated_Fine_Bounds[angle1].GetCarBounds(car1);
        Line2D[] carLines2 = SharedResources.CAR_Simulated_Fine_Bounds[angle2].GetCarBounds(car2);

        //Compare 4 lines against the 4 other lines. This is 16 comparison (in case of 2 cars).
        for (Line2D aCarLines1 : carLines1) {
            for (Line2D aCarLines2 : carLines2) {
                result = aCarLines1.intersectsLine(aCarLines2);
                if (result)
                    return result;
            }
        }
        return result;
    }


    /**
     * Checks if a car with a given angle intersects with a rectangle.
     * @param car The car (as a rectange) to check.
     * @param carAngleIndex The car's angle represented by the index value of the array of all possible angles.
     * @param r2 The rectangle to check the car collision with.
     * @return Return true if the car would collide with the rectangle. False if not.
     */
    private boolean isIntersecting(Rectangle car, int carAngleIndex, Rectangle r2) {
        return !(r2 == null || car == null) && isIntersecting(car, carAngleIndex, r2, true, false);
    }


    /**
     * Checks if a car with a given angle intersects with a line.
     * @param car The car (as a rectange) to check.
     * @param carAngleIndex The car's angle represented by the index value of the array of all possible angles.
     * @param l The line to check the car collision with.
     * @return Return true if the car would collide with the line. False if not.
     */
    private boolean isIntersecting(Rectangle car, int carAngleIndex, Line2D l) {
        return !(l == null || car == null) && isIntersecting(car, carAngleIndex, l, false, true);
    }

    /**
     * Checks if a car would collide with a rectangle or line object.
     * @param car The car (as a rectange) to check.
     * @param carAngleIndex The car's angle represented by the index value of the array of all possible angles.
     * @param o The rectangle or Line2D object to check the collision with.
     * @param isObjectRectangle True if the object is a rectangle. False if not. (Used for casting)
     * @param isObjectLine Ture if the objvect is a Line2D. False if not. (Used for casting).
     * @return Returns true if the car would collide with the object. False if not.
     */
    private boolean isIntersecting(Rectangle car, int carAngleIndex, Object o, Boolean isObjectRectangle, Boolean isObjectLine)
    {
        Line2D tmpLine = null;
        Rectangle tmpRec = null;
        if (isObjectRectangle)
            tmpRec = ((Rectangle) o);
        if (isObjectLine)
            tmpLine = ((Line2D) o);

        boolean result = false;
        Line2D[] carLines = SharedResources.CAR_Simulated_Fine_Bounds[carAngleIndex].GetCarBounds(car);

        for (Line2D carLine : carLines) {
            if (isObjectRectangle) {
                result = tmpRec.intersectsLine(carLine);
                if (result)
                    return result;
            }

            if (isObjectLine) {
                result = tmpLine.intersectsLine(carLine);
                if (result)
                    return result;
            }
        }
        return result;
    }



}
