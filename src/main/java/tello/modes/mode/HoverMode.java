package tello.modes.mode;

public class HoverMode extends AbstractMode {
    public HoverMode() {
        super();
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
