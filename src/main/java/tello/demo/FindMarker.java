package tello;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Rect;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import tellolib.camera.ArucoMarkers;
import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class FindMarker
{
	private final Logger logger = Logger.getGlobal(); 

	private TelloControl		telloControl;
	private TelloDrone			drone;
	private TelloCamera			camera;
	private ControllerManager	controllers;
	private ArucoMarkers		markerDetector;
	private boolean				detectMarkers = false;
	private int					markerId = 0;
	
	public void execute() throws Exception
	{
		int		leftX, leftY, rightX, rightY, deadZone = 10;
		int		markerCount;
		boolean found = false;

		logger.info("start");
	    
	    controllers = new ControllerManager();
		controllers.initSDLGamepad();
    	
    	ControllerState currState = controllers.getState(0);
    	  
    	// No controller, no fly!
    	
    	if (!currState.isConnected) throw new Exception("controller not connected");

	    telloControl = TelloControl.getInstance();
	    
	    drone = TelloDrone.getInstance();
	    
	    camera = TelloCamera.getInstance();
	    
	    // Create instance of Aruco Marker Detection support class.
	    
	    markerDetector = ArucoMarkers.getInstance();
	    		
	    telloControl.setLogLevel(Level.FINE);
		
		// Controller mapping:
		// Start button = take off
		// Back button  = land
		// A button     = take picture
	    // B button     = toggle video recording
	    // X button     = toggle marker detection mode
	    // Y button     = stop, go into hover
		// Dpad.up      = flip forward
		//
		// right joystick Y axis = forward/backward
		// right joystick X axis = left/right
		//
		// left joystick Y axis  = up/down
		// left joystick X axis  = rotate left/right

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();
		   
		    telloControl.startStatusMonitor();
		    
		    telloControl.streamOn();
		    
		    camera.setStatusBar(this::updateWindow);
		    
		    camera.startVideoCapture(true);
		   
		    // Now we loop until land button is pressed or we lose connection.
		    
		    while(drone.isConnected()) 
		    {
		    	// Read the current state of the first (and in our case only)
		    	// game pad.
		    	
		    	currState = controllers.getState(0);
		    	  
		    	// Back button lands drone and exits while loop.
		    	
		    	if (currState.backJustPressed) 
		    	{
		    		logger.info("back button");
		    		
		    		if (drone.isFlying()) telloControl.land();
		    		
		    		break;
		    	}
		    	
		    	// Start button takes off.

		    	if (currState.startJustPressed)
		    	{
		    		logger.info("start button");
		    		
		    		if (drone.isFlying())
		    			telloControl.land();
		    		else
		    			telloControl.takeOff();
		    	}

		    	// A button takes a picture.
		    	
		    	if  (currState.aJustPressed) camera.takePicture(System.getProperty("user.dir") + "\\Photos");
		    	
		    	// B button toggles video recording.
		    	
		    	if (currState.bJustPressed)
		    	{
		    		if (camera.isRecording())
		    			camera.stopRecording();
		    		else
		    			camera.startRecording(System.getProperty("user.dir") + "\\Photos");
		    	}

		    	// X button toggles marker detection. Note when detectMarkers is true we
		    	// execute the code on each loop, when false we skip code unless X button
		    	// is pressed.
		    	
		    	if (currState.xJustPressed)
		    	{
		    		// Toggle detectMarkers on X button.
		    		detectMarkers = !detectMarkers;
	    			
	    			// Clear any target rectangles if marker detection was turned off.
	    			if  (!detectMarkers) camera.addTarget(null);
		    	}
		    	
		    	if (detectMarkers)
		    	{
		    		// Call markerDetection class to see if markers are present in the current
		    		// video stream image.
	    			found = markerDetector.detectMarkers();
    			
	    			if (found)
	    			{
		    			// How many markers are detected? This is just information.
	    				markerCount = markerDetector.getMarkerCount();
	
	    				logger.finer("marker count=" + markerCount);
	    				
	    				// Get the array of rectangles describing the location and size
	    				// of the detected markers.
	    				ArrayList<Rect> markers = markerDetector.getMarkerTargets();

			    		// Clear any previous target rectangles on camera feed.
		    			camera.addTarget(null);
	    				
	    				// Set first marker rectangle to be drawn on video feed.
	    				camera.addTarget(markers.get(0));
	    				
	    				markerId = markerDetector.getMarkerId(0);
	    			} else {
		    			camera.addTarget(null);
	    				markerId = 0;
	    			}
		    	}
		    	
    			// If flying, pass the controller joystick deflection to the drone via
		    	// the flyRC command.
		    	
		    	if (drone.isFlying())
		    	{
		    		// scale controller stick axis range -1.0 to + 1.0 to -100 to + 100
		    		// used by the drone flyRC command. Apply a dead zone to allow
		    		// for stick axis not always returning 0 when released.
		    		leftX = deadZone((int) (currState.leftStickX * 100.0), deadZone);
		    		leftY = deadZone((int) (currState.leftStickY * 100.0), deadZone);
		    		rightX = deadZone((int) (currState.rightStickX * 100), deadZone);
		    		rightY = deadZone((int) (currState.rightStickY * 100), deadZone);
		    		
		    		//logger.info("lr=" + rightX + " fb=" + rightY + " ud=" + leftY + " yaw=" + leftX);
		    		
		    		// Pass joy stick deflection to drone.
		    		//                  L/R      F/B    U/D    YAW
	    			telloControl.flyRC(rightX, rightY, leftY, leftX);

	    			// Flips or other direct drone commands must go here to be executed after FlyRC is done.
	    			
		    		if (currState.dpadUpJustPressed) telloControl.doFlip(TelloFlip.forward);
		    		
		    		if (currState.yJustPressed) telloControl.stop();
		    	}
		    	
		    	Thread.sleep(100);
		    }
	    }	
	    catch (Exception e) {
	    	e.printStackTrace();
	    } finally 
	    {
	    	if (drone.isConnected() && drone.isFlying())
	    	{
	    		try
	    		{telloControl.land();}
	    		catch(Exception e) { e.printStackTrace();}
	    	}
	    }
	    
    	telloControl.disconnect();
	    
	    logger.info("end");
	}
	
	// Apply a dead zone to the input. Input below min value forced to zero.
	
	private int deadZone(int value, int minValue)
	{
		if (Math.abs(value) < minValue) value = 0;
		
		return value;
	}	
	
	// Return a string of info for the status area on video feed.
	// Drone won't respond to controller until Rdy = True, meaning
	// that takeoff is complete.
	
	private String updateWindow()
	{
    	 return String.format("Batt: %d  Alt: %d  Hdg: %d  Rdy: %b  Detect: %b  Id: %d", drone.getBattery(), drone.getHeight(), 
    			drone.getHeading(), drone.isFlying(), detectMarkers, markerId);
	}
}
