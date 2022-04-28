package tello.modes.mode;

import java.util.logging.Logger;

public class HoverMode extends AbstractMode {
    public HoverMode(String name, String description) {
        super(name, description);
        //TODO Auto-generated constructor stub
    }

    public void run() {
        super.run();

        stillRunning = true;

        // TODO Auto-generated method stub
        tellocoms.setTimeout(50000);
        telloControl.connect();

        telloControl.enterCommandMode();

        telloControl.takeOff();

        telloControl.startStatusMonitor();

        telloControl.setSpeed(10);

        telloControl.up(50);

        stopExecution();
    }

    @Override
    public void execute() {
        start();
    }
}
