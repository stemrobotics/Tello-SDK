package tello;

import java.util.logging.Level;
import java.util.logging.Logger;

// Main class contains the method main(), which is what Java JRE 
// calls to begin execution when a Java program is run.
public class Main 
{
	// Get reference to Java's built-in logging class.
	private static final Logger logger = Logger.getGlobal();

	// Main always called to start a Java program.
	public static void main(String[] args) throws Exception 
	{
		// Set default logging level to a bit more detailed than INFO. Logging
		// statements of INFO or above (WARNING,SEVERE) will be output to
		// the console window. INFO level is typically used to show high
		// level informational messages.
	  	logger.setLevel(Level.INFO);
	  	
	    logger.info("start");
	    
	    // Create an instance of the drone program (class) we want to run.
//	    Demo1 demo = new Demo1();

//		Demo2 demo = new Demo2();

//	    Demo3 demo = new Demo3();

//	    Demo4 demo = new Demo4();

//	    Demo5 demo = new Demo5();
	    
//	    FlySquare demo = new FlySquare();

//	    FlyGrid demo = new FlyGrid();

//	    FindMissionPad demo = new FindMissionPad();

//	    FlyController demo = new FlyController();

//	    FindMissionPad2 demo = new FindMissionPad2();
	
	    FindFace demo = new FindFace();

//	    FindFace2 demo = new FindFace2();
	
//	    FindMarker demo = new FindMarker();
	    
	    // Run that program.
	    demo.execute();

	    //CommandTest commandTest = new CommandTest();
	    
	    // Run that program.
	    //commandTest.executeCommandTest();
	    
	    //ControllerTest controllerTest = new ControllerTest();
	    
	    //controllerTest.executeControllerTest();
	    
	    logger.info("end");
	}
}