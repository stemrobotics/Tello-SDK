package tello.modes;


import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;

import tello.modes.mode.AbstractMode;

public class ModeManger {

    private String currentMode = null;
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
        String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
        
        StringBuilder s = new StringBuilder(10);

        int i;

        for ( i=0; i < 10; i++) {

            //generating a random number using math.random()

            int ch = (int)(AlphaNumericStr.length() * Math.random());

            //adding Random character one by one at the end of s

            s.append(AlphaNumericStr.charAt(ch));

        }

        String generatedString = s.toString();

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

    public void runMode(String modeID) {
        if (currentMode == null) {
            currentMode = modeID;
            modes.get(currentMode).execute();
        }else {
            if (currentMode != null) {
                if (modes.get(currentMode).doneRunning()) {
                    currentMode = null;
                    runMode(modeID);
                }else {
                    modes.get(currentMode).stopExecution();
                    currentMode = null;
                    runMode(modeID);
                }
            }
        }
    }
}
