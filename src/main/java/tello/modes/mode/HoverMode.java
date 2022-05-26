package tello.modes.mode;

public class HoverMode extends AbstractMode {
    public HoverMode() {
        super();
    }

    public void run() {
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
        run();
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Hover";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Hovers 50cm up";
	}
}
