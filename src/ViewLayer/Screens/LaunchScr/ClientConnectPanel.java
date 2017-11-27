package ViewLayer.Screens.LaunchScr;

import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;

/**
 * Allows the user to type in the servers address and port number.
 * Used on the LaunchScreen to provide a UI for the connection settings.
 */
public class ClientConnectPanel extends JPanel {

    /**
     * User enters server's IP here
     */
    private final JTextField _ServerIP = new JTextField(SharedResources.LS_ClientPanel_IP_Default_TEXT);

    /**
     * Port selection box.
     */
    private final JComboBox<Integer> _Ports = new JComboBox<>(SharedResources.SCP_VALID_PORT_NUMBERS);

    /**
     * Allows the user to type in the servers address and port number.
     * Used on the LaunchScreen to provide a UI for the connection settings.
     */
    public ClientConnectPanel() {
        setLayout(null);
        setBackground(new Color(255, 255, 255));
        setSize(300, 80);

        //Main title settings
        JLabel _TitleLabel = new JLabel(SharedResources.LS_ClientPanel_Header_CONNECTION_SETTINGS);
        _TitleLabel.setLocation(0, 5);
        _TitleLabel.setSize(300, 20);
        _TitleLabel.setFont(SharedResources.CSP_Header_Font);
        _TitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        _TitleLabel.setBackground(SharedResources.CSP_Header_Color);
        _TitleLabel.setOpaque(true);


        //Server IP title settings
        JLabel _ServerIPTile = new JLabel(SharedResources.LS_ClientPanel_ENTER_SERVER_ADDRESS);
        _ServerIPTile.setLocation(10, 35);
        _ServerIPTile.setSize(180, 20);
        _ServerIPTile.setFont(SharedResources.SRS_FontFaceMedium);

        //Server IP text settings
        _ServerIP.setLocation(10, 55);
        _ServerIP.setSize(180, 20);
        _ServerIP.setEditable(true);
        _ServerIP.setBackground(new Color(225, 225, 225));
        _ServerIP.setFont(SharedResources.SRS_FontFaceMedium);

        //Ports title label settings
        JLabel _PortsTitle = new JLabel(SharedResources.LS_ClientPanel_PORT_Text);
        _PortsTitle.setLocation(200, 35);
        _PortsTitle.setSize(80, 20);
        _PortsTitle.setFont(SharedResources.SRS_FontFaceMedium);

        //Ports combobox settings
        _Ports.setLocation(200, 55);
        _Ports.setSize(80, 20);
        _Ports.setFont(SharedResources.SRS_FontFaceMedium);

        //Adding components to the panel
        add(_TitleLabel);
        add(_ServerIP);
        add(_ServerIPTile);
        add(_Ports);
        add(_PortsTitle);

        setLocation(0, 0);
        setVisible(true);
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
     * Getter for the server IP or host address text
     *
     * @return Returns the IP address or hostname that the user typed in.
     */
    public String GetServerAddress() {
        return _ServerIP.getText();
    }


}
