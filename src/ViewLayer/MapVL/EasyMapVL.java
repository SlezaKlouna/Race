package ViewLayer.MapVL;

import ControlLayer.SharedResources;

import java.awt.*;

/**
 * Provides graphical representation of the Easy/default map.
 */
public class EasyMapVL extends MapView
{

    /**
     * Draws out the easy/default map without texture enhancement.
     *
     * @param g The palette to paint to.
     */
    @Override
    public void DrawMap(Graphics g)
    {
        if (!SharedResources.DGO_Default_MapTexture_On)
        {
            Color c1 = Color.green;
            g.setColor( c1 );
            g.fillRect( 150, 200, 550, 300 ); //grass
            Color c2 = Color.black;
            g.setColor( c2 );
            g.drawRect(50, 100, 750, 500); // outer edge
            g.drawRect(150, 200, 550, 300); // inner edge
            Color c3 = Color.yellow;
            g.setColor( c3 );
            g.drawRect( 100, 150, 650, 400 ); // mid-lane marker
            Color c4 = Color.white;
            g.setColor( c4 );
            g.drawLine( 425, 500, 425, 600 ); // start line
        }
    }

}
