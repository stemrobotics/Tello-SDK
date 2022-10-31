package tellolib.control;

import tellolib.camera.MissionDetectionCamera;
import tellolib.camera.TelloCamera;
import tellolib.command.BasicTelloCommand;
import tellolib.command.ComplexTelloCommand;
import tellolib.command.TelloCommandInterface;
import tellolib.command.TelloCommandValues;
import tellolib.command.TelloFlip;
import tellolib.communication.TelloCommunication;
import tellolib.communication.TelloConnection;
import tellolib.drone.TelloDrone;
import tellolib.drone.TelloModel;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TelloControl interface. Provides high level wrappers for
 * library classes used to send commands to the drone.
 */
public class TelloControl implements TelloControlInterface 
{
	private final 	Logger			logger = Logger.getLogger("Tello");
	private final 	ConsoleHandler 	handler = new ConsoleHandler();
	
	private TelloDrone			drone;
	
	private TelloCommunication 	communication;
	
	//private TelloCamera			camera;
	
	private Thread				statusMonitorThread, keepAliveThread;
	
	// Private constructor, holder class and getInstance() implement this
	// class as a singleton.
	
	private TelloControl() 
	{
		logger.setLevel(Level.OFF);
		handler.setLevel(Level.OFF);
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		  
		drone = TelloDrone.getInstance();
		  
		communication = TelloCommunication.getInstance();
		
		//camera = TelloCamera.getInstance();
	}
    
	private static class SingletonHolder 
	{
        public static final TelloControl INSTANCE = new TelloControl();
    }
	
	/**
	 * Get the global instance of TelloControl.
	 * @return Global TelloControl instance.
	 */
	public static TelloControl getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	@Override
	public void setLogLevel(Level logLevel)
	{
		logger.setLevel(logLevel);
		handler.setLevel(logLevel);
	}
	
	@Override
	public void connect() 
	{
		communication.connect();
		drone.setConnection(TelloConnection.CONNECTED);
	}
	
	@Override
	public void disconnect() 
	{
		stopStatusMonitor();
		stopKeepAlive();
		//camera.stopVideoCapture();
		  
		// This will land if we are still flying.

		if (drone.isConnected() && drone.isFlying()) land(); 
		  
		communication.disconnect();
		drone.setConnection(TelloConnection.DISCONNECTED);
	}
	  
	public TelloConnection getConnection() 
	{
		return drone.getConnection();
	}
	
	@Override
	public void enterCommandMode() 
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.COMMAND_MODE);
		communication.executeCommand(command);
		
		// Determine drone model. This method call will fail if not a Tello EDU and SDk 1.3.
		
		try
		{
			getSN();
		}
		catch (Exception  e) { drone.setModel(TelloModel.Basic); }
	}
	
	@Override
	public void takeOff() 
	{
		try
		{
			TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.TAKE_OFF);
			communication.executeCommand(command);
			drone.setFlying(true);
		}
		catch (Exception e) { throw e; }
	}
	
	@Override
	public void land() 
	{
		// We do this first so the crash detection code in status monitor will not think
		// we have crashed when height goes to zero.
	  	drone.setFlying(false);
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.LAND);
	  	communication.executeCommand(command);
	}
	
	@Override
	public void doFlip(TelloFlip telloFlip) 
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.FLIP, TelloFlip.toCommand(telloFlip));
		communication.executeCommand(command);
	}
	
	@Override
	public void setSpeed(Integer speed) 
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.SPEED, speed.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public void forward(Integer distance) 
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.FORWARD, distance.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public void backward(Integer distance)
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.BACK, distance.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public void right(Integer distance) 
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.RIGHT, distance.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public void left(Integer distance) 
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.LEFT, distance.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public void rotateRight(Integer angle) 
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.CW, angle.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public void rotateLeft(Integer angle)
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.CCW, angle.toString());
		communication.executeCommand(command);
	}
	  
	public int getBattery() 
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_BATTERY);
		String battery = communication.executeReadCommand(command);
		drone.setBattery(Integer.parseInt(battery.trim()));
		return drone.getBattery();
	}
	  
	public int getSpeed() 
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_SPEED);
		String speed = communication.executeReadCommand(command);
		drone.setSpeed((int) Double.parseDouble(speed.trim()));
		return drone.getSpeed();
	}

	@Override
	public void up( Integer distance )
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.UP, distance.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public void down( Integer distance )
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.DOWN, distance.toString());
		communication.executeCommand(command);
	}
	
	@Override
	public int getTime()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_FLY_TIME);
		String time = communication.executeReadCommand(command);
		drone.setTime(Integer.parseInt(time.trim().replaceAll("[^\\d.-]", "")));
		return drone.getTime();
	}
	
	@Override
	public int getHeight()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_HEIGHT);
		String height = communication.executeReadCommand(command);
		drone.setHeight(Integer.parseInt(height.trim().replaceAll("[^\\d.-]", "")) * 10);
		return drone.getHeight();
	}
	
	@Override
	public int getTemp()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_TEMPERATURE);
		String temp = communication.executeReadCommand(command);
		drone.setTemp(Integer.parseInt(temp.trim().split("~")[0].replaceAll("[^\\d.-]", "")));
		return drone.getTemp();
	}
	
	@Override
	public double getBarometer()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_BAROMETER);
		String barometer = communication.executeReadCommand(command);
		drone.setBarometer(Double.parseDouble(barometer.trim()));
		return drone.getBarometer();
	}
	
	@Override
	public int[] getAttitude()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_ATTITUDE);
		String attitude = communication.executeReadCommand(command);
		
		String spry[] = attitude.split(";");
		int pry[] = new int[3];

		for (int i = 0; i < 3; i++)
		{
			String axis[] = spry[i].split(":");
			pry[i] = Integer.parseInt(axis[1]);
			//logger.info(Integer.toString(pry[i]));
 		}

		drone.setAttitude(pry);
		
		return drone.getAttitude();
	}
	
	@Override
	public double[] getAcceleration()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_ACCELERATION);
		String acceleration = communication.executeReadCommand(command);
		
		String sxyz[] = acceleration.split(";");
		double xyz[] = new double[3];

		for (int i = 0; i < 3; i++)
		{
			String axis[] = sxyz[i].split(":");
			xyz[i] = Double.parseDouble(axis[1]);
			//logger.info(Double.toString(xyz[i]));
 		}

		drone.setAcceleration(xyz);
		return drone.getAcceleration();
	}
	
	@Override
	public double getTof()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.CURRENT_TOF);
		String tof = communication.executeReadCommand(command);
		drone.setTof(Double.parseDouble(tof.trim().replaceAll("[^\\d.-]", "")) / 10);
		return drone.getTof();
	}

	@Override
	public String getSN()
	{
		if (drone.getModel() == TelloModel.Basic)
		{
			logger.warning("SN command requires Tello EDU");
			return "";
		}
		
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.SN);
		String sn = communication.executeReadCommand(command);
		drone.setSN(sn.trim());
		return drone.getSN();
	}

	@Override
	public void goTo( Integer x, Integer y, Integer z, Integer speed )
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.GO, 
				x.toString() + " " + y.toString() + " " + z.toString() + " " + speed.toString());
		communication.executeCommand(command);
	}

	@Override
	public void flyRC( Integer lr, Integer fb, Integer ud, Integer yaw )
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.RC, 
				lr.toString() + " " + fb.toString() + " " + ud.toString() + " " + yaw.toString());
		communication.executeCommandNoWait(command);
	}

	@Override
	public String getSDK()
	{
		if (drone.getModel() == TelloModel.Basic)
		{
			logger.warning("SDK command requires Tello EDU");
			return "";
		}
		
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.SDK);
		String sdk = communication.executeReadCommand(command);
		drone.setSDK(sdk.trim());
		return drone.getSDK();
	}

	@Override
	public void stop()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.STOP);
		communication.executeCommand(command);
	}

	@Override
	public void emergency()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.EMERGENCY);
		communication.executeCommand(command);
	}

	@Override
	public void streamOn()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.ENABLE_VIDEO_STREAM);
		communication.executeCommand(command);
	}

	@Override
	public void streamOff()
	{
		TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.DISABLE_VIDEO_STREAM);
		communication.executeCommand(command);
	}

	@Override
	public void startStatusMonitor()
	{
		logger.fine("starting status monitor thread");
		
		if (statusMonitorThread != null) return;

		statusMonitorThread = new StatusMonitor();
		statusMonitorThread.start();
	}

	@Override
	public void stopStatusMonitor()
	{
		if (statusMonitorThread != null)
		{
			logger.fine("stopping status monitor thread");
			
			try
			{
				// Signal thread to stop.
				statusMonitorThread.interrupt();
				// Wait for thread to stop.
				statusMonitorThread.join();
			} catch (Exception e) {e.printStackTrace();}
		}
		
		statusMonitorThread = null;
	}
	
	private class StatusMonitor extends Thread
	{
		boolean		crashDetected;
		long		lastDetectionTime = 0;
		
		StatusMonitor()
		{
			logger.fine("monitor thread constructor");
			
			this.setName("StatusMonitor");
	    }
		
	    public void run()
	    {
			logger.fine("status monitor thread start");
			
	    	try
	    	{
	    		while (!isInterrupted())
	    		{
	    			String logData = communication.receiveStatusData();
	    			
	    			logger.finest(logData);
	    			
	    			String[] keyValuePairs = logData.split(";"); 

	    			int[] attpry = new int[3], mpxyz = new int[3], mppry = new int[3];
    				double[] accelxyz = new double[3], veloxyz = new double[3];
	    			
	    			for(String pair : keyValuePairs)                        // iterate over the pairs.
	    			{
	    			    String[] entry = pair.split(":");                   // split the pairs to get key and value. 
	    			    
	    			    switch (entry[0])
	    			    {
	    			    	case "bat": drone.setBattery(Integer.parseInt(entry[1].trim())); break;
	    			    	case "time": drone.setTime(Integer.parseInt(entry[1].trim())); break;
	    			    	case "temph": drone.setTemp(Integer.parseInt(entry[1].trim())); break;
	    			    	case "tof": drone.setTof(Integer.parseInt(entry[1].trim())); break;
	    			    	case "h": drone.setHeight(Integer.parseInt(entry[1].trim())); break;
	    			    	case "baro": drone.setBarometer(Double.parseDouble(entry[1].trim())); break;
	    			    	case "pitch": attpry[0] = Integer.parseInt(entry[1].trim()); break;
	    			    	case "roll": attpry[1] = Integer.parseInt(entry[1].trim()); break;
	    			    	case "yaw": attpry[2] = Integer.parseInt(entry[1].trim()); break;
	    			    	case "agx": accelxyz[0] = Double.parseDouble(entry[1].trim()); break;
	    			    	case "agy": accelxyz[1] = Double.parseDouble(entry[1].trim()); break;
	    			    	case "agz": accelxyz[2] = Double.parseDouble(entry[1].trim()); break;
	    			    	case "vgx": veloxyz[0] = Double.parseDouble(entry[1].trim()); break;
	    			    	case "vgy": veloxyz[1] = Double.parseDouble(entry[1].trim()); break;
	    			    	case "vgz": veloxyz[2] = Double.parseDouble(entry[1].trim()); break;

	    			    	// Record mission pad id number and x,y,z values read from the pad.
	    			    	case "mid": drone.setMissionPadId(Integer.parseInt(entry[1].trim())); break;
	    			    	case "x": mpxyz[0] = Integer.parseInt(entry[1].trim()); break;
	    			    	case "y": mpxyz[1] = Integer.parseInt(entry[1].trim()); break;
	    			    	case "z": mpxyz[2] = Integer.parseInt(entry[1].trim()); break;
	    			    	
	    			    	case "mpry":
	    			    		String[] entry2 = entry[1].split(",");
	    			    		mppry[0] = Integer.parseInt(entry2[0].trim());
	    			    		mppry[1] = Integer.parseInt(entry2[1].trim());
	    			    		mppry[2] = Integer.parseInt(entry2[2].trim());
	    			    		break;
	    			    }
	    			}
    			    
	    			// If we are flying and height goes to zero and status is still coming
	    			// it probably means drone has crashed. This condition must persist 
	    			// for more than 1 second to avoid spurious height == 0 reports.
	    			
	    			if (drone.isFlying() && drone.getHeight() <= 0) 
	    			{
	    				lastDetectionTime = System.currentTimeMillis();
	    				crashDetected = true;
	    			} else crashDetected = false;
	    			
	    			if (crashDetected && System.currentTimeMillis() - lastDetectionTime > 1000)
	    				throw new Exception("crash detected");
	    			
	    			// Record status information in Drone class.
	    			
    			    drone.setAttitude(attpry);
    			    
    			    drone.setAcceleration(accelxyz);
    			    
    			    drone.setVelocity(veloxyz);
    			    
    			    drone.setMissionPadxyz(mpxyz);
    			    
    			    drone.setMissionPadpry(mppry);
	    		}
	    	}
	    	catch (Exception e) 
	    	{ 
	    		logger.severe("status monitor failed: " + e.getMessage()); 
	    		// Error on status monitor most likely means drone has shut down.
	    		drone.setConnection(TelloConnection.DISCONNECTED);
	    	}
	    	finally {}
    		
    		logger.fine("status monitor thread ended");
	    	
	    	statusMonitorThread =  null;
	    }
	}
	
	@Override
	public void startKeepAlive()
	{
		logger.fine("starting keepalive thread");
		
		if (keepAliveThread != null) return;

		keepAliveThread = new KeepAlive();
		keepAliveThread.start();
	}

	@Override
	public void stopKeepAlive()
	{
		if (keepAliveThread != null)
		{
			logger.fine("stopping keepalive thread");
			
			try
			{
				// Signal thread to stop.
				keepAliveThread.interrupt();
				// Wait for thread to stop.
				keepAliveThread.join();
			} catch (Exception e) {e.printStackTrace();}
		}
		
		keepAliveThread = null;
	}
	
	private class KeepAlive extends Thread
	{
		KeepAlive()
		{
			logger.fine("KeepAlive thread constructor");
			
			this.setName("KeepAlive");
	    }
		
	    public void run()
	    {
			logger.fine("keepalive thread start");
			
	    	try
	    	{
	    		while (!isInterrupted())
	    		{
	    			sleep(5000);	// 5 seconds.
	    			
	    			getBattery();
	    		}
	    	}
	    	catch (InterruptedException e) {}
	    	catch (Exception e) 
	    	{ 
	    		logger.severe("keepalive failed: " + e.getMessage()); 
	    		// Error on status monitor most likely means drone has shut down.
	    		drone.setConnection(TelloConnection.DISCONNECTED);	    		
	    	}
    		
    		logger.fine("keep alive thread ended");
	    	
	    	keepAliveThread =  null;
	    }
	}

	@Override
	public void setMissionMode( boolean enabled, MissionDetectionCamera camera )
	{
		if (enabled)
		{
			TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.MON);
			communication.executeCommand(command);

			command = new ComplexTelloCommand(TelloCommandValues.MDIRECTION, MissionDetectionCamera.toCommand(camera));
			communication.executeCommand(command);

			drone.setMissionMode(true);
		}
		else
		{
			TelloCommandInterface command = new BasicTelloCommand(TelloCommandValues.MOFF);
			communication.executeCommand(command);
			
			drone.setMissionMode(false);
		}
	}

	@Override
	public void setStationMode( String ssid, String password )
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.STATION_MODE, ssid + " " + password);
		communication.executeCommand(command);
	}

	@Override
	public void curve( Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2, Integer speed )
	{
		TelloCommandInterface command = new ComplexTelloCommand(TelloCommandValues.CURVE, 
				x1.toString() + " " + y1.toString() + " " + z1.toString() + " " + 
				x2.toString() + " " + y2.toString() + " " + z2.toString() + " " + speed.toString());
		
		communication.executeCommand(command);
	}
}
