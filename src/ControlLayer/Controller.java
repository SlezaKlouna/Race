package ControlLayer;

import ModelLayer.Networking.Client;
import ViewLayer.SoundEngine;
import ViewLayer.SwingUICore;

/**
 * Switches between the main flows of the application.
 * Acts as the "Maestro" in an orchestra, and there is only one single instance of it.
 */
public class Controller {

    private SwingUICore _UICore; //The top level Swing JFrame that loads other panels to display content.
    private GameEngine _GameEngine; //Fully controls one game scenario
    private SoundEngine _SoundEngine; //Responsible of playing music and sounds
    private ServerEngine _ServerEngine; //Responsible to manage the server service
    private Client _Client; //Acts as a client to connect to the game server and send/receive game data.

    /**
     * Set to be true when the user connects to the server to find an opponent to play with.
     * It allows preventing multiple requests to be sent while waiting for the server.
     */
    private boolean _IsWaitingForOpponentCallback = false;

    /**
     * Display the Swing based UI, starting with the main menu when the program launches
     */
    public void ApplicationStartUp()
    {
        //Launch the SWING based UI
        _UICore = new SwingUICore();
        _UICore.CreateDefaultWindow();

        //Display the main menu screen after start
        _UICore.NavigateToMainMenuScreen();

        //Launch the Sound Engine
        _SoundEngine = new SoundEngine();

    }

    /** Navigate to the server hosting scree.
     * This method is called when user selects the "Create a server" on the main menu.
     */
    public void UserInitiatesCreatingServer()
    {
        if (_ServerEngine == null) {
            _ServerEngine = new ServerEngine(_UICore);
        }

        _ServerEngine.LaunchServerConfiguration();
    }

    /** Navigate to the car selection screen and initiate a client game.
     * This method is called when user selects the "Connect to a server" on the main menu.
     */
    public void UserInitiatesConnectingAsClient()
    {
        CurrentGameSession.CreatePlayers();
        NavigateToCarSelectionWithDefaults();
    }

    /**
     * Orders the top JFrame to load the LaunchScreen JPanel.
     */
    private void NavigateToCarSelectionWithDefaults() {
        _UICore.NavigateToLaunchScreen();
        _UICore.SelectDefaultValuesOnLaunchScreen();

        //Create client and pass the Launch Screen terminal as output.
        if (_Client == null)
            _Client = new Client(_UICore.get_LaunchScreen().get_LogPanel().get_Terminal());
    }

    /** Navigate to the main menu screen and resets the game session.
     * This can be called when the user either clicks on the "new game" option on the MenuBar
     * or when a game finishes and the player will be put back to the main menu.
     * Also called when user closes down the server to return to the main.
     */
    public void NavigatingBackToMainMenuScreen() {
        //If this method was called why the game is running, then destroy the GameEngine controller.
        if(_GameEngine != null)
        {
            _GameEngine.ShutDown();
            _GameEngine = null;
        }

        //Shut down the server if it is running.
        if (_ServerEngine != null) {
            _ServerEngine.Shutdown();
        }

        //Shut down client if running.
        if (_Client != null) {
            _Client.TryCloseCurrentConnection(true);
        }

        //Reset the game session and direct the top JFrame to show the MainMenuScreen JPanel
        CurrentGameSession.ResetGameSession();
        _UICore.NavigateToMainMenuScreen();
    }

    /** Changes the selected car for a player in a game session.
     *  This method is called from the LaunchScreen JPanel.
     *
     *  @param PlayerNumber The number of the player to change the car for. Starts from 1.
     *  @param CarIndex The index number of the selected car. Starts from 0.
    */
    public void ChangeLocalSelectedCarType(int PlayerNumber, int CarIndex)
    {
        CurrentGameSession.get_CurrentPlayers().get(PlayerNumber-1).get_Car().set_CarImageFileIndex(CarIndex);
        CurrentGameSession.set_LocalPlayerSelectedCarTypeIndex(CarIndex);
    }

    /** Changes the selected map. This is called from the LaunchScreen JPanel.
     * @param MapNumber The index number of the selected map. Starts from 0.
     */
    public void ChangeGameSessionMap(int MapNumber)
    {
        CurrentGameSession.set_SelectedMapName(SharedResources.MSP_Maps[MapNumber]);
    }

    /** Instantiates the GameEngine, which takes over the control to launch a new game.
     * This is called from the LaunchScreen JPanel when user clicks on "start".
     */
    public void LaunchGame()
    {
        if (!_Client.is_IsThreadWaitingForMapResponse()) {
            String servAddr = _UICore.get_LaunchScreen().get_ClientConnectionPanel().GetServerAddress();
            int port = _UICore.get_LaunchScreen().get_ClientConnectionPanel().GetSelectedPort();
            boolean isConnectionSucces = _Client.ConnectToServer(servAddr, port);

            if (isConnectionSucces) {
                //Request sent. Waiting for callback.
                _IsWaitingForOpponentCallback = true;
                _Client.RequestOpponent(CurrentGameSession.get_SelectedMapName(), CurrentGameSession.get_LocalPlayerSelectedCarTypeIndex());
            }
        } else {
            _Client.NotifyUserThatAlreadyWaitingForOpponent();
        }
    }

    /**
     * This method is called when the client player request the server to find other opponents for him.
     * Waiting for the servers answer happens on a seperate thread and this method gets called when server responds.
     */
    public void OpponentFoundCallback() {
        //Opponent found, so start the game if the user is still waiting..
        if (_IsWaitingForOpponentCallback) {
            //Set the car design (type) for the remote car
            int remoteCarDesignIndex = _Client.get_MapResponse().get_CarImageFileIndex();
            CurrentGameSession.get_CurrentPlayers().get(1).get_Car().set_CarImageFileIndex(remoteCarDesignIndex);

            //If the server decided that this client is not player 1, then player needs to be switched up.
            //Note: A client defaults to Player 1 until the server decides otherwise.
            if (_Client.get_MapResponse().get_GivenPlayerNumber() != SharedResources.PLAYER_1)
                CurrentGameSession.SwitchPlayerNumbers();

            _IsWaitingForOpponentCallback = false;
            _GameEngine = new GameEngine(_UICore, _SoundEngine);
            _GameEngine.StartGame();

        } else {
            //The user decided to not to wait (e.g. returned to the main menu)
            //Lets notify the server about the client dropped.
            _Client.SendPlayerDroppedMessage();
        }
    }

    /** Directs the SoundEngine to turn on/off the background music.
     * This is called when user clicks on the relevant option on the MenuBar.
     * @param newState True to turn on the music. False for turning it off.
     */
    public void GameSettingsMenuBarMusicChanged(boolean newState)
    {
        SharedResources.DGO_Default_Music_On = newState;
        _SoundEngine.SetBackgroundMusic(newState);
    }

    /** Directs the SoundEngine to turn on/off the in-game sounds.
     * This is called when user clicks on the relevant option on the MenuBar.
     * @param newState True to turn on the sounds. False for turning them off.
     */
    public void GameSettingsMenuBarSoundChanged(boolean newState)
    {
        SharedResources.DGO_Default_Sound_On = newState;
    }

    /** Sets the in-game map texture on or off.
     * If the texture is off, then border lines will be drawn out instead.
     * @param newState True to keep the texture on. False for turning it off.
     */
    public void GameSettingsMenuBarMapTextureChanged(boolean newState)
    {
        SharedResources.DGO_Default_MapTexture_On = newState;
    }

    /**
     * Getter for the instantiated GameEngine.
     * @return Returns the GameEngine instance.
     */
    public GameEngine get_GameEngine() {
        return _GameEngine;
    }

    /**
     * Shuts down the application.
     * Called when user clicks on the "Exit" in the main menu.
     */
    public void UserInitiatesExit()
    {
        _SoundEngine.SetBackgroundMusic(false);
        _UICore.dispose();
    }

    /**
     * Called when the user tries to close the window.
     * It tries to close the client and server connections before exit.
     */
    public void WindowClosingHandler() {
        if (_Client != null)
            _Client.TryCloseCurrentConnection(true);
        if (_ServerEngine != null)
            _ServerEngine.Shutdown();
    }

    /**
     * Getter for the only Client instance.
     *
     * @return Return the only Client instance which communicates with the server.
     */
    public Client get_Client() {
        return _Client;
    }
}
