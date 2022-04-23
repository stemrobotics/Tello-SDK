package tello;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Rect;
import org.opencv.core.Size;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

import tellolib.camera.ArucoMarkers;
import tellolib.camera.FaceDetection;
import tellolib.camera.MissionDetectionCamera;
import tellolib.camera.TelloCamera;
import tellolib.command.TelloFlip;
import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.control.TelloControlInterface;
import tellolib.drone.TelloDrone;

public class ControllerTest
{
	private final Logger 		logger = Logger.getGlobal(); 
	private ControllerManager	controllers;
	private boolean				flying;
	private int					initialTargetArea =  0;
	
    private TelloControlInterface 	telloControl;
    private TelloCamera				camera;
    private ArucoMarkers			markers;
    private TelloDrone				drone;
	
	public ControllerTest()
	{
		controllers = new ControllerManager();
		controllers.initSDLGamepad();
		
		telloControl = TelloControl.getInstance();
		
		camera = TelloCamera.getInstance();
		
		drone = TelloDrone.getInstance();
	}

	public void executeControllerTest()
	{
		int		leftX, leftY, rightX, rightY, deadZone = 10;
		boolean	recording = false, trackArucoMarker = false, trackFace = false;
		
		logger.info("start");
	    
	    telloControl.setLogLevel(Level.FINE);

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();
		    
		    telloControl.startStatusMonitor();
		    
		    telloControl.streamOn();
		    
		    telloControl.startKeepAlive();
		    
		    camera.startVideoCapture(true);
		    
		    telloControl.setMissionMode(true, MissionDetectionCamera.downward);
		    
		    while(true) 
		    {
		    	ControllerState currState = controllers.getState(0);
		    	  
		    	if (!currState.isConnected) 
		    	{
		    		logger.severe("xbox controller not connected");
		    		break;
		    	}
		    	  
		    	if (currState.backJustPressed) 
		    	{
		    		logger.info("back button");
		    		
		    		if (flying)
		    		{
		    			telloControl.land();
		    			flying = false;
		    		}
		    		
		    		break;
		    	}

		    	if (currState.startJustPressed)
		    	{
		    		logger.info("start button");
		    		
		    		if (flying)
		    		{
		    			telloControl.land();
		    			flying = false;
		    		}
		    		else
		    		{
		    			telloControl.takeOff();
		    			flying = true;
		    		}
		    	}
		    	
		    	if (currState.bJustPressed)
		    	{
		    		if (recording)
		    		{
		    			camera.stopRecording();
		    			recording = false;
		    		} else
		    			recording = camera.startRecording(System.getProperty("user.dir") + "\\Photos");
		    	}
		    	
		    	if  (currState.aJustPressed) camera.takePicture(System.getProperty("user.dir") + "\\Photos");
		    	
		    	if (currState.xJustPressed) 
		    	{
		    		camera.addTarget(null);
		    		camera.setContours(null);
		    		
		    		if (markers == null) markers = ArucoMarkers.getInstance();
	    			
	    			boolean found = markers.detectMarkers();
		    		
		    		logger.info("markers found=" + found);
		    		
		    		if (found)
		    		{
		    			int markerCount = markers.getMarkerCount();
		    			
		    			logger.info("marker count=" + markerCount);
		    			
		    			ArrayList<Rect> targets = markers.getMarkerTargets();
		    			
		    			camera.addTarget(targets.get(0));
		    			
		    			camera.setContours(markers.getMarkerContours());
		    			
		    			logger.info(String.format("screen %dh x %dw  target %dh x %dw", 
		    					camera.getImage().height(),
		    					camera.getImage().width(),
		    					targets.get(0).height, targets.get(0).width));
		    		}
		    	}

//		    	if (currState.lbJustPressed)
//		    	{
//	    			telloControl.addTarget(null);
//
//	    			boolean found = FaceDetection.getInstance().detectFaces();
//	    			
//	    			if (found)
//	    			{
//	    				int faceCount = FaceDetection.getInstance().getFaceCount();
//
//	    				logger.info("face count=" + faceCount);
//	    				
//	    				Rect[] faces = FaceDetection.getInstance().getFaces();
//	    				
//	    				telloControl.addTarget(faces[0]);
//	    			}
//		    	}
		    	
		    	if (currState.yJustPressed) 
		    	{
		    		drone.resetHeadingZero();
		    		drone.resetYawZero();
		    	}
		    	
		    	if (currState.rbJustPressed) 
		    	{
		    		trackArucoMarker = !trackArucoMarker;
		    		
		    		if  (!trackArucoMarker)
		    		{
		    			initialTargetArea = 0;
		    			camera.addTarget(null);
		    		}
		    	}
		    	
		    	if (flying && trackArucoMarker)
		    	{
	    			camera.addTarget(null);
	    			
	    			boolean found = markers.detectMarkers();
		    		
		    		//logger.info("markers found=" + found);
		    		
		    		if (found) 
		    		{
		    			ArrayList<Rect> targets = markers.getMarkerTargets();
		    			
		    			Rect target = targets.get(0);
		    			
		    			followTarget(target);
		    		}
		    	}
		    	
		    	if (currState.lbJustPressed) 
		    	{
		    		trackFace = !trackFace;
		    		
		    		if  (!trackArucoMarker)
		    		{
		    			initialTargetArea = 0;
		    			camera.addTarget(null);
		    		}
		    	}
		    	
		    	if (flying && trackFace)
		    	{
	    			camera.addTarget(null);
	    			
	    			boolean found = FaceDetection.getInstance().detectFaces();
		    		
		    		logger.info("faces found=" + found);
		    		
		    		if (found) 
		    		{
	    				Rect[] faces = FaceDetection.getInstance().getFaces();
	    				
	    				camera.addTarget(faces[0]);
		    			
		    			followTarget(faces[0]);
		    		}
		    	}
	    		
	    		//logger.info("heading=" + telloControl.getHeading() + ";yaw=" + telloControl.getYaw());
		    	
		    	if (flying && !(trackArucoMarker || trackFace))
		    	{
		    		// scale controller stick axis range -1.0 to + 1.0 to -100 to + 100
		    		// used by the drone flyRC command. Apply a dead zone to allow
		    		// for stick axis not always returning 0 when released.
		    		leftX = deadZone((int) (currState.leftStickX * 100.0), deadZone);
		    		leftY = deadZone((int) (currState.leftStickY * 100.0), deadZone);
		    		rightX = deadZone((int) (currState.rightStickX * 100), deadZone);
		    		rightY = deadZone((int) (currState.rightStickY * 100), deadZone);
		    		
		    		//logger.info("lr=" + rightX + " fb=" + rightY + " ud=" + leftY + " yaw=" + leftX);
		    		//                  L/R      F/B    U/D    YAW
	    			telloControl.flyRC(rightX, rightY, leftY, leftX);
		    		
		    		if (currState.dpadUpJustPressed) telloControl.doFlip(TelloFlip.forward);
		    		if (currState.dpadDownJustPressed) telloControl.doFlip(TelloFlip.backward);
		    		if (currState.dpadLeftJustPressed) telloControl.doFlip(TelloFlip.left);
		    		if (currState.dpadRightJustPressed) telloControl.doFlip(TelloFlip.right);
		    		
		    		if (currState.yJustPressed) telloControl.stop();
		    	}
		    	
		    	TelloCamera.getInstance().setStatusBar(String.format("Batt: %d  Alt: %d  Hdg: %d  Mtrk: %b  Face: %b", 
		    			drone.getBattery(), drone.getHeight(), drone.getHeading(), trackArucoMarker, trackFace));
		    	
		    	Thread.sleep(100);
		    }
	    }	
	    catch (Exception e) {
	    	e.printStackTrace();
	    } finally {
	    	if (telloControl.getConnection() == TelloConnection.CONNECTED)
	    	{
	    		try
	    		{ 
	    			if (flying) telloControl.land(); 
	    		}
	    		catch(Exception e) { e.printStackTrace(); }
	    	}
	    }
	    
    	telloControl.disconnect();
    	
//    	logger.info("mpid=" + telloControl.getMissionPadId() + ";mxyz=" + telloControl.getMissionPadxyz()[0] +
//    			"," + telloControl.getMissionPadxyz()[1] + "," + telloControl.getMissionPadxyz()[2] +
//    			";mpry=" + telloControl.getMissionPadpry()[0] + "," + telloControl.getMissionPadpry()[1] + "," +
//    			telloControl.getMissionPadpry()[2]);
	    
	    logger.info("end");

	}
	
	private int deadZone(int value, int minValue)
	{
		if (Math.abs(value) < minValue) value = 0;
		
		return value;
	}
	
	private void followTarget(Rect target)
	{
		camera.addTarget(target);
		
		Size imageSize = TelloCamera.getInstance().getImageSize();
		
		int targetArea = target.height * target.width;
		
		if (initialTargetArea == 0) initialTargetArea = targetArea;
		
		int targetCenterX = target.x + target.width / 2;
		int imageCenterX = (int) (imageSize.width / 2);
		
		// offset minus indicates target is left of image center,
		// plus to the right. If target is left, drone needs to turn
		// left to center the target in the image.
		
		int offset = targetCenterX - imageCenterX;

		logger.info("offset=" + offset);
		
		// If offset is small, call it good otherwise the drone
		// hunts back and forth. All of the constants determine
		// experimentally.
		
		if (Math.abs(offset) < 20) offset = 0;
		
		// "speed" of rotation in degrees. Need to rotate faster
		// if offset is larger to better track target movement.
		// 5 degrees is minimum.
		
		int rotate = 5;
		
		if (offset / 50 != 0) rotate = rotate * Math.abs(offset) / 50;
		
		if  (offset > 0)
			telloControl.rotateRight(rotate);
		else if (offset < 0)
			telloControl.rotateLeft(rotate);
		
		// Determine change in distance from first target acquisition.
		
		int distance = initialTargetArea - targetArea;
		
		logger.info(String.format("ia=%d  ta=%d  dist=%d", initialTargetArea, targetArea, distance));
		
		// If distance is small, call it good otherwise the drone
		// hunts back and forth.
	
		if (Math.abs(distance) < 5000) distance = 0;
	
		// Centimeters to move to adjust distance to target. 20 cm
		// is the default and minimum. Need to move more if target is far away
		// less if up close. Not yet done.
		
		int forwardBack = 20;
		
		// Plus distance means the target has moved away, minus means
		// moved closer.
		
		if (distance > 0)
			telloControl.forward(forwardBack);
		else if (distance < 0)
			telloControl.backward(forwardBack);
	}
}
