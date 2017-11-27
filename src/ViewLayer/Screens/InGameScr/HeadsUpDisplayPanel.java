package ViewLayer.Screens.InGameScr;

import ControlLayer.SharedResources;
import ModelLayer.Player;

import javax.swing.*;

/**
 * A heads up display representing in-game information for one associated player.
 * The HUD displays: Image/thumbnail of the car, player's name, current speed as a number, current speed in a progress bar.
 */
class HeadsUpDisplayPanel extends JPanel {

    /**
     * The associated player who's data needed to be displayed.
     */
    private final Player _Player;

    /**
     * Car image/thumbnail.
     */
    private JLabel _PIcon;

    /**
     * The player's name.
     */
    private JLabel _PName;

    /**
     * Displays the current speed of the car as text.
     */
    private JLabel _SpeedLabel;

    /**
     * Displays the current speed as a progress bar (which also represents the min and max speed limits).
     */
    private JProgressBar _Progress;

    /**
     * A heads up display representing in-game information for one associated player.
     * The HUD displays: Image/thumbnail of the car, player's name, current speed as a number, current speed in a progress bar.
     *
     * @param player The player to associate the HUD with.
     */
    public HeadsUpDisplayPanel(Player player) {
        _Player = player;
        this.setLayout(null);
        this.setLocation(0, 0);
        this.setSize(SharedResources.HUD_Panel_Width, SharedResources.HUD_Panel_Height);
        this.setBackground(SharedResources.HUD_Bg_Color);

        this.setVisible(true);
    }

    /**
     * Instantiates the HUD elements.
     */
    public void CreateHudComponents() {
        CreatePlayerIconLabel();
        CreatePlayerNameLabel();
        CreateSpeedDisplayLabel();
        CreateSpeedProgressBar();

        this.setVisible(true);
    }

    /**
     * Updates the current speed values in the HUD.
     */
    public void UpdateHUD() {
        int speed = _Player.GetLatestCarSpeedForHUD();
        _SpeedLabel.setText(SharedResources.HUD_SpeedLabel_Text_Prefix + Integer.toString(speed) + SharedResources.HUD_SpeedLabel_Text_PostFix);
        _Progress.setValue(speed);
    }


    /**
     * Instantiates the progress bar representing the current speed.
     * The minimum value is 0 and the maximum is set to 100 in SharedResources.GCS_Car_Virtual_Speed_Max
     */
    private void CreateSpeedProgressBar() {
        _Progress = new JProgressBar(SwingConstants.HORIZONTAL, 0, SharedResources.GCS_Car_Virtual_Speed_Max);
        _Progress.setValue(0);
        _Progress.setSize(SharedResources.HUD_SpeedBar_Width, SharedResources.HUD_SpeedBar_Height);
        _Progress.setBackground(SharedResources.HUD_SpeedBar_Bg_Color);

        int x = _SpeedLabel.getX();
        int y = _SpeedLabel.getY() + _SpeedLabel.getHeight() + SharedResources.HUD_PlayerSpeedYSpacer;
        _Progress.setLocation(x, y);

        this.add(_Progress);
        _Progress.setVisible(true);
    }

    /**
     * Instantiates the JLabel which displays the speed as a text.
     */
    private void CreateSpeedDisplayLabel() {
        _SpeedLabel = new JLabel(SharedResources.HUD_SpeedLabel_Text_Prefix + "0" + SharedResources.HUD_SpeedLabel_Text_PostFix);
        _SpeedLabel.setFont(SharedResources.HUD_PlayerSpeed_Font);

        int x = _PName.getX();
        int y = _PName.getY() + _PName.getHeight() + SharedResources.HUD_PlayerNameYSpacer;
        _SpeedLabel.setLocation(x, y);
        _SpeedLabel.setSize(SharedResources.HUD_PlayerSpeedWidth, SharedResources.HUD_PlayerSpeedHeight);

        this.add(_SpeedLabel);
        _SpeedLabel.setVisible(true);
    }

    /**
     * Instantiates the JLabel that contains the player's name.
     */
    private void CreatePlayerNameLabel() {
        _PName = new JLabel(_Player.get_PlayerName());
        _PName.setFont(SharedResources.HUD_PlayerName_Font);

        int x = _PIcon.getX() + _PIcon.getWidth() + SharedResources.HUD_IconLeftSpacer;
        int y = _PIcon.getY();
        _PName.setLocation(x, y);
        _PName.setSize(SharedResources.HUD_PlayerNameWidth, SharedResources.HUD_PlayerNameHeight);

        this.add(_PName);
        _PName.setVisible(true);
    }

    /**
     * Instantiates the JLabel which displays the icon/thumbnail of the palyer's car.
     */
    private void CreatePlayerIconLabel() {
        _PIcon = new JLabel();
        _PIcon.setSize(SharedResources.CAR_Image_Size_X, SharedResources.CAR_Image_Size_Y);

        //Vertical centering the icon
        int y = (SharedResources.HUD_Panel_Height - SharedResources.CAR_Image_Size_Y) / 2;
        _PIcon.setLocation(0, y);

        ImageIcon icon = new ImageIcon(_Player.GetPlayerCarIconForHUD());
        _PIcon.setIcon(icon);

        this.add(_PIcon);
        _PIcon.setVisible(true);
    }


}
