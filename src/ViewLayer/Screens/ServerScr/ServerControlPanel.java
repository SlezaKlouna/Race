package ViewLayer.Screens.ServerScr;

import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;

/**
 * Provides the user with an interface to start and stop a server, select ports, and displays the detected
 * local hosts' ip or hostname. Used by the ServerScreen
 */
class ServerControlPanel extends JPanel {

    /**
     * Displays server's detected IP
     */
    private final JTextArea _ServerIP = new JTextArea(" ", 1, 1);

    /**
     * Port selection box.
     */
    private final JComboBox<Integer> _Ports = new JComboBox<>(SharedResources.SCP_VALID_PORT_NUMBERS);

    /**
     * Start or stop server button
     */
    private final JButton _StartStopButton = new JButton(SharedResources.SRS_START_SERVER);

    /**
     * Provides the user with an interface to start and stop a server, select ports, and displays the detected
     * local hosts' ip or hostname.
     */
    public ServerControlPanel() {
        setLayout(null);
        setBackground(new Color(255, 255, 255));
        setSize(450, 80);

        //Main title settings
        JLabel _TitleLabel = new JLabel(SharedResources.SCP_SERVER_LAUNCHER);
        _TitleLabel.setLocation(0, 5);
        _TitleLabel.setSize(450, 30);
        _TitleLabel.setFont(SharedResources.SRS_FontFaceLarge);
        _TitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //Server IP title settings
        JLabel _ServerIPTile = new JLabel(SharedResources.SCP_SERVER_S_DETECTED_IP_Label);
        _ServerIPTile.setLocation(10, 35);
        _ServerIPTile.setSize(150, 20);
        _ServerIPTile.setFont(SharedResources.SRS_FontFaceMedium);

        //Server IP text settings
        _ServerIP.setLocation(10, 55);
        _ServerIP.setSize(150, 20);
        _ServerIP.setEditable(false);
        _ServerIP.setBackground(new Color(225, 225, 225));
        _ServerIP.setFont(SharedResources.SRS_FontFaceMedium);

        //Ports title label settings
        JLabel _PortsTitle = new JLabel(SharedResources.SCP_PORTS_TEXT);
        _PortsTitle.setLocation(170, 35);
        _PortsTitle.setSize(80, 20);
        _PortsTitle.setFont(SharedResources.SRS_FontFaceMedium);

        //Ports combobox settings
        _Ports.setLocation(170, 55);
        _Ports.setSize(80, 20);
        _Ports.setFont(SharedResources.SRS_FontFaceMedium);

        //Start or stop button settings
        _StartStopButton.setLocation(275, 55);
        _StartStopButton.setSize(150, 20);
        _StartStopButton.setFont(SharedResources.SRS_FontFaceMedium);
        _StartStopButton.setBackground(new Color(255, 255, 255));

        //Adding components to the panel
        add(_TitleLabel);
        add(_ServerIP);
        add(_ServerIPTile);
        add(_Ports);
        add(_PortsTitle);
        add(_StartStopButton);

        setLocation(0, 0);
        setVisible(true);
    }

    /**
     * Assigns an event listener to the start-stop button.
     *
     * @param eventHandler The object which handles the events.
     * @return Returns a reference to the start-stop button
     */
    public JButton AddStartButtonEventListener(ServerScreen eventHandler) {
        _StartStopButton.addActionListener(eventHandler);
        return _StartStopButton;
    }

    /**
     * Returns the currently selected port number.
     *
     * @return The currently selected port number.
     */
    public int GetSelectedPort() {
        return (int) _Ports.getSelectedItem();
    }

    /**
     * Changes the IP or host address within the user read-only textarea.
     *
     * @param ip The new ip or host address to display
     */
    public void SetServerAddress(String ip) {
        _ServerIP.setText(ip);
    }
}
