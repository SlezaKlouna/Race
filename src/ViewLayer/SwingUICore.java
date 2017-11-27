package ViewLayer;


import ControlLayer.ServerEngine;
import ControlLayer.SharedResources;
import ModelLayer.FileLoaders.ImageFileLoader;
import ViewLayer.Screens.InGameScr.InGameScreen;
import ViewLayer.Screens.LaunchScr.LaunchScreen;
import ViewLayer.Screens.MainMenuScr.MainMenuScreen;
import ViewLayer.Screens.ServerScr.ServerScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * The top level and single instance graphical element of the application. Extends a JFrame.
 * It contains the menu bar and loads in screens (JPanels) to display the appropriate content.
 */
public class SwingUICore extends JFrame implements ActionListener {

    /**
     * The JFrame's content pane.
     */
    private final Container _ContentPane;
    /**
     * Background image for the Main Menu Screen and for the Launch Screen JPanels.
     */
    private final Image _MenusBackgroundImage;

    /**
     * Contains all the screens (JPanels).
     */
    private final ArrayList<JPanel> _Screens;
    /**
     * JPanel containing the MainMenu components
     */
    private MainMenuScreen _MainMenuScreen;
    /**
     * JPanel containing the game configuration screen
     */
    private LaunchScreen _LaunchScreen;
    /**
     * JPanel containing the in-game elements (race track, cars)
     */
    private InGameScreen _InGameScreen;
    /**
     * Contains server configuration and monitoring components.
     */
    private ServerScreen _ServerScreen;

    /**
     * New game button in the menu bar
     */
    private JMenuItem _NewGameMenuButton;
    /**
     * Help--About button in menu bar
     */
    private JMenuItem _HelpAboutMenuButton;
    /**
     * Help--Controls button in menu bar
     */
    private JMenuItem _HelpControlMenuButton;
    /**
     * Help--Collision button in menu bar
     */
    private JMenuItem _HelpCollisionMenuButton;
    /**
     * Settings--Sounds on button in menu bar
     */
    private JCheckBoxMenuItem _SoundsOnMenuButton;
    /**
     * Settings--Music on button in menu bar
     */
    private JCheckBoxMenuItem _MusicOnMenuButton;
    /**
     * Settings--Map textures on button in menu bar
     */
    private JCheckBoxMenuItem _MapTextureOnButton;

    /**
     * The top level and single instance graphical element of the application. Extends a JFrame.
     * It contains the menu bar and loads in screens (JPanels) to display the appropriate content.
     */
    public SwingUICore()
    {
        InstantiateMenuBar();

        setTitle(SharedResources.APPLICATIONNAME);
        setResizable(false);
        setBounds(0,0, SharedResources.MW_JFRAME_WIDTH, SharedResources.MW_JFRAME_HEIGHT);
        setLocationRelativeTo(null); //Place the window to screen's center.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        _ContentPane = getContentPane();
        _Screens = new ArrayList<>();
        _MenusBackgroundImage = ImageFileLoader.LoadBackgroundImage();

        //Listen to window events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SharedResources.MainController.WindowClosingHandler();
                super.windowClosing(e);
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                if (SharedResources.MainController.get_GameEngine() != null)
                    SharedResources.MainController.get_GameEngine().HandleWindowGettingDeactivated();
                super.windowDeactivated(e);
            }
        });
    }

    /**
     * Display the Frame
     */
    public void CreateDefaultWindow()
    {
        this.setVisible(true);
    }

    /**
     * Display the Main menu screen JPanel content
     */
    public void NavigateToMainMenuScreen()
    {
        HideAllScreens();
        if(_MainMenuScreen == null)
        {
            _MainMenuScreen = new MainMenuScreen(_MenusBackgroundImage);
            _Screens.add(_MainMenuScreen);
            _ContentPane.add(_MainMenuScreen);
        }
        _MainMenuScreen.setVisible(true);
        this.setVisible(true);
    }

    /**
     * Creates menubar on the top of the screen
     */
    private void InstantiateMenuBar()
    {
        JMenuBar _MenuBar = new JMenuBar();

        //Creating main menus
        JMenu GameMenu = new JMenu(SharedResources.MB_GameMenuString);
        JMenu helpMenu = new JMenu(SharedResources.MB_HelpMenuString);
        JMenu settingsMenu = new JMenu(SharedResources.MB_SettingsMenuString);
        _MenuBar.add(GameMenu);
        _MenuBar.add(settingsMenu);
        _MenuBar.add(helpMenu);

        //Submenus for new game
        _NewGameMenuButton = new JMenuItem(SharedResources.MB_StartNewGameMenuString);
        GameMenu.add(_NewGameMenuButton);

        //Submenus for help
        _HelpAboutMenuButton = new JMenuItem(SharedResources.MB_HelpAboutMenuString);
        _HelpControlMenuButton = new JMenuItem(SharedResources.MB_HelpControlMenuString);
        _HelpCollisionMenuButton = new JMenuItem(SharedResources.MB_HelpCollisionMenuString);
        helpMenu.add(_HelpAboutMenuButton);
        helpMenu.add(_HelpControlMenuButton);
        helpMenu.add(_HelpCollisionMenuButton);

        //Submenus for settings
        _MusicOnMenuButton = new JCheckBoxMenuItem(SharedResources.MB_MusicMenuButtonString);
        _MusicOnMenuButton.setState(SharedResources.DGO_Default_Music_On);
        _SoundsOnMenuButton = new JCheckBoxMenuItem(SharedResources.MB_SoundsMenuButtonString);
        _SoundsOnMenuButton.setState(SharedResources.DGO_Default_Sound_On);
        _MapTextureOnButton = new JCheckBoxMenuItem(SharedResources.MB_MapTextureMenuButtonString);
        _MapTextureOnButton.setState(SharedResources.DGO_Default_MapTexture_On);
        settingsMenu.add(_MusicOnMenuButton);
        settingsMenu.add(_SoundsOnMenuButton);
        settingsMenu.add(_MapTextureOnButton);

        //Add event listening to this JFrame
        _NewGameMenuButton.addActionListener(this);
        _HelpAboutMenuButton.addActionListener(this);
        _HelpControlMenuButton.addActionListener(this);
        _HelpCollisionMenuButton.addActionListener(this);
        _MusicOnMenuButton.addActionListener(this);
        _SoundsOnMenuButton.addActionListener(this);
        _MapTextureOnButton.addActionListener(this);

        this.setJMenuBar(_MenuBar);
    }

    /**
     * Sets the visibility of all screens to false.
     */
    private void HideAllScreens()
    {
        _Screens.stream().forEach(s -> s.setVisible(false));
    }

    /**
     * Displays the launch screen screen JPanel.
     */
    public void NavigateToLaunchScreen()
    {
        HideAllScreens();
        if (_LaunchScreen == null)
        {
            _LaunchScreen = new LaunchScreen(_MenusBackgroundImage);
            _Screens.add(_LaunchScreen);
        }


        _LaunchScreen.get_LogPanel().get_Terminal().Reset();
        _ContentPane.add(_LaunchScreen);
        this.setVisible(true);
        setVisible(true);
        repaint();
    }

    /**
     * Orders the launch screen to select default cars and maps.
     */
    public void SelectDefaultValuesOnLaunchScreen() {
        _LaunchScreen.SelectDefaultValues();
        setVisible(true);
        repaint();
    }


    /**
     * Displays the Server screen (showing clients, messages, server start/stop controllers.
     *
     * @param eventHandler The controller object that handles user interaction on the screen.
     */
    public void NavigateToServerScreen(ServerEngine eventHandler) {
        HideAllScreens();
        if (_ServerScreen == null) {
            _ServerScreen = new ServerScreen(_MenusBackgroundImage, eventHandler);
            _Screens.add(_ServerScreen);
        }

        _ServerScreen.setVisible(true);
        _ContentPane.add(_ServerScreen);
        this.setVisible(true);

    }

    /**
     * Displays the in-game screen (racing map, cars etc).
     */
    public void NavigateToInGameScreen()
    {
        HideAllScreens();
        if (_Screens.contains(_InGameScreen))
            _Screens.remove(_InGameScreen);

        _InGameScreen = new InGameScreen();
        _Screens.add(_InGameScreen);

        _ContentPane.add(_InGameScreen);
        this.setVisible(true);
        _InGameScreen.grabFocus();
    }

    /**
     * This method is called each time the refresh Timer ticks.
     * It asks the in-game screen to update its components (HUD, cars)
     */
    public void UpdateInGameScreen()
    {
        if(_InGameScreen != null)
            _InGameScreen.NextFrame();
    }

    /**
     * Displays game over messages. Called when the game ends/two cars crash.
     */
    public void DisplayGameOverPopup()
    {
        JOptionPane.showMessageDialog(this, SharedResources.GO_GameOver_Message, SharedResources.Go_GameOver_Title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Displays game over messages. Called when the game ends/two cars crash.
     */
    public void DisplayGameOverPopupWithError(String errorMsg) {
        JOptionPane.showMessageDialog(this, SharedResources.GO_GameOverWithError_Message + errorMsg, SharedResources.Go_GameOver_Title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Displays game over message describing that the opponent has dropped out.
     */
    public void DisplayGameOverWithDropoutPopup() {
        JOptionPane.showMessageDialog(this, SharedResources.GO_GameOverWithDropOut_Message, SharedResources.Go_GameOver_Title, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Displays game over messages. Called when the server went down.
     */
    public void DisplayGameOverWithServerDownPopup() {
        JOptionPane.showMessageDialog(this, SharedResources.GO_GameOverWithServerDown_Message, SharedResources.Go_GameOver_Title, JOptionPane.PLAIN_MESSAGE);
    }


    /**
     * Handle events when user clicks on the buttons on the menu bar.
     *
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //If the "Start new game" / "Return to main" button pressed"
        if(e.getSource() == _NewGameMenuButton)
        {
            if(_InGameScreen != null)
            {
                _ContentPane.remove(_InGameScreen);
                _Screens.remove(_InGameScreen);
                _InGameScreen = null;
            }

            SharedResources.MainController.NavigatingBackToMainMenuScreen();
            return;
        }

        //When the Help->About button pressed
        if(e.getSource() == _HelpAboutMenuButton)
        {
            JOptionPane.showMessageDialog(this, SharedResources.MB_Help_About_Content, SharedResources.MB_Help_About_Title,JOptionPane.PLAIN_MESSAGE);
            return;
        }

        //When the Help->Control button pressed
        if (e.getSource() == _HelpControlMenuButton)
        {
            JOptionPane.showMessageDialog(this, SharedResources.MB_Help_Control_Content, SharedResources.MB_Help_Control_Title,JOptionPane.PLAIN_MESSAGE);
            return;
        }

        //When the Help->Collision button pressed
        if (e.getSource() == _HelpCollisionMenuButton) {
            JOptionPane.showMessageDialog(this, SharedResources.MB_Help_Collision_Content, SharedResources.MB_Help_Collision_Title, JOptionPane.PLAIN_MESSAGE);
            return;
        }

        //When Options->Music on button pressed
        if(e.getSource() == _MusicOnMenuButton)
        {
            SharedResources.MainController.GameSettingsMenuBarMusicChanged(_MusicOnMenuButton.getState());
            return;
        }

        //When Options->Sound on button pressed
        if(e.getSource() == _SoundsOnMenuButton)
        {
            SharedResources.MainController.GameSettingsMenuBarSoundChanged((_SoundsOnMenuButton.getState()));
            return;
        }

        //When Options->Map texture button pressed
        if(e.getSource() == _MapTextureOnButton)
        {
            SharedResources.MainController.GameSettingsMenuBarMapTextureChanged(_MapTextureOnButton.getState());
        }
    }

    /**
     * Returns the only instance of the InGameScreen.
     * @return Returns the only instance of the InGameScreen.
     */
    public InGameScreen get_InGameScreen() {
        return _InGameScreen;
    }

    /**
     * Getter for the Server Screen.
     *
     * @return Returns the only instance of the server screen JPanel.
     */
    public ServerScreen get_ServerScreen() {
        return _ServerScreen;
    }

    /**
     * Getter for the only instance of the LaunchScreen.
     *
     * @return Returns the only instance of the LaunchScreen.
     */
    public LaunchScreen get_LaunchScreen() {
        return _LaunchScreen;
    }


}
