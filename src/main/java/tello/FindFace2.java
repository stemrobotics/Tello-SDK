package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Rect;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import tellolib.camera.FaceDetection;
import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class FindFace2
{
	private final Logger logger = Logger.getGlobal(); 

	private TelloControl		telloControl;
	private TelloDrone			drone;
	private TelloCamera			camera;
	private ControllerManager	controllers;
	private FaceDetection		faceDetector;
	private boolean				detectFaces = false;
	
	public void execute() throws Exception
	{
		int		leftX, leftY, rightX, rightY, deadZone = 10;
		Thread	checkForFacesThread = null;

		logger.info("start");
	    
	    controllers = new ControllerManager();
		controllers.initSDLGamepad();
    	
    	ControllerState currState = controllers.getState(0);
    	  
    	// No controller, no fly!
    	
    	if (!currState.isConnected) throw new Exception("controller not connected");

	    telloControl = TelloControl.getInstance();
	    
	    drone = TelloDrone.getInstance();
	    
	    camera = TelloCamera.getInstance();
	    
	    // Create instance of FaceDetection support class.
	    
	    faceDetector = FaceDetection.getInstance();
	    		
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

		    	// X button toggles face detection.
		    	
		    	if (currState.xJustPressed)
		    	{
		    		// Toggle detectFaces on X button.
		    		detectFaces = !detectFaces;
		    		
		    		// If true, we start thread to watch for faces else we would
		    		// already have thread running to we signal it to stop.
		    		
	    			if (detectFaces)
	    			{
	    				checkForFacesThread = new CheckForFaces();
	    				checkForFacesThread.start();
	    			}
	    			else 
	    				checkForFacesThread.interrupt();
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
    	 return String.format("Batt: %d  Alt: %d  Hdg: %d  Rdy: %b  Detect: %b", drone.getBattery(), drone.getHeight(), 
    			drone.getHeading(), drone.isFlying(), detectFaces);
	}

	// Class with a class, called a nested or inner class. It has the features of
	// the Java Tread class and our code to run in the thread.
	
	private class CheckForFaces extends Thread
	{
		public void run()
		{
			boolean	found;
			int		faceCount;
			
			try
			{
				while (!isInterrupted())
				{
					// Call FaceDetection class to see if faces are present in the current
					// video stream image.
					found = faceDetector.detectFaces();
			
					// Clear any previous target rectangles.
					camera.addTarget(null);
					
					if (found)
					{
						// How many faces are detected? This is just information.
						faceCount = faceDetector.getFaceCount();
			
						logger.finer("face count=" + faceCount);
						
						// Get the array of rectangles describing the location and size
						// of the detected faces.
						Rect[] faces = faceDetector.getFaces();
						
						// Set first face rectangle to be drawn on video feed.
						camera.addTarget(faces[0]);
					}
			    	
			    	Thread.sleep(200);
				}
			} 
	    	catch (InterruptedException e) {}
	    	catch (Exception e) {e.printStackTrace();}
			
			// Clear any target rectangles if face detection is off.
			camera.addTarget(null);
		}
	}
}

