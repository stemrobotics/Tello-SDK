package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.camera.TelloCamera;
import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class FlySquare
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
	    
	    camera = TelloCamera.getInstance();

	    telloControl.setLogLevel(Level.FINE);

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();
		    
		    telloControl.startStatusMonitor();
		    
		    telloControl.streamOn();
		    
		    // Set the video stream processor to automatically update the video
		    // stream status area by repeatedly calling the method specified.
		    
		    camera.setStatusBar(this::updateWindow);
		    
		    camera.startVideoCapture(true);
		    
		    telloControl.takeOff();
		    
		    // Now we will execute a series of movement commands to fly in a square
		    // pattern. Distances in centimeters.
		    
		    for (int i = 0; i < 4; i++)
		    {
		    	telloControl.forward(50);

		    	telloControl.rotateRight(90);
		    }
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
	
	// Return a string of info for the status area on video feed.
	
	private String updateWindow()
	{
    	 return String.format("Batt: %d  Alt: %d  Hdg: %d", drone.getBattery(), drone.getHeight(), 
    			drone.getHeading());
	}
}
