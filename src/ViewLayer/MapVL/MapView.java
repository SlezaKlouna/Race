package ViewLayer.MapVL;


import ControlLayer.CurrentGameSession;

import java.awt.*;

/**
 * Visual representation (view layer) of a map.
 * Actual maps are inheriting from this superclass to draw out specific maps.
 */
public class MapView {

    /**
     * Draws out the map without texture/enhancement.
     *
     * @param g The palette the map should be drawn to.
     */
    public void DrawMap(Graphics g) {
    }

    /**
     * Gets the enhanced map (texture) image from the disk.
     * @return Image of the enhanced map.
     */
    public Image LoadTextureImage() {
        return CurrentGameSession.get_MapModel().GetMapTextureImage();
    }
}
