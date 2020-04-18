package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
  private static final Logger logger = Logger.getGlobal();

  public static void main(String[] args) 
  {
	  	logger.setLevel(Level.FINE);
	  	
	    logger.info("start");
	    
	    CommandTest commandTest = new CommandTest();
	    
	    commandTest.executeCommandTest();
	    
	    //ControllerTest controllerTest = new ControllerTest();
	    
	    //controllerTest.executeControllerTest();
	    
	    logger.info("end");
  }
}