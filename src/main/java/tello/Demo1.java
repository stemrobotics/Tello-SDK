package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;

public class Demo1
{
	// Get an reference to the Java Global Logger that was configured and level
	// set by the Main class.
	private final Logger logger = Logger.getGlobal(); 

	public void execute()
	{
		logger.info("start");
		
		// Get a reference to the global TelloControl class. We will use
		// this reference to call methods that control the drone.
		
	    TelloControl telloControl = TelloControl.getInstance();
	    
	    // TelloControl class has a separate logger so it can log at
	    // a different level than the code that calls it. TelloControl
	    // logging is off by default. The rest of the Tello SDK classes use
	    // this separate logger as well.
	    // Set log level to FINE to see information from the SDK classes 
	    // about what they are doing. Helps to understand how they work
	    // and helpful for debugging.
	    
	    telloControl.setLogLevel(Level.FINE);

	    // Wrap all code in a try/catch block so that we trap any error
	    // that occurs and handle it by safely landing the drone.
	    
	    try 
	    {
	    	// Call method to establish network connection with the drone.
	    	
		    telloControl.connect();
		    
		    // Send command to put drone in 'command mode', where it will
		    // accept the rest of the commands it knows instead of flying
		    // itself.
		    
		    telloControl.enterCommandMode();
				    
		    // In this demo we will issue drone commands to retrieve all of
		    // the status information available from the drone. Here we create
		    // variables to hold the information.
		    
		    int battery = 0, speed = 0, time = 0, height = 0, temp = 0;
		    
		    double baro = 0, tof = 0;
		    
		    String sn = "", sdk = "";
		    
		    int[] attitude;
		    double [] acceleration;
		    
		    // Issue commands to get the drone status information. When to call 
		    // these methods on TelloControl, the data is retrieved and stored
		    // in the global Drone class as well as returned for your use.
		    
			battery = telloControl.getBattery();
			
			speed = telloControl.getSpeed();
			
			time = telloControl.getTime();
			
			baro = telloControl.getBarometer();
			
			height = telloControl.getHeight();
			
			tof = telloControl.getTof();
			
			temp = telloControl.getTemp();
			
			sn = telloControl.getSN();
			
			sdk = telloControl.getSDK();
			
			attitude = telloControl.getAttitude();
			
			acceleration = telloControl.getAcceleration();
			    
		    logger.info("battery level=" + battery + ";speed=" + speed + ";time=" + time);
		    logger.info("baro=" + baro + ";height=" + height + ";tof=" + tof + ";temp=" + temp);
		    logger.info("sdk=" + sdk + ";sn=" + sn);
		    logger.info("pitch=" + attitude[0] + ";roll=" + attitude[1] + ";yaw=" + attitude[2]);
		    logger.info("accel x=" + acceleration[0] + ";y=" + acceleration[1] + ";z=" + acceleration[2]);
	    }	
	    catch (Exception e) {
	    	// If an error occurs, log it to the console window.
	    	e.printStackTrace();
	    } finally {
	    	// Error or not, make sure the drone lands before program ends. Note we
	    	// only want to issue land command if we are connected.
	    	if (telloControl.getConnection() == TelloConnection.CONNECTED)
	    	{
	    		try
	    		{telloControl.land();}
	    		catch(Exception e) { e.printStackTrace();}
	    	}
	    }
	    
	    // Close network connection to drone.
    	telloControl.disconnect();
	    
	    logger.info("end");
	}
}
