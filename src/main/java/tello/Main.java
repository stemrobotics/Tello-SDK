package tello;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import tello.demo.Demo3;
import tello.server.constant.ServerConstant;

// Main class contains the method main(), which is what Java JRE 
// calls to begin execution when a Java program is run.
public class Main {
    // Get references to Java's built-in logging classes. Makes it easier to debug

    private static final Logger logger = Logger.getGlobal();
    private static final ConsoleHandler handler = new ConsoleHandler();

    private static final ClassLoader loader = Main.class.getClassLoader();

    private static Server server;  // Instance of the server running
    private static String serverHome; // Home of the webApp folder, this is pre-set in the ServerConstant.WEBAPP_DIR
    private static int port; // Port the Server is running on

    // Main always called to start a Java program.
    public static void main(String[] args) throws Exception {
        // Configure global logger for console logging.
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);

        // Set default logging level to a bit more detailed than INFO. Logging
        // statements at or above the set logging level will be output to
        // the console window. INFO level is typically used to show high
        // level informational messages. FINE is for logging of more detail, FINER
        // for logging a lot of detail. Due to a quirk in the design of the Java
        // logger, we have to set the desired log level in both the logger and 
        // handler instances. The logger is used by the calling program to create
        // log messages and the handler is used by the logger to send log messages
        // to a destination (like the console or a file).
        logger.setLevel(Level.FINE);
        handler.setLevel(Level.FINE);

        logger.info("start");

        // Checks if the args includes an argument for the home WEBAPP_Directory, if not it uses the pre-set directory
        serverHome = args.length > 0 ? args[0] : loader.getResource(ServerConstant.WEBAPP_DIR).getPath();
        
        // Check if the args includes an argument for the port, if not uses the pre-set directory
        port = args.length != 1 ? ServerConstant.DEFAULT_PORT : Integer.parseInt(args[1]);

        server = new Server();

        // Makes a thread to run the server
        Thread thread = new Thread(server);

        thread.start();

        Runtime.getRuntime().addShutdownHook(new ShutDown());

        try {
            thread.join(); // Makes the thread part of the main program so it will not shut down before server is turned off
        } catch (Exception e) {}

        // Create an instance of the drone program (class) we want to run.
        //Demo1 demo = new Demo1();
        //Demo2 demo = new Demo2();
        //Demo3 demo = new Demo3();
        // Demo4 demo = new Demo4();
        // Demo5 demo = new Demo5();
        // FlySquare demo = new FlySquare();
        // FlyGrid demo = new FlyGrid();
        // FindMissionPad demo = new FindMissionPad();
        // FlyController demo = new FlyController();
        // FindMarker demo = new FindMarker();
        // TrackMarker demo = new TrackMarker();
        // FindFace demo = new FindFace();
        // FindFace2 demo = new FindFace2();
        // Run that program.
        //demo.execute();
        logger.info("end");
    }

    /**
     * Returns the instance of the server created by the Main Program
     * @return Server being ran
     */
    public static Server getServer() {
        return server;
    }

    /**
     * The name of the WEBAPP_Directory
     * @return The location of the directory the web app files are.
     */
    public static String getServerHome() {
        return serverHome;
    }

    /**
     * The port the server is being ran on.
     * @return port server is ran on.
     */
    public static int getServerPort() {
        return port;
    }
}
