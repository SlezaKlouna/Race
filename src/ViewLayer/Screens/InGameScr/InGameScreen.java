package ViewLayer.Screens.InGameScr;

import ControlLayer.CurrentGameSession;
import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

/**
 * Displays the cars, Heads Up Displays and the map during the game.
 */
public class InGameScreen extends JPanel implements ActionListener, KeyListener
{

    /**
     * The car images.
     */
    private CarInGameDisplayLabel[] _CarLabels;
    /**
     * The HUDs
     */
    private HeadsUpDisplayPanel[] _HUDs;
    /**
     * A background image for the map.
     */
    private JLabel _MapBackGroundTexture = null;

    /**
     * Displays the cars, Heads Up Displays and the map during the game.
     */
    public InGameScreen()
    {
        this.setLayout(null);
        CreateCarLabels();
        CreateHeadsUpDisplays();

        addKeyListener(this);
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.requestFocus();

        this.setVisible(true);
    }

    /**
     * Instantiates the heads up displays for both players
     */
    private void CreateHeadsUpDisplays() {

        _HUDs = new HeadsUpDisplayPanel[2];
        _HUDs[0] = new HeadsUpDisplayPanel(CurrentGameSession.get_CurrentPlayers().get(0));
        _HUDs[1] = new HeadsUpDisplayPanel(CurrentGameSession.get_CurrentPlayers().get(1));
        _HUDs[0].setLocation(SharedResources.HUD_OnScreenLocation_X_Player_1, SharedResources.HUD_OnScreenLocation_Y_Player_1);
        _HUDs[1].setLocation(SharedResources.HUD_OnScreenLocation_X_Player_2, SharedResources.HUD_OnScreenLocation_Y_Player_2);
        _HUDs[0].CreateHudComponents();
        _HUDs[1].CreateHudComponents();
        this.add(_HUDs[0]);
        this.add(_HUDs[1]);
        _HUDs[0].setVisible(true);
        _HUDs[1].setVisible(true);
    }

    /**
     * Instantiates the car image displaying labels for both players.
     */
    private void CreateCarLabels() {
        _CarLabels = new CarInGameDisplayLabel[2];
        _CarLabels[0] = new CarInGameDisplayLabel(CurrentGameSession.get_CurrentPlayers().get(0));
        _CarLabels[1] = new CarInGameDisplayLabel(CurrentGameSession.get_CurrentPlayers().get(1));


        _CarLabels[0].setLocation(CurrentGameSession.get_MapModel().getCAR_StartingPoint_X_Player1(), CurrentGameSession.get_MapModel().getCAR_StartingPoint_Y_Player1());
        _CarLabels[0].SetStartImage(CurrentGameSession.get_MapModel().getCAR_Starting_Angle());
        _CarLabels[1].setLocation(CurrentGameSession.get_MapModel().getCAR_StartingPoint_X_Player2(), CurrentGameSession.get_MapModel().getCAR_StartingPoint_Y_Player2());
        _CarLabels[1].SetStartImage(CurrentGameSession.get_MapModel().getCAR_Starting_Angle());

        this.add(_CarLabels[0]);
        this.add((_CarLabels[1]));
        _CarLabels[0].setVisible(true);
        _CarLabels[1].setVisible(true);
    }

    /**
     * Called by the GameController each time the timer ticks.
     * It calls all the Cars and HUDs to refresh them.
     */
    public void NextFrame()
    {
        Arrays.stream(_CarLabels).forEach(cl ->
        {
            if(SharedResources.MainController.get_GameEngine() != null)
            {
                cl.RefreshForNextFrame();
            }
        });

        Arrays.stream(_HUDs).forEach(h1 ->
        {
            if(SharedResources.MainController.get_GameEngine() != null) {
                h1.UpdateHUD();
            }
        });

        if(SharedResources.MainController.get_GameEngine() != null)
            repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        DrawMap(g);
    }

    /**
     * Draws out the map with or without texture image.
     *
     * @param g The palette to paint to.
     */
    private void DrawMap(Graphics g)
    {
        if (SharedResources.DGO_Default_MapTexture_On)
        {
            if (_MapBackGroundTexture == null)
            {
                _MapBackGroundTexture = new JLabel();
                Icon i = new ImageIcon(CurrentGameSession.get_MapView().LoadTextureImage());
                _MapBackGroundTexture.setIcon(i);
                _MapBackGroundTexture.setSize(i.getIconWidth(), i.getIconHeight());
                _MapBackGroundTexture.setLocation(0, 0);
                this.add(_MapBackGroundTexture);
            }
            else
            {
                if (!_MapBackGroundTexture.isVisible()) {
                    _MapBackGroundTexture.setVisible(true);
                }
            }
        } else {
            if (_MapBackGroundTexture != null) {

                _MapBackGroundTexture.setVisible(false);
            }
            CurrentGameSession.get_MapView().DrawMap(g);
        }
    }



    @Override
    public void actionPerformed(ActionEvent e)
    {

    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    /**
     * Handles when a key gets pressed. If it is one of the valid control keys, it calls the local CarInGameDisplayLabel object.
     * @param e The keyEvent
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        if (Arrays.asList(SharedResources.GCS_ControlKeys_Player_1).contains(e.getKeyCode())) {
            _CarLabels[CurrentGameSession.get_RealPlayerIndex()].ControlKeyPressed(e.getKeyCode());
        }
    }

    /**
     * Handles when a key gets released. If it is one of the valid control keys, it calls the local CarInGameDisplayLabel object.
     * @param e The keyEvent
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        if (Arrays.asList(SharedResources.GCS_ControlKeys_Player_1).contains(e.getKeyCode())) {
            _CarLabels[CurrentGameSession.get_RealPlayerIndex()].ControlKeyReleased(e.getKeyCode());
        }
    }


    /**
     * Sets the car images to the crashed car image.
     */
    public void SetCarLabelImagesCrashed()
    {
        for (CarInGameDisplayLabel _CarLabel : _CarLabels) {
            _CarLabel.SetImageToCrashedCar();
        }

        repaint();
    }

}
