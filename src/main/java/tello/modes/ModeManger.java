package tello.modes;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import tello.modes.mode.AbstractMode;

public class ModeManger {
    private Map<String, AbstractMode> modes;

    public ModeManger() {
        modes = new HashMap<String, AbstractMode>();
    }

    public ModeManger(List<AbstractMode> newModes) {
        modes = new HashMap<String, AbstractMode>();

        for (Object mode : newModes) {
            addMode(mode);
        }
    }

    /**
     * @param mode - The new mode you would like to add
     */
    public void addMode(Object mode) {
        modes.put(generateModeID(), (AbstractMode) mode);
    }

    /**
     * @param id - The id of the mode you would like to remove
     */
    public void removeMode(String id) {
        if (modes.containsKey(id)) {
            modes.remove(id);
        }
    }

    /**
     * @return  - Returns a new generated id for a mode
     */
    public String generateModeID() {
        byte[] array =  new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);

        String generatedString = new String(array, Charset.forName("UTF-8"));

        if (modes.containsKey(generatedString)) {
            return generateModeID();
        }

        return generatedString;
    }

    /**
     * @param id - The id of the mode you would like to get
     */
    public AbstractMode getMode(String modeID) {
        return modes.get(modeID);
    }

    /**
     * @return - Returns all the modes in string format
     */
    public String getModeListString() {
        JSONObject json = new JSONObject();

        for (String key : modes.keySet()) {
            json.put(key, modes.get(key).toJson());
        }

        return json.toString();
    }

    /**
     * @return - Returns all the modes in json format
     */
    public JSONObject getModeList() {
        JSONObject json = new JSONObject();

        for (String key : modes.keySet()) {
            json.put(key, modes.get(key).toJson());
        }

        return json;
    }
}
