package ModelLayer.CollisionManagement;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Represents a car's edges for a given angle.
 * This is used to improve accuracy of the collision detection.
 * All the possible angles of a car must be represented by one CarBounds instance each.
 * This class represents the car as rotated rectangle (instead of a square).
 */
public class CarBounds
{
    private final Line2D[] _AllCarBoundLines; //Containes the borders of the car as 4 lines

    /**
     * Represents a car's edges for a given angle.
     * This is used to improve accuracy of the collision detection.
     * All the possible angles of a car must be represented by one CarBounds instance each.
     * This class represents the car as rotated rectangle (instead of a square).
     * The parameter coordinates are describing the edge points of the rectangle in a clockwise order.
     * The points from the inport parameter will be converted to lines.
     *
     * @param p1x Top left point X coordinate
     * @param p1y Top left point Y coordinate
     * @param p2x Top right point X coordinate
     * @param p2y Top right point Y coordinate
     * @param p3x Bottom right point X coordinate
     * @param p3y Bottom right point Y coordinate
     * @param p4x Bottom left point X coordinate
     * @param p4y Bottom left point Y coordinate
     */
    public CarBounds(int p1x, int p1y, int p2x, int p2y, int p3x, int p3y, int p4x, int p4y)
    {
        Point _P1 = new Point(p1x, p1y);
        Point _P2 = new Point(p2x, p2y);
        Point _P3 = new Point(p3x, p3y);
        Point _P4 = new Point(p4x, p4y);

        Line2D _L1 = new Line2D.Double();
        Line2D _L2 = new Line2D.Double();
        Line2D _L3 = new Line2D.Double();
        Line2D _L4 = new Line2D.Double();

        _L1.setLine(_P1, _P2);
        _L2.setLine(_P2, _P3);
        _L3.setLine(_P3, _P4);
        _L4.setLine(_P4, _P1);

        _AllCarBoundLines = new Line2D[4];
        _AllCarBoundLines[0] = _L1;
        _AllCarBoundLines[1] = _L2;
        _AllCarBoundLines[2] = _L3;
        _AllCarBoundLines[3] = _L4;
    }


    /**
     * Returns the edges of the car described as an array of 4 lines forming a rectangle.
     *
     * @param shiftByCarPosition Shifts the edge coordinates with the position of this rectangle.
     * @return The 4 edges of the car with their position shifted by the shiftByCarPosition input parameter.
     */
    public Line2D[] GetCarBounds(Rectangle shiftByCarPosition)
    {
        Line2D[] result = new Line2D[4];

        for(int i = 0; i<4; i++)
        {
            result[i] = new Line2D.Double();
            result[i].setLine(_AllCarBoundLines[i].getX1() + shiftByCarPosition.getX(),
                    _AllCarBoundLines[i].getY1() + shiftByCarPosition.getY(),
                    _AllCarBoundLines[i].getX2() + shiftByCarPosition.getX(),
                    _AllCarBoundLines[i].getY2() + shiftByCarPosition.getY());
        }
        return result;
    }
}
