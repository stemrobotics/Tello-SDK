package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class Demo3
{
	private final Logger logger = Logger.getGlobal(); 

	public void execute()
	{
		logger.info("start");
		
	    TelloControl telloControl = TelloControl.getInstance();
	    
	    TelloDrone drone = TelloDrone.getInstance();

	    telloControl.setLogLevel(Level.FINE);

	    try 
	    {
		    telloControl.connect();
		    
		    telloControl.enterCommandMode();
		    
		    // Now we will fly. This command will cause the drone to take
		    // off to its default initial altitude. Note that taking off
		    // can take some time and we wait in this method until the
		    // signals completion of take off.
		    
		    telloControl.takeOff();
		    
		    telloControl.startStatusMonitor();
		    		    
		    // Now we will execute a series of movement commands.
		    // Distances in centimeters.
		    
	    	telloControl.forward(50);
		    
		    telloControl.backward(50);
		    
		    telloControl.up(50);
		    
		    telloControl.down(50);
		    
		    telloControl.left(50);
		    
		    telloControl.right(50);
		    
		    telloControl.rotateLeft(90);
		    
		    telloControl.rotateRight(90);
		    
		    // fly a curve to a point 3.25 feet in front of the drone and 
		    // 1.5 feet higher.
		    telloControl.curve(25, 25, 0, 100, 0, 50, 20);
		    
		    //telloControl.doFlip(TelloFlip.backward);
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
}
