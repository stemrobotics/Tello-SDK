package tello.modes.mode;

import java.util.logging.Logger;

public class HoverMode extends AbstractMode {
    public HoverMode(String name, String description) {
        super(name, description);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void execute() {
        stillRunning = true;

        // TODO Auto-generated method stub
        telloControl.connect();

        telloControl.takeOff();

        telloControl.startStatusMonitor();

        telloControl.setSpeed(10);

        telloControl.up(50);

        stillRunning = false;
    }
}
