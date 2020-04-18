package tello;

import static java.lang.Thread.sleep;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.control.TelloControlInterface;
import tellolib.drone.TelloDrone;

public class CommandTest
{
	private final Logger logger = Logger.getGlobal(); 

	public void executeCommandTest()
	{
		logger.info("start");
		
	    TelloControlInterface telloControl = TelloControl.getInstance();
	    
	    TelloDrone drone = TelloDrone.getInstance();
	    
	    telloControl.setLogLevel(Level.FINER);

	    try 
	    {
		    telloControl.connect();
		    telloControl.enterCommandMode();
		    telloControl.takeOff();
		    
		    telloControl.startStatusMonitor();
		    
		    telloControl.startKeepAlive();
		
		    logger.info("waiting...");
		    
		    try 
		    {
		      sleep(2000);
		    } catch (InterruptedException e) {
		      e.printStackTrace();
		    }
		    
		    logger.info("done waiting...");
		    
		    //telloControl.doFlip(TelloFlip.backward);
		    
		    int battery = 0, speed = 0, time = 0, height = 0, temp = 0;
		    
		    double baro = 0, tof = 0;
		    
		    String sn = "", sdk = "";
		    
		    int[] attitude;
		    double [] acceleration, velocity;
		    
			battery = drone.getBattery();
			
			speed = drone.getSpeed();
			
			time = telloControl.getTime();
			
			baro = drone.getBarometer();
			
			height = drone.getHeight();
			
			tof = drone.getTof();
			
			temp = drone.getTemp();
			
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
		    
		    //telloControl.streamOn();
		    
		    //telloControl.streamOff();
		    
		    //telloControl.emergency();
		    
	    	//telloControl.forward(100);
		    
//		    telloControl.backward(100);
//		    
//		    telloControl.up(100);
//		    
//		    telloControl.down(100);
//		    
//		    telloControl.left(50);
//		    
//		    telloControl.right(50);
//		    
//		    telloControl.rotateLeft(90);
//		    
//		    telloControl.rotateRight(90);
//		    
//		    telloControl.goTo(20, 20, 20, 50);
	    }	
	    catch (Exception e) {
	    	e.printStackTrace();
	    } finally 
	    {
	    	if (telloControl.getConnection() == TelloConnection.CONNECTED)
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
