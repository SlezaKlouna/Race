package ModelLayer.MapML;

import ControlLayer.CurrentGameSession;
import ModelLayer.CollisionManagement.MapObject;
import ModelLayer.Enumerations.InGameObjectType;
import ViewLayer.MapVL.MediumMapVL;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class MediumMapML extends MapModel {
    public MediumMapML() {
        CAR_Starting_Angle = 90;
        CAR_StartingPoint_X_Player1 = 375;
        CAR_StartingPoint_Y_Player1 = 500;
        CAR_StartingPoint_X_Player2 = 375;
        CAR_StartingPoint_Y_Player2 = 550;
        MAP_TextureFile = "/imgs/maptextures/MediumMapTexture.png";
    }

    @Override
    protected void CreateMapObjects() {
        _MapObjects = new ArrayList<>();
        MediumMapVL mapVL = (MediumMapVL) CurrentGameSession.get_MapView();
        ArrayList<Line2D> lnTmp;

        lnTmp = mapVL.GetRacingTrackEdgesAsLines(true);
        CreateOutOfMapMapObjects(lnTmp);
        lnTmp = mapVL.GetRacingTrackEdgesAsLines(false);
        CreateOutOfMapMapObjects(lnTmp);
    }

    private void CreateOutOfMapMapObjects(ArrayList<Line2D> lnTmp) {
        MapObject tmp;
        for (Line2D aLnTmp : lnTmp) {
            tmp = new MapObject();
            tmp.Line = aLnTmp;
            tmp.Type = InGameObjectType.OUTOFMAP;
            _MapObjects.add(tmp);
        }
    }


}
