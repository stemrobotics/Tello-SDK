package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class Demo2
{
	private final Logger logger = Logger.getGlobal(); 

	public void execute()
	{
		logger.info("start");
		
	    TelloControl telloControl = TelloControl.getInstance();
	    
	    // Get a reference to global Drone class.
	    
	    TelloDrone drone = TelloDrone.getInstance();
	    
	    telloControl.setLogLevel(Level.FINE);

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();

		    // Starts a background thread to automatically receive the status
		    // information transmitted regularly by the drone. That thread
		    // constantly updates the Drone class global instance to you can
		    // call methods on the drone class to get drone status info.
		    
		    telloControl.startStatusMonitor();
		    
		    int battery = 0, speed = 0, time = 0, height = 0, temp = 0;
		    
		    double baro = 0, tof = 0;
		    
		    String sn = "", sdk = "";
		    
		    int[] attitude;
		    double [] acceleration, velocity;
		    
			battery = drone.getBattery();
			
			speed = drone.getSpeed();
			
			time = drone.getTime();
			
			baro = drone.getBarometer();
			
			height = drone.getHeight();
			
			tof = drone.getTof();
			
			temp = drone.getTemp();
			
			// Next two items not sent with auto drone status we need
			// to get them directly from the drone with a command.
			
			sn = telloControl.getSN();
			
			sdk = telloControl.getSDK();
			
			attitude = drone.getAttitude();
			
			acceleration = drone.getAcceleration();
			
			velocity = drone.getVelocity();
			    
		    logger.info("battery level=" + battery + ";speed=" + speed + ";time=" + time);
		    logger.info("baro=" + baro + ";height=" + height + ";tof=" + tof + ";temp=" + temp);
		    logger.info("sdk=" + sdk + ";sn=" + sn);
		    logger.info("pitch=" + attitude[0] + ";roll=" + attitude[1] + ";yaw=" + attitude[2]);
		    logger.info("accel x=" + acceleration[0] + ";y=" + acceleration[1] + ";z=" + acceleration[2]);
		    logger.info("veloc x=" + velocity[0] + ";y=" + velocity[1] + ";z=" + velocity[2]);
	    }	
	    catch (Exception e) {
	    	e.printStackTrace();
	    } finally 
	    {
	    	// Error or not, make sure the drone lands before program ends. Note we
	    	// only want to issue land command if we are connected and the drone is flying.
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
}
