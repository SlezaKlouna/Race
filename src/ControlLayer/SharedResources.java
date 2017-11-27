package ControlLayer;

import ModelLayer.CollisionManagement.CarBounds;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Contains global variables, constants. Act as a configuration file/class.
 */
public class SharedResources {

    public static final String APPLICATIONNAME = "DS Cars"; //Displayed on the window title
    public static final int PLAYER_1 = 1; //Magic number (to remove dubious 1 parameters)
    public static final int PLAYER_2 = 2; //Magic number (to remove dubious 2 parameters)
    public static final int FRAMERATE = 40; //Delay in milliseconds between in-game frame refreshes. 40ms equals to 25 Frames Per Second.
    /*    END Default game options (controllable from the Menu bar) */
    /*    Main window settings */
    public static final int MW_JFRAME_WIDTH = 850;
    public static final int MW_JFRAME_HEIGHT = 650;
    /*     Menu bar settings         */
    public  static  final String MB_GameMenuString = "Game";
    public  static  final String MB_StartNewGameMenuString = "Start new game";
    public  static  final String MB_SettingsMenuString = "Settings";
    public  static  final String MB_HelpMenuString = "Help";
    public  static  final String MB_HelpAboutMenuString = "About the game";
    public  static  final String MB_HelpControlMenuString = "Controls";
    public static final String MB_HelpCollisionMenuString = "Collisions";
    public  static  final String MB_MusicMenuButtonString = "Music on";
    public  static  final String MB_SoundsMenuButtonString = "Sounds on";
    public static   final String MB_MapTextureMenuButtonString = "Map texture on";
    public  static  final String MB_Help_About_Title = "Help - About the game";
    public  static  final String MB_Help_Control_Title = "Help - About the game";
    public static final String MB_Help_Collision_Title = "Help - About collisions";
    public static final String MB_Help_About_Content =
            "DS Cars is a car racing game, where the players’ goal is to go race around the arena without hitting into each other. \n" +
                    "The program features:\n" +
                    "\n" +
                    "- A server mode to provide host games. While in server mode, the application cannot be used as a client.\n" +
                    "- The server mode allows selection of ports and displays log messages during its service.\n" +
                    "- Players will receive friendly notification of errors if an opponent leaves the game or server shuts down.\n" +
                    "- A client mode to allow to play with an opponent using a server.\n" +
                    "- 4 selectable car designs (colours). All cars have transparent background.\n" +
                    "- 2 selectable maps (easy and medium).\n" +
                    "- Maps are using enhanced graphics for better visual appeal. The textures can be turned off by the user.\n" +
                    "- Music and sounds. These can be turned off by the user. \n" +
                    "- Cars are equipped with 3 types of collision detection: 'car to grass', 'car to wall', and 'car to car'. \n" +
                    "- Enhanced accuracy of the collision detection: it is based on the visible car pixels instead of the car image file borders.\n" +
                    "- A user help about the game (this), and about the controls. \n" +
                    "- An enhanced (more realistic) acceleration and slowdown of the cars. \n" +
                    "- An in-game heads up display (HUP) that shows the players’ current speed, car type, and name. \n" +
                    "- An easy to use menu to select between game modes.\n" +
                    "- An easy to use game launcher menu to select between cars, maps or server connection.\n";
    public static final String MB_Help_Control_Content =
            "A car can be controlled with 4 keys. These are described below:\n" +
                    "\n" +
                    "1. Accelerate (Key: up arrow): \n" +
                    "    - Increases the speed of the car up to a specific limit.\n" +
                    "    - The speed limit is virtually 100 mph if the car is on the racing track (road).\n" +
                    "    - The speed limit is virtually 37 mph if the car is on the grass/field. \n" +
                    "    - The car accelerates faster when on low speeds, and slower as it reaches its peak.\n" +
                    "    - The car cannot accelerate if the reverse/break button is also pressed. \n" +
                    "    - A reversing car cannot be accelerated (forward).\n" +
                    "    - A car slows down and eventually stops if accelerate is not pressed (unless reversing).\n" +
                    "    \n" +
                    "2. Reverse / break (Key: down arrow)\n" +
                    "    - Increases the reverse speed.\n" +
                    "    - A forwarding car first gets slowed down, then the reversing takes place.\n" +
                    "    - The speed limit of reversing is virtually 50 mph if the car is on the racing track (road)\n" +
                    "    - The speed limit of reversing is virtually 37 mph if the car is the grass/field\n" +
                    "    - A car slows down and eventually stops if reverse is not pressed (unless forwarding).\n" +
                    "\n" +
                    "3. Turn left (Key: left arrow)\n" +
                    "    - Turns the car’s angle with 22 degrees counter-clockwise.\n" +
                    "    - To improve playability, car can turn even if it is standing (unlike in real life)\n" +
                    "    - The car will not turn if turning would cause collision (a sound effect marks this).   \n" +
                    "\n" +
                    "4. Turn right (Key: right arrow)\n" +
                    "    - Turns the car’s angle with 22 degrees clockwise.\n" +
                    "    - To improve playability, car can turn even if it is standing (unlike in real life)\n" +
                    "    - The car will not turn if turning would cause collision (a sound effect marks this).   \n";
    public static final String MB_Help_Collision_Content =
            "About COLLISIONS:\n" +
                    "\n" +
                    "- A car is not able to accelerate if it hits the wall in a high angle.\n" +
                    "- If the car hits the wall in a low angle, than it is capable to slowly move along the wall (friction)\n" +
                    "- The car detects collision based on its visible outer edges (rectangle) and not on the image aspect (square).\n" +
                    "-  Because of this and the cars’ shape, it is possible that a car will not turn if it is too close the wall.\n" +
                    "- If the car is too close to the wall to turn, it might be needed to be reversed and then turned (like in real life). \n";
    /*    Main Menu Screen settings */
    public static final String MMS_Menu_ConnectToServer = "Connect to server";
    public static final String MMS_Menu_CreateServer = "Create a server";
    public  static  final String MMS_Menu_Exit = "Exit";
    public  static  final int MMS_Menu_Width = 300;
    public static final int MMS_Menu_Height = 150;
    public  static  final int MMS_Menu_Option_Height = 50;
    public static final String MMS_BackgroundImageFile = "/imgs/mainmenubg.jpg";
    public static   final Color MMS_Button_BackGround_Color =  new Color(255,255,255,255);
    public static   final Font MMS_Button_Font = new Font("Consolas",Font.BOLD, 14);
    /* Launch Screen settings */
    public static final String LS_ReturnToMainButton_Text = "Return to Main";
    public static final String LS_YOUR_KEYS_TEXT = "Your keys";
    public static final int LS_ReturnToMainButton_X = 30;
    public static final int LS_ReturnToMainButton_Y = 570;
    public static final int LS_ReturnToMainButton_Width = 150;
    public static final int LS_ReturnToMainButton_Height = 30;
    public static final Color LS_ReturnToMainButton_Color = new Color(255,255,255);
    public static final Font LS_ReturnToMainButton_Font = new Font("Consolas",Font.BOLD, 14);
    public static final String LS_GameStartButton_Text_Ready = "<html><center>Connect &<br> Start!</center></html>";
    public static final int LS_GameStartButton_Y = 540;
    public static final int LS_GameStartButton_Height = 60;
    public static final Color LS_GameStartButton_Color = LS_ReturnToMainButton_Color;
    public static final Font LS_GameStartButton_Font = LS_ReturnToMainButton_Font;
    public static final Color LS_GameStartButton_Foreground_Color = new Color(255,0,0);
    public static final String LS_SelectorArrow_LtoR_Image_Filename = "/imgs/launchscreen/SelectorArrow_LtoR.png";
    public static final String LS_SelectorArrow_RtoL_Image_Filename = "/imgs/launchscreen/SelectorArrow_RtoL.png";
    public static final int LS_SelectorArrow_Image_Height = 20;
    public static final int LS_SelectorArrow_Image_Width = 12;
    public static final int LS_Connection_Panel_Vertical_Spacer = 50;
    /* Car selection panel settings */
    public static final int CSP_Number_Of_Selectable_Cars = 4;
    public static final String CSP_Selectable_Car_Image_FileName_NoPrefix = "_selectablecar.png";
    public static final String CSP_Selectable_Car_Image_FileNamePath = "/imgs/launchscreen/";
    public static final String CSP_Error_Selectable_Car_Image_Missing = "No image";
    public static final int CSP_Selectable_Car_Image_Size = 50;
    public static final int CSP_Selectable_Car_Holder_Spacing = 45;
    public static final Color CSP_Selectable_Car_Holder_Color = new Color(255,255,255,255);
    public static final int CSP_Location_Player1_X = 30;
    public static final int CSP_Location_Player1_Y = 50;
    public static final int CSP_Header_Height = 20;
    public static final Font CSP_Header_Font = new Font("Consolas",Font.BOLD, 18);
    public static final Color CSP_Header_Color = new Color(128,191,255,100);
    public static final String CSP_Header_Message_After_Player_Number = "Select car";
    /*Controlling key layout image Label on LaunchScreen settings*/
    public static final int CLI_Width = 160;
    public static final int CLI_Height = 95;
    public static final int CLI_Background_Image_Width = 118;
    public static final int CLI_Background_Image_Height = 70;
    public static final String CLI_LayoutImage_FilePath = "/imgs/launchscreen/";
    public static final String CLI_ImageFileName_NoSubFix = "KeyboardLayout_Player_";
    public static final String CLI_ImageFile_FileExtension = ".png";
    public static final int CLI_HorizontalSpacing_From_CSP = 30;
    public static final Font CLI_Header_Font = CSP_Header_Font;
    public static final Color CLI_Background_Color = new Color(255,255,255,192);
    /*  Map selection panel settings on LaunchScreen  */
    public static final Color MSP_BackgroundColor = new Color(255,255,255,255);
    public static final int MSP_Space_From_CLI = 30;
    public static final int MSP_Panel_Width = 120;
    public static final int MSP_Panel_Vertical_Spacer = 30;
    public static final int MSP_Map_Image_Width = 80;
    public static final int MSP_Map_Image_Height = 80;
    public static final String[] MSP_Maps = {"Easy", "Medium"};
    public static final String MSP_MapImage_FilePath = "/imgs/launchscreen/";
    public static final String MSP_ImageFileName_NoNamePrefix = "Map_";
    public static final String MSP_ImageFile_FileExtension = ".png";
    public static final Color MSP_Map_BackGround = new Color(100,100,100,192);
    public static final Font MSP_Header_Font = CSP_Header_Font;
    public static final int MSP_Header_Height = 20;
    public static final int MSP_Header_Vertical_Spacer = 5;
    public static final int MSP_Map_From_Header_Spacer = 15;
    public static final String MSP_Header_Text = "Select map";
    public static final Color MSP_Header_Bg_Color = CSP_Header_Color;
    public static final Font MSP_Map_Title_Font = MSP_Header_Font;
    public static final int MSP_Map_TitleHeight = 20;
    /* INGAME: Settings related to a car's display during the game */
    public static final String CAR_ImageFile_RootPath = "/imgs/carsets/";
    public static final String CAR_ImageFileName_Prefix = "car_";
    public static final String CAR_ImageFileName_Extension = ".png";
    public static final String CAR_Crashed_ImageFileNameWithPath = "/imgs/carsets/crash.png";
    public static final int CAR_Image_Size_X = 50;
    public static final int CAR_Image_Size_Y = CAR_Image_Size_X;
    public static final String[] CAR_ImageFile_Angles = {"0", "22", "45", "67", "90", "112", "135", "157", "180", "202", "225", "247", "270", "292", "315", "337"}; //The car image filenames reflecting the angles
    public static final int[]   CAR_Simulated_Angle_Values = {0,22,45,67,90,112,135,157,180,202,225,247,270,292,315,337}; //The game logic works with these angle values.
    public static final CarBounds[] CAR_Simulated_Fine_Bounds =
            {
                    new CarBounds(11,1,38,1,11,48,38,48), //Angle: 0
                    new CarBounds(22,0,42,8,30,46,8,39), //Angle: 22
                    new CarBounds(32,1,46,13,19,47,1,32), //Angle: 45
                    new CarBounds(40,8,49,26,11,44,0,21), //Angle: 67
                    new CarBounds(1,12,48,12,48,37,1,37), //Angle: 90
                    new CarBounds(11,6,48,23,41,43,0,27), //Angle: 112
                    new CarBounds(20,1,47,30,35,47,2,16), //Angle: 135
                    new CarBounds(19,1,48,31,36,47,2,16), //Angle: 157
                    new CarBounds(11,1,38,1,11,48,38,48), //Angle: 180 (same as 0, this could be done more accurately in the future)
                    new CarBounds(22,0,42,8,30,46,8,39), //Angle: 202 (same as 22, this could be done more accurately in the future)
                    new CarBounds(32,1,46,13,19,47,1,32), //Angle: 225 (same as 45, this could be done more accurately in the future)
                    new CarBounds(40,8,49,26,11,44,0,21), //Angle: 247 (same as 67, this could be done more accurately in the future)
                    new CarBounds(1,12,48,12,48,37,1,37), //Angle: 270 (same as 90, this could be done more accurately in the future)
                    new CarBounds(11,6,48,23,41,43,0,27), //Angle: 292 (same as 112, this could be done more accurately in the future)
                    new CarBounds(20,1,47,30,35,47,2,16), //Angle: 315 (same as 135, this could be done more accurately in the future)
                    new CarBounds(19,1,48,31,36,47,2,16), //Angle: 337 (same as 157, this could be done more accurately in the future)
            }; /* This is used by the collision detection. Describes where are the non-transparent pixels within the car image.
                  Described as the car made up of 4 lines per each angle (may or may not be an exact rotated rectangle)
            */
    /* INGAME: Game controlling settings */
    public static final Integer[] GCS_ControlKeys_Player_1 =  { KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT}; //The order of keys are important! Always: UP, DOWN, LEFT, RIGHT
    public static final Integer[] GCS_ControlKeys_Player_2 = {87, 83, 65, 68};  //87=W (UP), 83=S (DOWN), 65=A (LEFT), 68=D (RIGHT). The order of keys are important! Always: UP, DOWN, LEFT, RIGHT
    public static final float GCS_Maximum_Car_Speed = 8; //pixel change per frame when forwarding on road
    public static final float GCS_Maximum_Car_Reverse_Speed = 4; //pixel change per frame when reversing
    public static final float GCS_Maximum_Car_Grass_Speed = 3; //pixel change per frame when moving on grass
    public static final float GCS_Car_ColdStart_Acceleration_Percentage = 0.05f; //0.05f of max speed
     public static final float GCS_Car_RadicalAcceleration_SpeedLimit = 0.25f; //At 25% of max speed
    public static final float GCS_Car_RadicalAcceleration_Ratio = 1.5f; // Multiply current speed with 1.5
    public static final float GCS_Car_NormalAcceleration_SpeedLimit = 0.60f; //At 60% of max speed
    public static final float GCS_Car_NormalAcceleration_Ratio = 1.2f; //Multiply current speed with 1.2
    public static final float GCS_Car_HighSpeedAcceleration_Ratio = 1.1f; //Multiply current speed with 1.1
    public static final float GCS_Car_NoAcceleration_Slowdown_Ratio = 0.9f; //Multiply current speed with 0.9
    public static final float GCS_CarNoAcceleration_Stop_Threshold = 0.05f; //Stop the car reaching this threshold if no acceleration
    public static final int GCS_Car_Virtual_Speed_Max = 100; //The actual speed converted into a virtual scale (e.g. mph)
    /* INGAME: Heads Up Display (HUD) */
    public static final int HUD_Panel_Width = 200;
    public static final int HUD_Panel_Height = 75;
    public static final Color HUD_Bg_Color = new Color(255,255,255);
    public static final Font HUD_PlayerName_Font = CSP_Header_Font;
    public static final Font HUD_PlayerSpeed_Font = new Font("Consolas", Font.BOLD, 14);
    public static final int HUD_IconLeftSpacer = 10;
    public static final String HUD_SpeedLabel_Text_Prefix = "SPEED: ";
    public static final String HUD_SpeedLabel_Text_PostFix = " mph";
    public static final int HUD_PlayerNameYSpacer = 10;
    public static final int HUD_PlayerNameWidth = 140;
    public static final int HUD_PlayerNameHeight = 20;
    public static final int HUD_PlayerSpeedYSpacer = 5;
    public static final int HUD_PlayerSpeedWidth = 140;
    public static final int HUD_PlayerSpeedHeight = 15;
    public static final int HUD_SpeedBar_Width = 100;
    public static final int HUD_SpeedBar_Height = 10;
    public static final Color HUD_SpeedBar_Bg_Color = new Color(128,191,255,100);
    public static final int HUD_OnScreenLocation_X_Player_1 = 0;
    public static final int HUD_OnScreenLocation_Y_Player_1 = 0;
    public static final int HUD_OnScreenLocation_X_Player_2 = 650;
    public static final int HUD_OnScreenLocation_Y_Player_2 = HUD_OnScreenLocation_X_Player_1;
    public static final float HUD_StillCar_Speed_VirtualValue_Masking_Threshold = 0.6f;
    /*  Music and sound settings */
    public static final String SND_BackgroundMusic_FilenameWithPath = "/sounds/backgroundmusic.wav";
    public static final String SND_CarImpactSound_FilenameWithPath = "/sounds/carimpact.wav";
    public static final String SND_CarCrash_FilenameWithPath = "/sounds/carcrashfinal.wav";
    public static final String SND_CarPowerUp_FilenameWithPath = "/sounds/motorpowerup.wav";
    /* Game over message */
    public  static final String GO_GameOver_Message = "Game over!\n\nClick 'Ok' to return to the main menu and start a new game.";
    public  static final String Go_GameOver_Title = "Game over!";
    public static final String GO_GameOverWithError_Message = "The game has to be ended due to an error :(.\nPlease return to the main menu to start a new game.\n\nThe error was: ";
    public static final String GO_GameOverWithDropOut_Message = "The game has ended as your opponent have left the game.\nConsider yourself a winner!\n\nClick 'Ok' to return to the main menu and start a new game.";
    public static final String GO_GameOverWithServerDown_Message = "The game has ended as the server went down.\nConsider yourself a winner!\n\nClick 'Ok' to return to the main menu and start a new game.";
    /**
     * SERVER Related
     */
    public static final int SRV_MAX_SESSION_EXCEPTION_INAROW = 5; //Maximum number of exceptions in a row before server disconnects the client
    /* Server screen / control panel */
    public static final Font SRS_FontFaceMedium = new Font("Consolas", Font.BOLD, 12);
    public static final Font SRS_FontFaceLarge = new Font("Consolas", Font.BOLD, 20);
    public static final String SCP_SERVER_S_DETECTED_IP_Label = "Detected name:";
    public static final Integer[] SCP_VALID_PORT_NUMBERS = {24816, 48162, 16248};
    public static final String SRS_CLOSE_SERVER_AND_RETURN_TO_MAIN = "Close server and return to main";
    public static final String SRS_STOP_SERVER = "Stop server!";
    public static final String SRS_START_SERVER = "Start server!";
    public static final String SCP_SERVER_LAUNCHER = "Server Launcher";
    public static final String SCP_PORTS_TEXT = "Ports:";
    public static final String SRV_SERVER_LOG_TEXT = "Server log";
    public static final String SRV_CONNECTION_STATUS_TEXT = "Connection status";
    public static final String LS_ClientPanel_IP_Default_TEXT = "127.0.0.1";
    public static final String LS_ClientPanel_Header_CONNECTION_SETTINGS = "Connection settings";
    public static final String LS_ClientPanel_ENTER_SERVER_ADDRESS = "Enter server's address:";
    public static final String LS_ClientPanel_PORT_Text = "Port:";
    public static Controller MainController; //The main controller of the application
    /*    START Default game options (controllable from the Menu bar) */
    public static boolean DGO_Default_Music_On = true;
    public static boolean DGO_Default_Sound_On = true;
    public static boolean DGO_Default_MapTexture_On = true;


}
