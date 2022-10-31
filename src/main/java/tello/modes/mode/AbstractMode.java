package tello.modes.mode;

import org.json.JSONObject;

import java.util.logging.Level;

import tellolib.communication.TelloCommunication;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public abstract class AbstractMode{
    protected String name;
    protected String description;
    
    protected TelloControl telloControl;
    protected TelloCommunication tellocoms;
    protected TelloDrone drone;

    protected Boolean stillRunning = false;

    public AbstractMode() {
        telloControl = TelloControl.getInstance();
        drone = TelloDrone.getInstance();
        tellocoms = TelloCommunication.getInstance();

        telloControl.setLogLevel(Level.FINE);
    }

    /**
     * @return the name
     */
    public abstract String getName(); 

    /**
     * @return the description
     */
    public abstract String getDescription();

    /**
     * @return - The name and description of the mode in JSON format
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", getName());
        json.put("description", getDescription());

        return json;
    }

    /**
     * @return - The name and description of the mode in string format
     */
    @Override
    public String toString() {
        return toJson().toString();
    }

    public abstract void execute();

    public Boolean doneRunning() {
        return !stillRunning;
    }

    /**
     * Stops running the mode, lands the robot and then disconnect from it
     */
    public void stopExecution() {
        telloControl.disconnect();
        stillRunning = false;
    }
}
