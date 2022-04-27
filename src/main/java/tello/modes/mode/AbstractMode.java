package tello.modes.mode;

import org.json.JSONObject;

import java.util.logging.Level;

import tellolib.control.TelloControl;

public abstract class AbstractMode {
    private String name;
    private String description;
    
    private TelloControl telloControl;

    public AbstractMode(String name, String description) {
        this.name = name;
        this.description = description;
        telloControl = TelloControl.getInstance();

        telloControl.setLogLevel(Level.FINE);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return - The name and description of the mode in json format
     */
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);

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
}
