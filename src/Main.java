import ControlLayer.Controller;
import ControlLayer.SharedResources;

class Main {


    /**
     * Entry point for the application. Instantiates the Controller and lets the application run by it.
     *
     * @param args Input parameters are not used by the application.
     */
    public static void main(String[] args) {

        SharedResources.MainController = new Controller();
        SharedResources.MainController.ApplicationStartUp();
    }
}
