package ViewLayer.Screens.LaunchScr;

import ControlLayer.SharedResources;
import ModelLayer.FileLoaders.ImageFileLoader;

import javax.swing.*;
import java.awt.*;

/**
 * A simple JLabel containing a keyboard layout image for the specific player.
 * This is used on tha launch screen to aid the user about the game controls.
 */
class KeyBoardLayoutLabel extends JLabel
{

    /**
     * The keyboardf layout image.
     */
    private final Image _BackGround;

    /**
     * A simple JLabel containing a keyboard layout image for the player.
     * This is used on the launch screen to aid the user about the game controls.
     */
    public KeyBoardLayoutLabel()
    {
        _BackGround = ImageFileLoader.LoadKeyboardLayoutImage();
        this.setSize(SharedResources.CLI_Width,SharedResources.CLI_Height);

        this.setBackground(SharedResources.CLI_Background_Color);
        this.setOpaque(true);

        String labelText = SharedResources.LS_YOUR_KEYS_TEXT;
        this.setText(labelText);


        this.setAlignmentY(NORTH);
        this.setVerticalAlignment(TOP);
        this.setHorizontalAlignment(CENTER);
        this.setHorizontalTextPosition(CENTER);

        this.setFont(SharedResources.CLI_Header_Font);

        this.setLocation(0,0);
        this.setVisible(true);
    }

    /**
     * Draws out the components on a background.
     * @param g The palette to paint to.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        try
        {
            int bg_y = SharedResources.CLI_Height - SharedResources.CLI_Background_Image_Height;
            int bg_x = (SharedResources.CLI_Width - SharedResources.CLI_Background_Image_Width) / 2;

            g.drawImage(_BackGround,bg_x,bg_y,this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
