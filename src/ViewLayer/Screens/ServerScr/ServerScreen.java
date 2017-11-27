package ViewLayer.Screens.ServerScr;

import ControlLayer.ServerEngine;
import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Contains all the elements (other JPanels, labels, buttons) that are shown when server hosting function is used.
 */
public class ServerScreen extends JPanel implements ActionListener {


    /**
     * The background image of the screen.
     */
    private final Image _BackgroundImg;

    /**
     * Displaying latest communication messages or server notifications.
     */
    private final LogPanel _MessageLog = new LogPanel(false);

    /**
     * Return to main menu button.
     */
    private final JButton _BackToMainButton = new JButton(SharedResources.SRS_CLOSE_SERVER_AND_RETURN_TO_MAIN);

    /**
     * JPanel containing server's detected IP, selectable port number, and server start/stop button.
     */
    private final ServerControlPanel _ControlPanel = new ServerControlPanel();

    /**
     * The controller that needs to be notified when user interacts with the UI elements.
     */
    private final ServerEngine _ServerEngine;

    /**
     * Reference to the start button within the Control Panel.
     */
    private final JButton _StartServerButton;


    /**
     * Contains all the elements (other JPanels, labels, buttons) that are shown when server hosting function is used.
     *
     * @param backgroundImg The background to be used.
     * @param eventHandler  The object that captures the server start or stop button pressing events.
     */
    public ServerScreen(Image backgroundImg, ServerEngine eventHandler) {
        this.setLayout(null);
        _BackgroundImg = backgroundImg;
        _ServerEngine = eventHandler;

        //Add Control panel
        add(_ControlPanel);
        _ControlPanel.setLocation(200, 10);
        _ControlPanel.setVisible(true);
        _StartServerButton = _ControlPanel.AddStartButtonEventListener(this);

        //Add message log panel
        add(_MessageLog);
        _MessageLog.setLocation(10, 100);
        _MessageLog.setVisible(true);

        //Add return to main button
        add(_BackToMainButton);
        _BackToMainButton.setSize(300, 30);
        _BackToMainButton.setLocation(275, 560);
        _BackToMainButton.setBackground(Color.WHITE);
        _BackToMainButton.setFont(SharedResources.SRS_FontFaceMedium);
        _BackToMainButton.setVisible(true);
        _BackToMainButton.addActionListener(this);

        setVisible(true);
    }


    /**
     * Draws the background and paints the components.
     *
     * @param g The palette to be painted to.
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            g.drawImage(_BackgroundImg, 0, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for the terminal object within the message log panel
     *
     * @return The terminal object within the message log panel
     */
    public LogTerminal GetMessageLogTerminal() {
        return _MessageLog.get_Terminal();
    }


    /**
     * Returns the user selected portnumber from the ports combobox.
     * @return One of the three possible port numbers the user can select.
     */
    public int GetSelectedPortNumber() {
        return _ControlPanel.GetSelectedPort();
    }

    /**
     * Clears the texts from the terminals' screens.
     */
    public void ResetTerminals() {
        _MessageLog.get_Terminal().Reset();
    }

    /**
     * When the start or stop button gets pressed
     * @param e The event object
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == _StartServerButton) {
            _ServerEngine.ServerStartStopButtonPressed();
            return;
        }

        if (e.getSource() == _BackToMainButton) {
            _ServerEngine.ReturnToMainButtonPressed();
        }
    }


    /**
     * Changes the start-stop server button text according to the servers' current state.
     *
     * @param isButtonToDisplayStop True to change the text stopping the server. False to change the text to starting it.
     */
    public void SetStartButtonText(boolean isButtonToDisplayStop) {
        if (isButtonToDisplayStop)
            _StartServerButton.setText(SharedResources.SRS_STOP_SERVER);
        else
            _StartServerButton.setText(SharedResources.SRS_START_SERVER);

    }

    /**
     * Changes the IP or host address within the user read-only textarea.
     * @param ip The new ip or host address to display
     */
    public void SetIP(String ip) {
        _ControlPanel.SetServerAddress(ip);
    }
}
