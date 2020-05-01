package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.camera.MissionDetectionCamera;
import tellolib.camera.TelloCamera;
import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class FindMissionPad2
{
	private final Logger logger = Logger.getGlobal(); 

	private TelloControl	telloControl;
	private TelloDrone		drone;
	private TelloCamera		camera;
	private boolean			padFound;
	
	public void execute()
	{
		logger.info("start");
		
	    telloControl = TelloControl.getInstance();
	    
	    drone = TelloDrone.getInstance();
	    
	    camera = TelloCamera.getInstance();

	    telloControl.setLogLevel(Level.FINEST);

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();
		    
		    telloControl.startStatusMonitor();
		    
		    telloControl.takeOff();
		    
		    // Turn on Mission Pad detection and select camera to monitor.
		    
		    telloControl.setMissionMode(true, MissionDetectionCamera.downward);
		    
		    // Create background thread to watch for mission pad under drone. It 
		    // will signal to stop the search. WatchForPad is defined below.
		    
		    WatchForPad padWatcher = new WatchForPad();
		    
		    // Start the thread. start() calls Thread run method.
		    
		    padWatcher.start();
		    
		    // Now we will execute a series of movement commands to fly in a grid
		    // pattern. Distances in centimeters.
		    
		    for (int i = 0; i < 2; i++)
		    {
		    	if (flyForward(100, 30)) break;
		    	
		    	telloControl.rotateRight(90);

		    	if (flyForward(20, 10)) break;
		    	
		    	telloControl.rotateRight(90);
		    	
		    	if (flyForward(100, 30)) break;
		    	
		    	telloControl.rotateLeft(90);

		    	if (flyForward(20, 10)) break;
		    	
		    	telloControl.rotateLeft(90);
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
	
	private boolean flyForward(int distance, int speed) throws InterruptedException
	{
		int startTime = drone.getTime();
		int endTime = distance / speed + startTime;
		
		logger.info(String.format("st=%d  et=%d", startTime, endTime));

		while (drone.getTime() < endTime && !padFound)
		{
			telloControl.flyRC(0, speed, 0, 0);
			
    		Thread.sleep(100);	// Wait 10 ms.
		}
		
		telloControl.flyRC(0, 0, 0, 0);
		
		return padFound;
	}
	
	private boolean flyForward2(int distance, int speed) throws InterruptedException
	{
		long startTimeMS = System.currentTimeMillis();
		long endTimeMS = (long) ((double) distance / (double) speed * 1000.0 + startTimeMS);
		
		logger.info(String.format("st=%d  et=%d  flt=%d", startTimeMS, endTimeMS, endTimeMS - startTimeMS));

		while (System.currentTimeMillis() < endTimeMS && !padFound)
		{
			telloControl.flyRC(0, speed, 0, 0);
			
    		Thread.sleep(10);	// Wait 10 ms.
		}
		
		telloControl.flyRC(0, 0, 0, 0);
		
		return padFound;
	}
	
	// Thread class that runs in background and watches for the status feed from drone
	// to indicate a mission pad is recognized by the selected camera.
	
	private class WatchForPad extends Thread
	{
		public void run()
		{
			int padId = 0;
			
			logger.info("Pad monitor thread started");
			
	    	try
	    	{
	    		// Loop until we are done.
	    		
    	    	while (!isInterrupted() && drone.isFlying())
    	    	{
    	    		padId = drone.getMissionPadId();

    	    		
    	    		// Pad id greater than zero means a pad is detected.
    	    		
    	    		if (padId > 0) 
    	    		{
    	    			padFound = true;
    	    			logger.info(String.format("mission pad %d detected", padId));
    	    			break;	// Kicks us out of the while loop.
    	    		}
    	    		
    	    		sleep(1000);	// Wait 1000 ms or 1 second.
    	    	}
	    	}
	    	catch (InterruptedException e) { }
	    }
	}
}

