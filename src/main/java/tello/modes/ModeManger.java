package tello.modes;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.stripes.util.ResolverUtil;

import org.json.JSONObject;

import tello.modes.mode.AbstractMode;

public class ModeManger {

    private String currentMode = null;
    private Map<String, AbstractMode> modes;
    private boolean alreadyRan = false;

    public ModeManger() {
        modes = new HashMap<String, AbstractMode>();
    }

    /**
     * Starts the mode manager, loads all the modes for later use
     */
    public void run() {
        if (alreadyRan) {
            return;
        }

        alreadyRan = true;

        ResolverUtil<AbstractMode> resolver = new ResolverUtil<AbstractMode>();

        resolver.findImplementations(AbstractMode.class, AbstractMode.class.getPackage().getName());

        for (Class<? extends AbstractMode> exclass : resolver.getClasses()) {
            AbstractMode mode = null;

            try {
                // The if statement is to prevent an abstract class from being instantiated, because it will give an error
                if (!exclass.getDeclaredConstructor().toString().equals("public tello.modes.mode.AbstractMode()")) {
                    mode = exclass.getDeclaredConstructor().newInstance();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mode != null) {
                this.addMode(mode);
            }
        }
    }

    /**
     * @param mode - The new mode you would like to add
     */
    private void addMode(AbstractMode mode) {
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
     * @return - Returns a new generated id for a mode
     */
    public String generateModeID() {
        String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";

        StringBuilder s = new StringBuilder(10);

        int i;

        for (i = 0; i < 10; i++) {

            //generating a random number using math.random()
            int ch = (int) (AlphaNumericStr.length() * Math.random());

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
     * @param modeID - The id of the mode you would like to get
     * @return The mode if available
     */
    public AbstractMode getMode(String modeID) {
        return modes.get(modeID);
    }

    public String getModeId(AbstractMode mode) {
        for (Entry<String, AbstractMode> entry : modes.entrySet()) {
            if (entry.getValue().equals(mode)) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Mode is not in ModeManager");
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
     * @return - Returns all the modes in JSON format
     */
    public JSONObject getModeList() {
        JSONObject json = new JSONObject();

        for (String key : modes.keySet()) {
            json.put(key, modes.get(key).toJson());
        }

        return json;
    }

    /**
     * First checks there is no mode currently being ran before running a mode,
     * if a mode is being ran, it stops the mode then starts a new mode.
     *
     * @param modeID - The id of the mode you would like to run
     */
    public void runMode(String modeID) {
        if (currentMode == null) {
            currentMode = modeID;
            modes.get(currentMode).execute();
        } else {
            if (modes.get(currentMode).doneRunning()) {
                currentMode = null;
                runMode(modeID);
            } else {
                modes.get(currentMode).stopExecution();
                currentMode = null;
                runMode(modeID);
            }
        }
    }

    /**
     * First checks there is no mode currently being ran before running a mode,
     * if a mode is being rand, it stops the mode then starts a new mode.
     * 
     * @param mode - The mode you would like to run
     */
    public void runMode(AbstractMode mode) {
        if (currentMode == null) {
            currentMode = getModeId(mode);
            mode.execute();
        } else {
            if (currentMode != null) {
                if (modes.get(currentMode).doneRunning()) {
                    currentMode = null;
                    runMode(mode);
                } else {
                    modes.get(currentMode).stopExecution();
                    currentMode = null;
                    runMode(mode);
                }
            }
        }
    }
}
