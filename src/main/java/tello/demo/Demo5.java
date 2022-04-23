package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;
import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class Demo5
{
	private final Logger logger = Logger.getGlobal(); 

	private TelloControl	telloControl;
	private TelloDrone		drone;
	private TelloCamera		camera;
	
	public void execute()
	{
		logger.info("start");
		
	    telloControl = TelloControl.getInstance();
	    
	    drone = TelloDrone.getInstance();
	    
	    // Get reference to global TelloCamera instance.
	    
	    camera = TelloCamera.getInstance();

	    telloControl.setLogLevel(Level.FINE);

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();
		    
		    telloControl.startStatusMonitor();

		    // Send command to drone to turn on video stream.
		    
		    telloControl.streamOn();
		    
		    // Turn on video stream capture by this program and display the live
		    // feed on the PC. This call can take several seconds when displaying
		    // live video window.
		    
		    camera.startVideoCapture(true);
		    
		    // We start recording the video stream to a .avi file in the Photos folder of this
		    // Eclipse project. File will be labeled with date and time.
		    
		    camera.startRecording(System.getProperty("user.dir") + "\\Photos");

		    // Tell drone to get airborne.
		    
		    telloControl.takeOff();

		    // Now we will execute a series of movement commands.
		    // Distances in centimeters.
		    
	    	telloControl.forward(50);
		    
	    	updateWindow();
	    			
		    telloControl.backward(50);
		    
	    	updateWindow();
		    
		    telloControl.up(50);
		    
	    	updateWindow();
		    
		    telloControl.down(50);
		    
	    	updateWindow();
		    
		    telloControl.left(50);
		    
	    	updateWindow();
		    
		    telloControl.right(50);
		    
	    	updateWindow();
		    
		    telloControl.rotateLeft(90);
		    
	    	updateWindow();
		    
		    // Lets take a still picture (.jpg) at this moment.
		    
		    camera.takePicture(System.getProperty("user.dir") + "\\Photos");
		    
		    telloControl.rotateRight(90);
		    
	    	updateWindow();
		    
		    //telloControl.doFlip(TelloFlip.backward);
	    }	
	    catch (Exception e) {
	    	e.printStackTrace();
	    } finally 
	    {
	    	if (telloControl.getConnection() == TelloConnection.CONNECTED && drone.isFlying())
	    	{
	    		try
	    		{telloControl.land();}
	    		catch(Exception e) { e.printStackTrace();}
	    	}
	    }
	    
    	telloControl.disconnect();
	    
	    logger.info("end");
	}
	
	// Update the status area on live window with some drone information.
	
	private void updateWindow()
	{
    	camera.setStatusBar(String.format("Batt: %d  Alt: %d  Hdg: %d", drone.getBattery(), drone.getHeight(), 
    			drone.getHeading()));
	}
}

