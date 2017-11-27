package ViewLayer.Screens.ServerScr;


import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;

/**
 * Displays read-only information to the user by using a read-only textarea and a JLabel title.
 */
public class LogPanel extends JPanel {

    /**
     * Title text on the top of the panel
     */
    private final JLabel _PanelTitle = new JLabel();

    /**
     * The system messages are displayed in this uneditable textarea
     */
    private final LogTerminal _Terminal = new LogTerminal();
    /**
     * Adds scrollbars to the _Terminal
     */
    private JScrollPane _Scroll = null;


    /**
     * Displays read-only information to the user by using a read-only textarea and a JLabel title.
     * @param isClientSize true to use a size and header suitable for the client on the LaunchScreen. False for ServerScreen.
     */
    public LogPanel(boolean isClientSize) {
        setLayout(null);
        setBackground(new Color(240, 240, 240));


        _PanelTitle.setLocation(0, 10);
        _PanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        _PanelTitle.setFont(SharedResources.SRS_FontFaceLarge);

        if (!isClientSize) {
            _PanelTitle.setText(SharedResources.SRV_SERVER_LOG_TEXT);
            ConfigureServerScreenSize();
        } else {
            _PanelTitle.setText(SharedResources.SRV_CONNECTION_STATUS_TEXT);
            ConfigureLaunchScreenSize();
        }
        add(_PanelTitle);
        add(_Scroll);

        setLocation(0, 0);
        setVisible(true);
    }


    /**
     * Sets the the size and header for server screen use.
     */
    private void ConfigureServerScreenSize() {
        setSize(830, 450);
        _PanelTitle.setSize(830, 30);

        _Terminal.setSize(810, 10000);
        _Terminal.setLocation(10, 40);
        _Terminal.setPreferredSize(new Dimension(810, 6000));

        _Scroll = new JScrollPane(_Terminal);
        _Scroll.setSize(810, 400);
        _Scroll.setLocation(10, 40);
        _Scroll.setPreferredSize(new Dimension(830, 6000));
        _Scroll.setVisible(true);
    }

    /**
     * Sets the size and header for LaunchScreen use
     */
    private void ConfigureLaunchScreenSize() {
        setSize(560, 250);
        _PanelTitle.setSize(560, 20);
        _PanelTitle.setFont(SharedResources.CSP_Header_Font);


        _Terminal.setSize(540, 1000);
        _Terminal.setLocation(10, 30);
        _Terminal.setPreferredSize(new Dimension(540, 1000));

        _Scroll = new JScrollPane(_Terminal);
        _Scroll.setSize(540, 210);
        _Scroll.setLocation(10, 30);
        _Scroll.setPreferredSize(new Dimension(540, 1000));
        _Scroll.setVisible(true);

    }


    /**
     * Getter for the terminal.
     *
     * @return Returns the terminal like textarea which is read only for the user.
     */
    public LogTerminal get_Terminal() {
        return _Terminal;
    }


}
