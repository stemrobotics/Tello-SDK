package tello;

import static java.lang.Thread.sleep;

import java.util.logging.Level;
import java.util.logging.Logger;

import tellolib.command.TelloFlip;
import tellolib.communication.TelloConnection;
import tellolib.control.TelloControl;
import tellolib.drone.TelloDrone;

public class Demo4
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
		    
		    telloControl.takeOff();
		    
		    telloControl.startStatusMonitor();
		    
		    // The drone will land automatically if it does not receive
		    // a new command within 15 seconds. This thread will make
		    // sure the drone keeps flying if your program does not send
		    // commands regularly enough. The sleep call demonstrates this.
		    // The startKeepAlive() method will launch a thread that pings
		    // the drone regularly so it will keep flying if you have a
		    // prolonged wait in your programs.
		    
		    //telloControl.startKeepAlive();
		
		    logger.info("waiting...");
		    
		    try 
		    {
		      sleep(20000);
		    } catch (InterruptedException e) {
		      e.printStackTrace();
		    }
		    
		    logger.info("done waiting...");
		    
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
