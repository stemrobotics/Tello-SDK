package tello;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Rect;
import org.opencv.core.Size;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import tellolib.camera.ArucoMarkers;
import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class TrackMarker
{
	private final Logger logger = Logger.getGlobal(); 

	private TelloControl		telloControl;
	private TelloDrone			drone;
	private TelloCamera			camera;
	private ControllerManager	controllers;
	private ArucoMarkers		markerDetector;
	private boolean				trackMarker = false;
	private int					markerId = 0, initialTargetArea = 0;
	
	public void execute() throws Exception
	{
		int				leftX, leftY, rightX, rightY, deadZone = 10;
		int				markerCount;
		long			lastDetectionTime = 0;
		boolean 		found = false;
		TrackingResult	trackingResult = null;

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

		    	// X button toggles marker detection and tracking.
		    	
		    	if (currState.xJustPressed)
		    	{
		    		// Toggle trackMarker on X button.
		    		trackMarker = !trackMarker;
	    			
	    			// Clear any target rectangles if marker detection is turned off.
	    			if  (!trackMarker) 
	    			{
	    				camera.addTarget(null);
	    				initialTargetArea = 0;
	    			}
		    	}
		    			    	
		    	if (trackMarker)
		    	{
		    		// Call markerDetection class to see if markers are present in the current
		    		// video stream image.
	    			found = markerDetector.detectMarkers();
    			
	    			if (found)
	    			{
	    				if (lastDetectionTime != 0) 
	    				{
	    					long elapsedTime = System.currentTimeMillis() - lastDetectionTime;
	    					logger.fine("time: " + elapsedTime);
	    				}
	    				
	    				// record time of detection.
	    				lastDetectionTime = System.currentTimeMillis();
	    				
		    			// How many markers are detected? This is just information.
	    				markerCount = markerDetector.getMarkerCount();
	
	    				logger.finer("marker count=" + markerCount);
	    				
	    				// Get the array of rectangles describing the location and size
	    				// of the detected markers.
	    				ArrayList<Rect> markers = markerDetector.getMarkerTargets();
	    				
	    				// Get marker id of first marker.
	    				markerId = markerDetector.getMarkerId(0);
	    				
	    				// Track the first marker by computing flyRC input to center marker
	    				// in camera view and adjust distance to marker based on marker
	    				// size in the camera view. We also allow a momentary loss of marker
	    				// before switching back to search mode.
	    				
	    				trackingResult = followTarget(markers.get(0));
	    			} else if (System.currentTimeMillis() - lastDetectionTime < 1000) {
	    				trackingResult = new TrackingResult(0, 0);
	    			} else {
	    				logger.fine("no marker");
		    			camera.addTarget(null);
	    				markerId = 0;
	    				initialTargetArea = 0;
	    				// Set drone to rotate right slowly to acquire marker.
	    				trackingResult = new TrackingResult(40, 0);
	    			}
		    	}
		    	
    			// If flying, pass the controller joystick deflection to the drone via
		    	// the flyRC command. Bypass if we are in marker tracking mode.
		    	
		    	if (drone.isFlying() && !trackMarker)
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
		    	
		    	// If flying and we are marker tracking, fly drone according to tracking result.
		    	
		    	if (drone.isFlying() && trackMarker)
		    	{
		    		//                 L/R        F/B                U/D            YAW
	    			telloControl.flyRC(0, trackingResult.forwardBack, 0, trackingResult.leftRight);
		    	}
		    	
		    	// Speed up the loop to make the drone more responsive to marker movement.
		    	Thread.sleep(25);
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
    	 return String.format("Batt: %d  Alt: %d  Hdg: %d  Rdy: %b  Track: %b  Id: %d", drone.getBattery(), drone.getHeight(), 
    			drone.getHeading(), drone.isFlying(), trackMarker, markerId);
	}
	
	private class TrackingResult
	{
		public int	leftRight, forwardBack;
		
		public TrackingResult(int leftRight, int forwardBack)
		{
			this.leftRight = leftRight;
			this.forwardBack = forwardBack;
		}
	}
	
	// Given rectangle around target, compute yaw offset that can be used to turn drone to
	// center target in field of vision and adjusts forward/backward position to maintain 
	// initial to target distance.
	
	private TrackingResult followTarget(Rect target)
	{
		// Clear any existing target rectangle.
		camera.addTarget(null);
		
		// Outline the target being tracked.
		camera.addTarget(target);
		
		// This is field of view size.
		Size imageSize = camera.getImageSize();
		
		// get size of target outline area.
		int targetArea = target.height * target.width;
		
		// If we have just acquired target, record initial size. Size comparison
		// is how we determine distance to target.
		
		if (initialTargetArea == 0) initialTargetArea = targetArea;
		
		// Compute center point of target in the camera view image.
		int targetCenterX = target.x + target.width / 2;
		int imageCenterX = (int) (imageSize.width / 2);
		
		// offset minus indicates target is left of image center,
		// plus to the right. If target is left, drone needs to turn
		// left to center the target in the image which is a minus yaw value.
		
		int offset = targetCenterX - imageCenterX;

		logger.fine("offset=" + offset);
		
		offset *= .25;	// Scale offset down;
		
		// Determine change in distance from last target acquisition.
		
		double distance = initialTargetArea - targetArea;
		
		logger.fine(String.format("ia=%d  ta=%d  dist=%.0f", initialTargetArea, targetArea, distance));
		
		//initialTargetArea = targetArea;
		
		double scaleFactor = 0.0;
		
		// If distance is small, call it good otherwise the drone
		// hunts back and forth. Value must be determined by testing.
		
		if (Math.abs(distance) < 2000.0) 
			distance = 0.0;
		else
		{
			// Scale distance change to a fwd/back movement value of 20% for flyRC command.
			// For some unknown reason, need more power to fly forward than back and even
			// with 30%, forward seems not reliable.
			// scaleFactor must be positive to preserve the sign of distance value.
			
			scaleFactor = 20.0 / Math.abs(distance);
		
			distance = distance * scaleFactor;
		}
		
		logger.fine(String.format("dist=%.1f  fact=%f",  distance, scaleFactor));
		
		return new TrackingResult(offset, (int) distance);
	}
}
