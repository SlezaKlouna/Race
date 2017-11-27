package ViewLayer.Screens.MainMenuScr;

import ControlLayer.SharedResources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Acts as the main menu/first screen of the application, which offers options for the user to choose from.
 * It is an extended JPanel. Contains the options as buttons within an another JPanel.
 */
public class MainMenuScreen extends JPanel implements ActionListener
{

    /**
     * The background image for the screen.
     */
    private final Image _BackgroundImg;
    /**
     * Panel containing the menu buttons.
     */
    private JPanel _MenuOptionsContainer;
    /**
     * List of menu buttons
     */
    private ArrayList<JButton> _MenuButtons;


    /**
     * Acts as the main menu/first screen of the application, which offers options for the user to choose from.
     * It is an extended JPanel. Contains the options as buttons within an another JPanel.
     *
     * @param backGround The background image for the screen.
     */
    public MainMenuScreen(Image backGround)
    {
        _BackgroundImg = backGround;
        this.setLayout(null);

        InstantiateMenuButtons();
        AddMenuOptionsToJpanelContainer();
        this.add(_MenuOptionsContainer);

        this.setVisible(true);
    }

    /**
     * Draws the background and the components.
     * @param g The palette to be used to drawing to.
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
     * Instantiating the JPanel which contains the buttons for the menu
     */
    private void AddMenuOptionsToJpanelContainer()
    {
        _MenuOptionsContainer = new JPanel();
        _MenuOptionsContainer.setBounds(GetMainMenuLocation());

        _MenuOptionsContainer.setLayout(null); //No layout manager
        _MenuButtons.stream().forEach(i -> _MenuOptionsContainer.add(i)); //Add each button to the panel
        _MenuOptionsContainer.setVisible(true);
    }

    /**
     * Returns the size and location of the menu containing the buttons. It will be centered to the screen.
     * @return A rectangle representing the size and location of the JPanel which contains the menu buttons.
     */
    private Rectangle GetMainMenuLocation()
    {
        int x = (SharedResources.MW_JFRAME_WIDTH - SharedResources.MMS_Menu_Width) / 2;
        int y = (SharedResources.MW_JFRAME_HEIGHT - SharedResources.MMS_Menu_Height) /2;

        return new Rectangle(x,y,SharedResources.MMS_Menu_Width, SharedResources.MMS_Menu_Height);
    }

    /**
     * Instantiates the buttons used in the menu.
     */
    private void InstantiateMenuButtons()
    {
        //Instantiating buttons
        _MenuButtons = new ArrayList<>();
        _MenuButtons.add(new JButton(SharedResources.MMS_Menu_ConnectToServer));
        _MenuButtons.add(new JButton(SharedResources.MMS_Menu_CreateServer));
        _MenuButtons.add(new JButton(SharedResources.MMS_Menu_Exit));

        //Setting the size of each buttons
        _MenuButtons.stream().forEach(i -> i.setSize(new Dimension(SharedResources.MMS_Menu_Width,SharedResources.MMS_Menu_Option_Height)));

        //Set the location of each buttons (vertical alignment)
        for(int i = 1; i<_MenuButtons.size(); i++)
        {
            _MenuButtons.get(i).setLocation(0,i*SharedResources.MMS_Menu_Option_Height);
        }

        //Set the style of each buttons
        _MenuButtons.stream().forEach(i ->
        {
            i.setBackground(SharedResources.MMS_Button_BackGround_Color);
            i.setFont(SharedResources.MMS_Button_Font);
        });

        //Add event listener for the buttons
        _MenuButtons.stream().forEach(i -> i.addActionListener(this));
    }


    /**
     * Listening for menu button clicks and inform the controller about user intention.
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        //If the first, second, or fourth button raised the event
        if (e.getSource().equals(_MenuButtons.get(0)) || e.getSource().equals(_MenuButtons.get(1)) || e.getSource().equals(_MenuButtons.get(2)))
        {
            //Casting to button
            JButton temp = (JButton)e.getSource();

            //Select the action based on button's text
            switch (temp.getText())
            {
                case SharedResources.MMS_Menu_ConnectToServer:
                    SharedResources.MainController.UserInitiatesConnectingAsClient();
                    break;
                case SharedResources.MMS_Menu_CreateServer:
                    SharedResources.MainController.UserInitiatesCreatingServer();
                    break;
                case SharedResources.MMS_Menu_Exit:
                    SharedResources.MainController.UserInitiatesExit();
                    break;
            }
        }
    }
}
