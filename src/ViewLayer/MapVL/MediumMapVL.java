package ViewLayer.MapVL;

import ControlLayer.SharedResources;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Provides graphical representation of the Medium map.
 */
public class MediumMapVL extends MapView {


    /**
     * Outer racing track lines
     */
    private final ArrayList<Line2D> _InnerEdges;

    /**
     * Inner racing track lines
     */
    private final ArrayList<Line2D> _OuterEdges;

    /**
     * Start line
     */
    private final Point StartLineTop = new Point(425, 500);
    private final Point StartLineBottom = new Point(425, 600);

    /**
     * Provides graphical representation of the Medium map.
     */
    public MediumMapVL() {
        //Outer racing track points
        Point TopLeftOuter = new Point(50, 100);
        Point BottomLeftOuter = new Point(50, 600);
        Point BottomRightOuter = new Point(800, 600);
        Point MiddleRightOuter = new Point(800, 300);
        Point MiddleCenterOuter = new Point(375, 300);
        Point TopCenterOuter = new Point(375, 100);

        //Inner racing track points
        Point TopLeftInner = new Point(150, 200);
        Point BottomLeftInner = new Point(150, 500);
        Point BottomRightInner = new Point(700, 500);
        Point MiddleRightInner = new Point(700, 400);
        Point MiddleCenterInner = new Point(275, 400);
        Point TopCenterInner = new Point(275, 200);


        _InnerEdges = new ArrayList<>();
        _OuterEdges = new ArrayList<>();

        //Connecting the outer racing track points to be lines
        _InnerEdges.add(new Line2D.Double(TopLeftInner.x, TopLeftInner.y, BottomLeftInner.x, BottomLeftInner.y));
        _InnerEdges.add(new Line2D.Double(BottomLeftInner.x, BottomLeftInner.y, BottomRightInner.x, BottomRightInner.y));
        _InnerEdges.add(new Line2D.Double(BottomRightInner.x, BottomRightInner.y, MiddleRightInner.x, MiddleRightInner.y));
        _InnerEdges.add(new Line2D.Double(MiddleRightInner.x, MiddleRightInner.y, MiddleCenterInner.x, MiddleCenterInner.y));
        _InnerEdges.add(new Line2D.Double(MiddleCenterInner.x, MiddleCenterInner.y, TopCenterInner.x, TopCenterInner.y));
        _InnerEdges.add(new Line2D.Double(TopCenterInner.x, TopCenterInner.y, TopLeftInner.x, TopLeftInner.y));

        //Connecting the inner racing track points to be lines
        _OuterEdges.add(new Line2D.Double(TopLeftOuter.x, TopLeftOuter.y, BottomLeftOuter.x, BottomLeftOuter.y));
        _OuterEdges.add(new Line2D.Double(BottomLeftOuter.x, BottomLeftOuter.y, BottomRightOuter.x, BottomRightOuter.y));
        _OuterEdges.add(new Line2D.Double(BottomRightOuter.x, BottomRightOuter.y, MiddleRightOuter.x, MiddleRightOuter.y));
        _OuterEdges.add(new Line2D.Double(MiddleRightOuter.x, MiddleRightOuter.y, MiddleCenterOuter.x, MiddleCenterOuter.y));
        _OuterEdges.add(new Line2D.Double(MiddleCenterOuter.x, MiddleCenterOuter.y, TopCenterOuter.x, TopCenterOuter.y));
        _OuterEdges.add(new Line2D.Double(TopCenterOuter.x, TopCenterOuter.y, TopLeftOuter.x, TopLeftOuter.y));
    }


    /**
     * Draws out the easy/default map without texture enhancement.
     *
     * @param g The palette the map should be drawn to.
     */
    @Override
    public void DrawMap(Graphics g) {

        if (!SharedResources.DGO_Default_MapTexture_On) {

            g.setColor(Color.black);
            //Draw  the inner and outer edges of the racing track
            for (int i = 0; i < _InnerEdges.size(); i++) {
                g.drawLine((int) _InnerEdges.get(i).getX1(), (int) _InnerEdges.get(i).getY1(), (int) _InnerEdges.get(i).getX2(), (int) _InnerEdges.get(i).getY2());
                g.drawLine((int) _OuterEdges.get(i).getX1(), (int) _OuterEdges.get(i).getY1(), (int) _OuterEdges.get(i).getX2(), (int) _OuterEdges.get(i).getY2());
            }

            //Draw the start line
            g.setColor(Color.white);
            g.drawLine(StartLineTop.x, StartLineTop.y, StartLineBottom.x, StartLineBottom.y);
        }
    }


    /**
     * Returnes the list of lines representing the edges of the racing track.
     * @param outerEdge True if the outer edges of the racing track needs to be returned. False if the inner edges needed.
     * @return A list of lines representing the edges of the racing track.
     */
    public ArrayList<Line2D> GetRacingTrackEdgesAsLines(boolean outerEdge) {
        if (outerEdge) {
            return _OuterEdges;
        } else {
            return _InnerEdges;
        }
    }

}
