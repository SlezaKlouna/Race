package ControlLayer;

import ModelLayer.CarRemote;
import ModelLayer.CollisionManagement.CollisionManager;
import ModelLayer.MapML.MapModel;
import ModelLayer.Player;
import ViewLayer.MapVL.MapView;

import java.util.ArrayList;

/**
 * Represents one game session, containing references to all the participant objects and related current game settings.
 */
public class CurrentGameSession
{
    private static ArrayList<Player> _CurrentPlayers; //Contains the instantiated player objects (2 instances)
    private static String _SelectedMapName; //The string name of the selected map, based on SharedResources.MSP_Maps (Easy, Medium)
    private static CollisionManager _CollisionManager; //Instance of the CollisionManager that calculates the collisions
    private static MapView _MapView; //The view layer object of the selected map (this draws on screen)
    private static MapModel _MapModel; //The model layer object of the selected map (this contains the MapObjects needed for collision detection)
    private static int _RealPlayerIndex; //Represents the player index number who is sitting in front of the client (non-remote);
    private static int _LocalPlayerSelectedCarTypeIndex; //Represents the car type (design) that the local user selected.

    /**
     * Getter for the MapView
     *
     * @return The view layer object of the selected map. This is returned as the general parent MapModel.
     */
    public static MapView get_MapView() {
        return _MapView;
    }

    /**
     * Setter for the MapView. This is called by the GameEngine.
     * @param _MapView A child of the MapView parent, which represents the view layer of a map.
     */
    public static void set_MapView(MapView _MapView) {
        CurrentGameSession._MapView = _MapView;
    }

    /**
     * Getter for the MapModel.
     * @return A child of the MapModel parent, which represents the model layer of a map.
     */
    public static MapModel get_MapModel() {
        return _MapModel;
    }

    /**
     * Setter for the MapModel.
     * @param _MapModel A child of the MapModel parent, which represent the model layer of a map.
     */
    public static void set_MapModel(MapModel _MapModel) {
        CurrentGameSession._MapModel = _MapModel;
    }


    /**
     * Instantiates the Player objects.
     */
    public static void CreatePlayers() {
        _CurrentPlayers = new ArrayList<>();
        _CurrentPlayers.add(new Player("You", false));
        _CurrentPlayers.add(new Player("Opponent", true));
        _RealPlayerIndex = 0;
    }

    /**
     * Getter for the CurrentPlayers
     * @return Returns a list of Player objects. This list could contain 1 or 2 players.
     */
    public static ArrayList<Player> get_CurrentPlayers() {
        return _CurrentPlayers;
    }

    /**
     * Getter for the SelectedMap
     * @return The string name of the selected map, based on SharedResources.MSP_Maps (Easy, Medium)
     */
    public static String get_SelectedMapName() {
        return _SelectedMapName;
    }

    /**
     * Setter for the Selected map.
     * @param _SelectedMapName Must be a string name of the selected map, based on SharedResources.MSP_Maps (Easy, Medium)
     */
    public static void set_SelectedMapName(String _SelectedMapName)
    {
        CurrentGameSession._SelectedMapName = _SelectedMapName;
    }

    /**
     * Returns the only CollisionManager object instance of the game, which is used to calculate collisions
     * @return Returns the only CollisionManager object instance of the game.
     */
    public static CollisionManager get_CollisionManager() {
        return _CollisionManager;
    }

    /**
     * Setter for the CollisionManager. Called by the GameEngine.
     * @param _CollisionManager The CollisionManager instance to use.
     */
    public static void set_CollisionManager(CollisionManager _CollisionManager) {
        CurrentGameSession._CollisionManager = _CollisionManager;
    }

    /**
     * Resets all the CurrentSession values to default.
     * The default is the same as when the application starts up.     *
     */
    public static void ResetGameSession()
    {
        _SelectedMapName = "";
        _CurrentPlayers = null;
        _CollisionManager = null;
        _MapModel = null;
        _MapView = null;
    }

    /**
     * Switches up the order of the players.
     * Assumes that always exectly 2 players exist
     */
    public static void SwitchPlayerNumbers() {
        Player t0 = _CurrentPlayers.get(0);
        Player t1 = _CurrentPlayers.get(1);
        _CurrentPlayers.clear();
        _CurrentPlayers.add(t1);
        _CurrentPlayers.add(t0);
        _RealPlayerIndex = 1;
    }

    /**
     * Getter for the real player index number.
     *
     * @return The index number of an array of the players, where the referred player is non-remote.
     */
    public static int get_RealPlayerIndex() {
        return _RealPlayerIndex;
    }

    /**
     * Getter for the LocalPlayerSelectedCarTypeIndex.
     * Represents the car design the local user selected on the launch screen.
     *
     * @return Returns the index number of the type (design) that the local player selected.
     */
    public static int get_LocalPlayerSelectedCarTypeIndex() {
        return _LocalPlayerSelectedCarTypeIndex;
    }

    /**
     * Setter for the LocalPlayerSelectedCarTypeIndex.
     * Represents the car design the local user selected on the launch screen.
     *
     * @param selectedCarIndex The index value of the selected car type (design)
     */
    public static void set_LocalPlayerSelectedCarTypeIndex(int selectedCarIndex) {
        _LocalPlayerSelectedCarTypeIndex = selectedCarIndex;
    }

    /**
     * Returns the player instance that represents the remote player.
     *
     * @return Player instance that represents the remote player.
     */
    public static CarRemote GetRemoteCar() {
        CarRemote result = null;
        if (_RealPlayerIndex == 0) {
            result = (CarRemote) _CurrentPlayers.get(1).get_Car();
        }

        if (_RealPlayerIndex == 1) {
            result = (CarRemote) _CurrentPlayers.get(0).get_Car();
        }

        return result;
    }
}
