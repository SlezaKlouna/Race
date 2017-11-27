package ViewLayer.Screens.LaunchScr;

import ControlLayer.SharedResources;
import ViewLayer.Screens.ServerScr.LogPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A pre-game configuration screen allowing the player to select car(s) and a map.
 * It is a JPanel that contains other JPanels to achieve these functions.
 */
public class LaunchScreen extends JPanel implements ActionListener
{

    /**
     * The background image of screen.
     */
    private final Image _BackgroundImg;
    /**
     * Car selection panel. Contains the selectable cars.
     */
    private CarSelectionPanel _CarSelectionPanel;
    /**
     * Map selection panel. Contains the available maps.
     */
    private MapSelectionPanel _MapSelectionPanel;
    /**
     * Return to the main menu button.
     */
    private JButton _BackToMainButton;
    /**
     * Start game button.
     */
    private JButton _StartGameButton;

    /**
     * Server connection settings are displayed within this panel (IP, port)
     */
    private ClientConnectPanel _ClientConnectionPanel;

    /**
     * Displays a terminal like text area, which contains logs and messages from the client-server communication
     */
    private LogPanel _LogPanel;

    /**
     * A pre-game configuration screen allowing the player to select car and a map.
     * It is a JPanel that contains other JPanels to achieve these functions.
     *
     * @param backgroundImage The image to use for background.
     */
    public LaunchScreen (Image backgroundImage)
    {
        this.setLayout(null);
        _BackgroundImg = backgroundImage;

        SetForRemoteGameScenarion();

        AddReturnToMainButton();
        AddGameStartButton();
        this.setVisible(true);
    }


    /**
     * Selects car and map for default, so the player can proceed without selecting one.
     * By default, the first car and the Easy map selected.
     */
    public void SelectDefaultValues() {
        _CarSelectionPanel.SelectFirstCarByDefault();
        _MapSelectionPanel.SelectDefaultMap();
        setVisible(true);
        repaint();
    }


    /**
     * Creates the "start game" button and places to the bottom right corner.
     */
    private void AddGameStartButton()
    {
        if(_StartGameButton == null)
        {
           _StartGameButton = new JButton(SharedResources.LS_GameStartButton_Text_Ready);

            int location_x = _MapSelectionPanel.getX();
           _StartGameButton.setLocation(location_x, SharedResources.LS_GameStartButton_Y);

            int width = _MapSelectionPanel.getWidth();
            _StartGameButton.setSize(width,SharedResources.LS_GameStartButton_Height);
            _StartGameButton.setBackground(SharedResources.LS_GameStartButton_Color);
            _StartGameButton.setFont(SharedResources.LS_GameStartButton_Font);

            _StartGameButton.setForeground(SharedResources.LS_GameStartButton_Foreground_Color);

            this.add(_StartGameButton);
            _StartGameButton.setVisible(true);
            _StartGameButton.addActionListener(this);
        }

    }

    /**
     * Creates the "Return to the main menu" button and places it to the bottom left corner.
     */
    private void AddReturnToMainButton()
    {
        if(_BackToMainButton == null)
        {
            _BackToMainButton = new JButton(SharedResources.LS_ReturnToMainButton_Text);
            _BackToMainButton.setLocation(SharedResources.LS_ReturnToMainButton_X,SharedResources.LS_ReturnToMainButton_Y);
            _BackToMainButton.setSize(SharedResources.LS_ReturnToMainButton_Width,SharedResources.LS_ReturnToMainButton_Height);

            _BackToMainButton.setBackground(SharedResources.LS_ReturnToMainButton_Color);
            _BackToMainButton.setFont(SharedResources.LS_ReturnToMainButton_Font);

            this.add(_BackToMainButton);
            _BackToMainButton.setVisible(true);

            _BackToMainButton.addActionListener(this);
        }
    }

    /**
     * Draws the background and paints the components.
     * @param g The palette to be painted to.
     */
    protected void  paintComponent(Graphics g)
    {
        super.paintComponent(g);
        try
        {
            g.drawImage(_BackgroundImg,0,0,this);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Setting up the screen for a Single Player scenario:
     * One car selection panel, One keyboard layout panel, one map selection panel and a start button.
     */
    private void SetForRemoteGameScenarion()
    {
        //Creating and adding the CarSelectionPanel
        _CarSelectionPanel = new CarSelectionPanel();
        this.add(_CarSelectionPanel);
        _CarSelectionPanel.setLocation(SharedResources.CSP_Location_Player1_X, SharedResources.CSP_Location_Player1_Y);
        _CarSelectionPanel.setVisible(true);
        _CarSelectionPanel.SelectFirstCarByDefault();

        //Adding the Keyboard Layout label
        KeyBoardLayoutLabel keyBoardLayout = new KeyBoardLayoutLabel();
        int keyBoardLayout_X = SharedResources.CSP_Location_Player1_X + _CarSelectionPanel.getWidth() + SharedResources.CLI_HorizontalSpacing_From_CSP;
        int keyBoardLayout_Y = SharedResources.CSP_Location_Player1_Y;
        keyBoardLayout.setLocation(keyBoardLayout_X,keyBoardLayout_Y);
        this.add(keyBoardLayout);

        //Adding the map selection panel
        MapSelectionPanel mapSelectionPanel = new MapSelectionPanel();
        int mapSelectionPanel_X = keyBoardLayout_X + keyBoardLayout.getWidth() +  SharedResources.MSP_Space_From_CLI;
        mapSelectionPanel.setLocation(mapSelectionPanel_X, keyBoardLayout_Y);
        this.add(mapSelectionPanel);
        _MapSelectionPanel = mapSelectionPanel;

        //Adding the client connection panel
        _ClientConnectionPanel = new ClientConnectPanel();
        int clientConnectionPanel_Y = SharedResources.CSP_Location_Player1_Y + SharedResources.LS_Connection_Panel_Vertical_Spacer + _CarSelectionPanel.getHeight();
        _ClientConnectionPanel.setLocation(SharedResources.CSP_Location_Player1_X, clientConnectionPanel_Y);
        this.add(_ClientConnectionPanel);

        //Adding log panel (terminal)
        _LogPanel = new LogPanel(true);
        int logPanel_Y = clientConnectionPanel_Y + _ClientConnectionPanel.getHeight() + (SharedResources.LS_Connection_Panel_Vertical_Spacer / 2);
        _LogPanel.setLocation(SharedResources.CSP_Location_Player1_X, logPanel_Y);
        this.add(_LogPanel);
    }


    /**
     * Getter for the only instance of the LogPanel.
     * @return The only instance of the LogPanel
     */
    public LogPanel get_LogPanel() {
        return _LogPanel;
    }

    /**
     * Getter for client connection panel's only instance.
     *
     * @return Only instance of the client connection panel.
     */
    public ClientConnectPanel get_ClientConnectionPanel() {
        return _ClientConnectionPanel;
    }

    /**
     * Handling events from the "Back to main button" and
     * "Start game button" components.
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        //If the "Return to main" button clicked then return to the main screen and forget current settings.
        if(e.getSource() == _BackToMainButton)
        {
            SharedResources.MainController.NavigatingBackToMainMenuScreen();
        }

        //If the "Start" button clicked, then launch the game.
        if(e.getSource() == _StartGameButton)
        {
            SharedResources.MainController.LaunchGame();
        }
    }
}
