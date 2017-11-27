package ModelLayer.MapML;

import ModelLayer.CollisionManagement.MapObject;
import ModelLayer.Enumerations.InGameObjectType;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Describes the elements and properties of the Easy map.
 */
public class EasyMapML extends MapModel
{

    /**
     * Describes the elements and properties of the Easy map.
     * Cars are facing towards east (Angle = 90).
     * Cars are placed to the start line, which is at the bottom center of the map.
     */
    public EasyMapML() {
        CAR_Starting_Angle = 90;
        CAR_StartingPoint_X_Player1 = 375;
        CAR_StartingPoint_Y_Player1 = 500;
        CAR_StartingPoint_X_Player2 = 375;
        CAR_StartingPoint_Y_Player2 = 550;
        MAP_TextureFile = "/imgs/maptextures/EasyMapTexture.jpg";
    }

    /**
     * Creates the MapObjects which are used for collision detection.
     * MapObjects are objects of a map a car can collide with (edge of the racing track, grass, etc)
     */
    @Override
    protected void CreateMapObjects()
    {
        _MapObjects = new ArrayList<>();
        MapObject tmp;

        //Border values for the outer rectangle
        int LeftX = 50;
        int RightX = 800;
        int TopY = 100;
        int BottomY = 600;

        //Top OUTOFMAP border for the racing track (North line)
        tmp = new MapObject();
        tmp.Line = new Line2D.Double();
        tmp.Line.setLine(LeftX,TopY,RightX,TopY);
        tmp.Type = InGameObjectType.OUTOFMAP;
        _MapObjects.add(tmp);

        //Left OUTOFMAP border for the racing track (East line)
        tmp = new MapObject();
        tmp.Line = new Line2D.Double();
        tmp.Line.setLine(LeftX,TopY,LeftX,BottomY);
        tmp.Type = InGameObjectType.OUTOFMAP;
        _MapObjects.add(tmp);

        //Bottom OUTOFMAP border for the racing track (South line)
        tmp = new MapObject();
        tmp.Line = new Line2D.Double();
        tmp.Line.setLine(LeftX,BottomY,RightX,BottomY);
        tmp.Type = InGameObjectType.OUTOFMAP;
        _MapObjects.add(tmp);

        //Right OUTOFMAP border for the racing track(West line)
        tmp = new MapObject();
        tmp.Line = new Line2D.Double();
        tmp.Line.setLine(RightX,TopY,RightX,BottomY);
        tmp.Type = InGameObjectType.OUTOFMAP;
        _MapObjects.add(tmp);

        //Inner grass area
        tmp = new MapObject();
        tmp.Rec = new Rectangle(150,200,550,300);
        tmp.Type = InGameObjectType.GRASS;
        _MapObjects.add(tmp);
    }



}
