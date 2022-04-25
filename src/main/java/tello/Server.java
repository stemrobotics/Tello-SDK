// Code For Server was adapted from "https://github.com/roytuts/java/tree/master/httpserver-source"
package tello;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.OutputStream;

import tello.server.constant.ServerConstant;
import tello.server.handler.ServerResourceHandler;
import tello.server.handler.TestPost;
import com.sun.net.httpserver.HttpServer;

public class Server implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private static final ClassLoader loader = Server.class.getClassLoader();

    private static Server server;
    private HttpServer httpServer;
    private ExecutorService executor;
    private static String serverHome;
    private static int port;

    public static void main(String[] args) {
        serverHome =  args.length > 0 ? args[0] : loader.getResource(ServerConstant.WEBAPP_DIR).getPath();
        port = args.length != 1 ? ServerConstant.DEFAULT_PORT : Integer.parseInt(args[1]);

        server = new Server();

        Thread thread = new Thread(server);

        thread.start();

        Runtime.getRuntime().addShutdownHook(new ShutDown());

        try {
            thread.join();
        }catch (Exception e) {}
    }

    @Override
    public void run() {
		// This is incase we run the server from the main class rather than the server class;
		if (server == null) {
			server =  Main.getServer();
			port = Main.getServerPort();
			serverHome = Main.getServerHome();
		}

        try {
			executor = Executors.newFixedThreadPool(10);

			httpServer = HttpServer.create(new InetSocketAddress(ServerConstant.DEFAULT_HOST, port), 0);
			
			httpServer.createContext(
				ServerConstant.FORWARD_DOUBLE_SLASH + "testPOST", 
				new TestPost()
			);
			
			httpServer.createContext(
                ServerConstant.FORWARD_SINGLE_SLASH, 
                new ServerResourceHandler(serverHome, true, false)
            );

			httpServer.setExecutor(executor);

			LOGGER.info("Starting server...");

			httpServer.start();

			LOGGER.info("Server started => " + ServerConstant.DEFAULT_HOST + ":" + port);

			// Wait here until shutdown is notified
			synchronized (this) {
				try {
					this.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			LOGGER.severe("Error occurred during server starting..." + e);
		}
    }

    static void shutDown() {
		try {
			LOGGER.info("Shutting down server...");
			server.httpServer.stop(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		synchronized (server) {
			server.notifyAll();
		}
	}
}