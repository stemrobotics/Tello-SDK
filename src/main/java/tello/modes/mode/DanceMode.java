package tello.modes.mode;

public class DanceMode extends AbstractMode {
    static private DancesMode[] danceMove;

    static DancesMode[] getDefaultDanceMoves() {
        DancesMode[] dancesMove = {
            DancesMode.LEFT,
            DancesMode.RIGHT,
            DancesMode.FORWARD,
            DancesMode.BACKWARD,
            DancesMode.FLIP
        };

        return dancesMove;
    }

    static void addNewDanceMove(DancesMode danceMove) {

    }

    public DanceMode() {
        super();
    }

    public void run() {

    }

    @Override
    public void execute() {
        run();
    }

    @Override
    public String getName() {
        return "Dance";
    }

    @Override
    public String getDescription() {
        return "Dances";
    }
}
