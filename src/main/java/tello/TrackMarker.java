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

		    	// X button toggles marker detection and tracking. Note when detectMarkers is true we
		    	// execute the code on each loop, when false we skip code unless X button
		    	// is pressed.
		    	
		    	if (currState.xJustPressed || trackMarker)
		    	{
		    		// Toggle detectMarker on X button.
		    		if (currState.xJustPressed) trackMarker = !trackMarker;
		    		
		    		// Call markerDetection class to see if markers are present in the current
		    		// video stream image.
	    			found = markerDetector.detectMarkers();

	    			// Clear any previous target rectangles.
	    			camera.addTarget(null);
    			
	    			if (found)
	    			{
		    			// How many markers are detected? This is just information.
	    				markerCount = markerDetector.getMarkerCount();
	
	    				logger.finer("marker count=" + markerCount);
	    				
	    				// Get the array of rectangles describing the location and size
	    				// of the detected markers.
	    				ArrayList<Rect> markers = markerDetector.getMarkerTargets();
	    				
	    				// Get marker id of first marker.
	    				markerId = markerDetector.getMarkerId(0);
	    				
	    				// Track the first marker computing flyRC input to center marker
	    				// in camera view and adjust distance to marker based on marker
	    				// size in the camera view.
	    				
	    				trackingResult = followTarget(markers.get(0));
	    			} else 
	    			{
	    				markerId = 0;
	    				// Set drone to rotate slowly to acquire marker.
	    				trackingResult = new TrackingResult(2, 0);
	    			}
	    			
	    			// Clear any target rectangles if marker detection was turned off.
	    			if  (!trackMarker) camera.addTarget(null);
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
	
	private TrackingResult followTarget(Rect target)
	{
		// Outline the target being tracked.
		camera.addTarget(target);
		
		Size imageSize = camera.getImageSize();
		
		// get size of target area.
		int targetArea = target.height * target.width;
		
		// If we have just aquired target, record initial size. Size comparison
		// is how we determine distance to target.
		
		if (initialTargetArea == 0) initialTargetArea = targetArea;
		
		// Compute center point of target in the camera view image.
		int targetCenterX = target.x + target.width / 2;
		int imageCenterX = (int) (imageSize.width / 2);
		
		// offset minus indicates target is left of image center,
		// plus to the right. If target is left, drone needs to turn
		// left to center the target in the image.
		
		int offset = targetCenterX - imageCenterX;

		logger.finer("offset=" + offset);
		
		// If offset is small, call it good otherwise the drone
		// hunts back and forth. All of the constants determine
		// experimentally.
		
		//if (Math.abs(offset) < 20) offset = 0;
		
		// "speed" of rotation in degrees. Need to rotate faster
		// if offset is larger to better track target movement.
		// 5 degrees is minimum.
		
//		int rotate = 5;
//		
//		if (offset / 50 != 0) rotate = rotate * Math.abs(offset) / 50;
//		
//		if  (offset > 0)
//			telloControl.rotateRight(rotate);
//		else if (offset < 0)
//			telloControl.rotateLeft(rotate);
		
		// Determine change in distance from first target acquisition.
		
		int distance = initialTargetArea - targetArea;
		
		logger.finer(String.format("ia=%d  ta=%d  dist=%d", initialTargetArea, targetArea, distance));
		
		// If distance is small, call it good otherwise the drone
		// hunts back and forth. Value must be determined by testing.
	
		if (Math.abs(distance) < 5000) distance = 0;
		
		// scale distance to a value compatible with flyRC command.
		
		distance = (int) (distance * 1.0);
	
//		// Centimeters to move to adjust distance to target. 20 cm
//		// is the default and minimum. Need to move more if target is far away
//		// less if up close. Not yet done.
//		
//		int forwardBack = 20;
//		
//		// Plus distance means the target has moved away, minus means
//		// moved closer.
//		
//		if (distance > 0)
//			telloControl.forward(forwardBack);
//		else if (distance < 0)
//			telloControl.backward(forwardBack);
		
		return new TrackingResult(offset, distance);
	}
}
