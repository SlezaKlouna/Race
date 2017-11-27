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
 * Uses a JPanel to display user selectable maps with their names and thumbnail image.
 * The panel's size is calculated dynamically based on the number of selectable maps.
 */
class MapSelectionPanel extends JPanel implements ActionListener, MouseListener {

    /**
     * Contains the labels with map image
     */
    private final ArrayList<MapSelectionLabel> _Maps;
    /**
     * Contains the labels with the maps' name
     */
    private final ArrayList<JLabel> _MapTitleLabels;

    /**
     * An arrow icon facing from left to right. Used when a map gets selected.
     */
    private JLabel _MapSelectorArrowLtoR;
    /**
     * An arrow icon facing from right to left. Used when a map gets selected.
     */
    private JLabel _MapSelectorArrowRtoL;

    /**
     * Uses a JPanel to display user selectable maps with their names and thumbnail image.
     * The panel's size is calculated dynamically based on the number of selectable maps.
     */
    public MapSelectionPanel() {
        _Maps = new ArrayList<>();
        _MapTitleLabels = new ArrayList<>();
        this.setLayout(null);
        this.setBounds(GetPanelBoundSize());
        this.setBackground(SharedResources.MSP_BackgroundColor);

        DisplayHeader();
        DisplayMapSelections();

        setLocation(0, 0);
        this.setVisible(true);
    }

    /**
     * Adds a title text (header) to the map selection panel's top. ("Select Map")
     */
    private void DisplayHeader() {
        JLabel header = new JLabel(SharedResources.MSP_Header_Text);
        header.setFont(SharedResources.MSP_Header_Font);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        header.setVerticalAlignment(SwingConstants.TOP);
        header.setBackground(SharedResources.MSP_Header_Bg_Color);
        header.setSize(SharedResources.MSP_Panel_Width, SharedResources.MSP_Header_Height);
        header.setLocation(0, SharedResources.MSP_Header_Vertical_Spacer);
        header.setOpaque(true);
        header.setVisible(true);

        this.add(header);
    }

    /**
     * Selects the first map from the list by default.
     */
    public void SelectDefaultMap() {
        SelectMap(0);
        SharedResources.MainController.ChangeGameSessionMap(0);
    }

    /**
     * Visually reflects on the selected map by displaying surrounding arrow images
     *
     * @param i The index number of the selectable map. Starts from 0.
     */
    private void SelectMap(int i)
    {
        if (_MapSelectorArrowLtoR == null || _MapSelectorArrowRtoL == null)
        {
            LoadMapSelectorLabels();
        }

        int left_x = _Maps.get(i).getX() - SharedResources.LS_SelectorArrow_Image_Width;
        int left_y = _Maps.get(i).getY() + (SharedResources.MSP_Map_Image_Height / 2);
        _MapSelectorArrowLtoR.setLocation(left_x, left_y);

        int right_x = left_x + SharedResources.MSP_Map_Image_Width + SharedResources.LS_SelectorArrow_Image_Width;
        _MapSelectorArrowRtoL.setLocation(right_x, left_y);
    }

    /**
     * Instantiates the JLabels containing arrow images to represent map selection
     */
    private void LoadMapSelectorLabels()
    {
        _MapSelectorArrowLtoR = new JLabel();
        _MapSelectorArrowRtoL = new JLabel();

        ImageIcon iconLtR =  new ImageIcon(ImageFileLoader.LoadSelectorArrowImage(true));
        ImageIcon iconRtL = new ImageIcon(ImageFileLoader.LoadSelectorArrowImage(false));

        _MapSelectorArrowLtoR.setIcon(iconLtR);
        _MapSelectorArrowRtoL.setIcon(iconRtL);

        _MapSelectorArrowLtoR.setSize(SharedResources.LS_SelectorArrow_Image_Width, SharedResources.LS_SelectorArrow_Image_Height);
        _MapSelectorArrowRtoL.setSize(SharedResources.LS_SelectorArrow_Image_Width, SharedResources.LS_SelectorArrow_Image_Height);

        this.add(_MapSelectorArrowLtoR);
        this.add(_MapSelectorArrowRtoL);

        _MapSelectorArrowLtoR.setVisible(true);
        _MapSelectorArrowRtoL.setVisible(true);
    }

    /**
     * Adds each available map's (name and thumbnail image) as JLabels with to the panel.
     */
    private void DisplayMapSelections()
    {
        for(int i =0; i < SharedResources.MSP_Maps.length; i++)
        {
            //Creating the map MapSelectionLabel for one map
            Image mapImage = ImageFileLoader.LoadMapSelectionImage(SharedResources.MSP_Maps[i]);
            MapSelectionLabel mapLabel = new MapSelectionLabel(mapImage);
            _Maps.add(mapLabel);

            int location_y = (i * (SharedResources.MSP_Map_Image_Height + SharedResources.MSP_Panel_Vertical_Spacer));
            location_y += SharedResources.MSP_Header_Height;
            location_y += SharedResources.MSP_Header_Vertical_Spacer;
            location_y += SharedResources.MSP_Map_From_Header_Spacer;

            int location_x = (SharedResources.MSP_Panel_Width - SharedResources.MSP_Map_Image_Width) / 2;
            mapLabel.setLocation(location_x,location_y);

            this.add(mapLabel);
            mapLabel.setVisible(true);
            mapLabel.addMouseListener(this);

            //Adding description label
            JLabel mapTitle = new JLabel(SharedResources.MSP_Maps[i]);
            mapTitle.setFont(SharedResources.MSP_Map_Title_Font);
            mapTitle.setSize(SharedResources.MSP_Panel_Width,SharedResources.MSP_Map_TitleHeight);
            mapTitle.setHorizontalAlignment(SwingConstants.CENTER);
            mapTitle.setHorizontalTextPosition(SwingConstants.CENTER);
            int titleLocationY = location_y + SharedResources.MSP_Map_Image_Height;
            mapTitle.setLocation(0,titleLocationY);

            _MapTitleLabels.add(mapTitle);
            mapTitle.addMouseListener(this);
            this.add(mapTitle);
            mapTitle.setVisible(true);
        }
    }


    /**
     * Calculates the size of the panel based on the number of available maps.
     * @return The size and position the map selection panel.
     */
    private Rectangle GetPanelBoundSize()
    {
        int height = (SharedResources.MSP_Maps.length * (SharedResources.MSP_Map_Image_Height  + SharedResources.MSP_Panel_Vertical_Spacer));
        height += SharedResources.MSP_Header_Height;
        height += SharedResources.MSP_Header_Vertical_Spacer;
        height += SharedResources.MSP_Map_From_Header_Spacer;

        return new Rectangle(0,0,SharedResources.MSP_Panel_Width,height);
    }


    /**
     * Handling when one of the maps from the list gets clicked by the user.
     * That map will be selected.
     *
     * @param e The MouseEvent.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        try {
            JLabel sender = (JLabel) e.getSource();
            int i;

            //Search for the sender map's index in the maps list.
            for (i = 0; i < _Maps.size(); i++) {
                if (_Maps.get(i) == sender || _MapTitleLabels.get(i) == sender) {
                    break;
                }
            }

            //Select the sender map and notify the controller
            if (i < _Maps.size()) {
                SelectMap(i);
                SharedResources.MainController.ChangeGameSessionMap(i);
            }

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    /**
     * Same action as when the mouse gets clicked: the sender map gets selected.
     *
     * @param e The MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent e) {
        mouseClicked(e);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //No action
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
}
