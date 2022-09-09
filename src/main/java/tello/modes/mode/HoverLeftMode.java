package tello.modes.mode;

public class HoverLeftMode extends AbstractMode{

    public HoverLeftMode() {
        super();
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

    @Override
    public String getName() {
        return "Hover Left";
    }

    @Override
    public String getDescription() {
        return "Hovers up 50cm and then ggoes left 50cm";
    }
    
}
