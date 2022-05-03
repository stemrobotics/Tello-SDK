package tello.modes.mode;

public class HoverLeftMode extends AbstractMode{

    public HoverLeftMode(String name, String description) {
        super(name, description);
    }

    public void run() {
        stillRunning = true;

        tellocoms.setTimeout(50_000);
        telloControl.connect();

        telloControl.enterCommandMode();

        // Everything else goes inbetween

        telloControl.takeOff();

        telloControl.startStatusMonitor();

        telloControl.setSpeed(10);

        telloControl.up(50); // hovering 50cm up

        telloControl.left(50); // goes left 50cm
        //
        stopExecution();
    }

    @Override
    public void execute() {
        run();
    }
    
}
