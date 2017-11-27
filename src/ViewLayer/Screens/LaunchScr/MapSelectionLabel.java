package ViewLayer.Screens.LaunchScr;

import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;

/**
 * Builds on a JLabel to display a thumbnail of a selectable map on the launch screen.
 */
class MapSelectionLabel extends JLabel
{
    /**
     * The thumbnail image of the map.
     */
    private final Image _Map;

    /**
     * Builds on a JLabel to display a thumbnail of a selectable map on the launch screen.
     *
     * @param img The thumbnail image of the selectable map.
     */
    public MapSelectionLabel(Image img)
    {
        _Map = img;
        this.setSize(SharedResources.MSP_Map_Image_Width, SharedResources.MSP_Map_Image_Height);
        this.setLocation(0,0);
        this.setOpaque(true);
        this.setBackground(SharedResources.MSP_Map_BackGround);
        this.setVisible(true);
    }

    /**
     * Draws the map thumbnail.
     * @param g The palette to use for drawing.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        try {
            g.drawImage(_Map,0,0,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
