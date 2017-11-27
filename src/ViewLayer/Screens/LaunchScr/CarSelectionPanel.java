package ViewLayer.Screens.LaunchScr;

import ControlLayer.SharedResources;
import ModelLayer.FileLoaders.ImageFileLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Uses a JPanel to display user selectable car images.
 * The panel's size is calculated dynamically based on the number of selectable cars.
 */
class CarSelectionPanel extends JPanel implements ActionListener, MouseListener
{

    /**
     * List of labels displaying a car individually.
     */
    private ArrayList<JLabel> _Cars;
    /**
     * An arrow icon facing from left to right. Used when a car gets selected.
     */
    private JLabel _CarSelectorArrowLtoR;
    /**
     * An arrow icon facing from right to left. Used when a car gets selected.
     */
    private JLabel _CarSelectorArrowRtoL;

    /**
     * Uses a JPanel to display user selectable car images.
     * The panel's size is calculated dynamically based on the number of selectable cars.
     */
    public CarSelectionPanel()
    {
        CreatePlayerHeaderLabel();
        this.setBounds(GetCarSelectionHolderPosition());
        this.setBackground(SharedResources.CSP_Selectable_Car_Holder_Color);
        DisplayCarSelections();

        this.setVisible(true);
    }


    /**
     * Selects the first car from the lists to be default.
     */
    public void SelectFirstCarByDefault() {
        SharedResources.MainController.ChangeLocalSelectedCarType(SharedResources.PLAYER_1, 0);
            setVisible(true);
            repaint();
    }


    /**
     * Visually reflects on the selected car by displaying surrounding arrow images
     * @param i The index number of the selectable car. Starts from 0.
     */
    private void SelectCar(int i)
    {
        if(_CarSelectorArrowLtoR == null || _CarSelectorArrowRtoL == null)
        {
           LoadCarSelectorLabels();
        }

        int left_x = _Cars.get(i).getX() - SharedResources.LS_SelectorArrow_Image_Width;
        int left_y = _Cars.get(i).getY() + (SharedResources.CSP_Selectable_Car_Image_Size / 2);
        _CarSelectorArrowLtoR.setLocation(left_x, left_y);
        _CarSelectorArrowLtoR.setVisible(true);

        int right_x = left_x + SharedResources.CSP_Selectable_Car_Image_Size + SharedResources.LS_SelectorArrow_Image_Width;
        _CarSelectorArrowRtoL.setLocation(right_x, left_y);
        _CarSelectorArrowRtoL.setVisible(true);
    }


    /**
     * Instantiates the JLabels containing arrow images to represent car selection
     */
    private void LoadCarSelectorLabels()
    {
        _CarSelectorArrowLtoR = new JLabel();
        _CarSelectorArrowRtoL = new JLabel();

        ImageIcon iconLtR =  new ImageIcon(ImageFileLoader.LoadSelectorArrowImage(true));
        ImageIcon iconRtL = new ImageIcon(ImageFileLoader.LoadSelectorArrowImage(false));

        _CarSelectorArrowLtoR.setIcon(iconLtR);
        _CarSelectorArrowRtoL.setIcon(iconRtL);

        _CarSelectorArrowLtoR.setSize(SharedResources.LS_SelectorArrow_Image_Width, SharedResources.LS_SelectorArrow_Image_Height);
        _CarSelectorArrowRtoL.setSize(SharedResources.LS_SelectorArrow_Image_Width, SharedResources.LS_SelectorArrow_Image_Height);

        this.add(_CarSelectorArrowLtoR);
        this.add(_CarSelectorArrowRtoL);
    }


    /**
     * Adds a JLabel header containing text instruction to select car ("Select car")
     */
    private void CreatePlayerHeaderLabel()
    {
        Rectangle measures = GetCarSelectionHolderPosition();
        Dimension sizing = new Dimension(measures.width,SharedResources.CSP_Header_Height);
        JLabel j = new JLabel(SharedResources.CSP_Header_Message_After_Player_Number);

        j.setLocation(0,0);
        j.setSize(sizing);
        j.setMinimumSize(sizing);
        j.setPreferredSize(sizing);

        j.setFont(SharedResources.CSP_Header_Font);
        j.setHorizontalTextPosition(SwingConstants.CENTER);
        j.setHorizontalAlignment(SwingConstants.CENTER);
        j.setOpaque(true);
        j.setBackground(SharedResources.CSP_Header_Color);

        this.add(j);
    }

    /**
     * Calculates the on-screen position of the car selection JPanel. By default it is the center of the screen.
     * @return The size and location as a rectangle.
     */
    private Rectangle GetCarSelectionHolderPosition()
    {
        int spacing = SharedResources.CSP_Selectable_Car_Holder_Spacing;

        int width = SharedResources.CSP_Number_Of_Selectable_Cars * (SharedResources.CSP_Selectable_Car_Image_Size + spacing);
        int height = SharedResources.CSP_Selectable_Car_Image_Size + spacing;

        int x = (SharedResources.MW_JFRAME_WIDTH - width) / 2;
        int y = (SharedResources.MW_JFRAME_HEIGHT - height) / 2;

        return new Rectangle(x,y,width,height);
    }

    /**
     * Instantiates and adds the selection of cars to the containing JPanel
     */
    private void DisplayCarSelections()
    {
        _Cars = new ArrayList<>();

        for(int i = 0; i<SharedResources.CSP_Number_Of_Selectable_Cars; i++)
        {
            JLabel label = new JLabel();

            Image img = ImageFileLoader.LoadCarImageFromFileForLaunchScreen(i); //Load image from file

            if(img != null) //If image loading was successful
            {
                ImageIcon icon = new ImageIcon(img);
                label.setIcon(icon);
            }
            else
            {
                label.setText(SharedResources.CSP_Error_Selectable_Car_Image_Missing);
            }

            label.setSize(SharedResources.CSP_Selectable_Car_Image_Size,SharedResources.CSP_Selectable_Car_Image_Size);
            this.add(label);
            label.setVisible(true);
            _Cars.add(label);
            label.addMouseListener(this);

            //Add empty spacer labels between the car labels
            if(i != SharedResources.CSP_Number_Of_Selectable_Cars -1) {
                JLabel spacer = new JLabel("        "); //This is a workaround as the SetSize() is getting ignored by the layout manager.
                spacer.setVisible(true);
                this.add(spacer);
            }
        }
    }


    /**
     * Handles when mouse clicked to one of the car icons (JLabels).
     * In this case, the action sender car gets selected.
     * @param e The MouseEvent.
     */
    @Override
    public void mouseClicked(MouseEvent e)
    {
        try
        {
            JLabel sender = (JLabel)e.getSource();

            int i;
            for(i =0; i < _Cars.size(); i++)
            {
                if(_Cars.get(i) == sender)
                {
                    break;
                }
            }

            if(i < _Cars.size())
            {
                SelectCar(i);
                SharedResources.MainController.ChangeLocalSelectedCarType(SharedResources.PLAYER_1, i);
            }

        } catch (Exception x)
        {
            x.printStackTrace();
        }
    }


    /**
     * Same action as MouseClicked.
     * @param e The MouseEvent.
     */
    @Override
    public void mousePressed(MouseEvent e)
    {
        mouseClicked(e);
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        //No action
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //No action
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //No action
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //No action
    }
}
