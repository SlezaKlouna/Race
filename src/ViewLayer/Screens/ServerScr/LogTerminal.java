package ViewLayer.Screens.ServerScr;

import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;

/**
 * An uneditable (for the user) textarea which displays system/networking messages, logs.
 * Used on the server screen and the launch screen.
 */
public class LogTerminal extends JTextArea {

    /**
     * An uneditable (for the user) textarea which displays system/networking messages, logs.
     * Used on the server screen and the launch screen.
     */
    public LogTerminal() {
        setBackground(Color.black);
        setForeground(Color.WHITE);
        setFont(SharedResources.SRS_FontFaceMedium);
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);

    }


    /**
     * Adds a text to the terminal output.
     *
     * @param msg The message to be added.
     */
    public void Log(String msg) {
        append(msg);
        append("\n");
    }

    /**
     * Deletes the current content of the terminal.
     */
    public void Reset() {
        setText("");
    }
}
