package ControlLayer;

import ModelLayer.Networking.Server;
import ViewLayer.SwingUICore;

/**
 * Instantiates, starts and stops the Server based on user interaction.
 * Also commands tha SwingUICore to load up the server screen.
 */
public class ServerEngine {

    /**
     * The top level JFRame where the ServerScreen JPanel loaded
     */
    private final SwingUICore _UICore;

    /**
     * The server logic
     */
    private Server _Server;

    /**
     * Constructor.
     * Instantiates, starts and stops the Server based on user interaction.
     * Also commands tha SwingUICore to load up the server screen.
     * @param UICore The top level JFRame where the ServerScreen JPanel will be loaded
     */
    public ServerEngine(SwingUICore UICore) {
        _UICore = UICore;
    }


    /**
     * Asks the top level JFrame to load the the server configuration screen.
     */
    public void LaunchServerConfiguration() {
        _UICore.NavigateToServerScreen(this);
        _UICore.get_ServerScreen().ResetTerminals();

        InstantiateServer();
        _UICore.get_ServerScreen().SetIP(_Server.GetLocalHostName());
    }


    /**
     * Instantiates the Server (but does not launch it).
     */
    private void InstantiateServer() {
        //Instantiating the server if needed
        if (_Server == null) {
            _Server = new Server(_UICore.get_ServerScreen().GetMessageLogTerminal(), this);
        }
    }

    /**
     * When user clicks on the start or stop button on the server screen
     * it either stops the running server or launches it on the selected port.
     */
    public void ServerStartStopButtonPressed() {

        if (!_Server.IsRunning()) {
            _Server.StartServer(_UICore.get_ServerScreen().GetSelectedPortNumber());
        } else {
            _Server.StopServer();
        }
    }

    /**
     * Shuts down the server if it is running currently.
     */
    public void Shutdown() {
        if (_Server != null) {
            if (_Server.IsRunning()) {
                _Server.StopServer();
            }
        }

        _UICore.get_ServerScreen().SetStartButtonText(false);
    }


    /**
     * Called when user clicks on the return to main button on the Server Screen UI.
     */
    public void ReturnToMainButtonPressed() {
        Shutdown();
        //_UICore.get_ServerScreen().SetStartButtonText(false);
        SharedResources.MainController.NavigatingBackToMainMenuScreen();
    }

    /**
     * Called ny the server to notify the controller that the server is launched successfully.
     * In return, the controller asks the UI to change the text of the start-stop button accordingly.
     */
    public void ServerIsUp() {
        _UICore.get_ServerScreen().SetStartButtonText(true);
    }

    /**
     * Called ny the server to notify the controller that the server is stopped.
     * In return, the controller asks the UI to change the text of the start-stop button accordingly.
     */
    public void ServerIsDown() {
        _UICore.get_ServerScreen().SetStartButtonText(false);
    }
}
