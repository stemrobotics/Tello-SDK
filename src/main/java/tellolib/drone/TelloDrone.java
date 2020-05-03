package tellolib.drone;

import tellolib.communication.TelloConnection;

/**
 * TelloDrone class holds information about the drone,
 * set by retrieving information from the drone or other sources.
 */
public class TelloDrone implements TelloDroneInterface
{
  /*
   * Connection IP address.
   */
  public static final String IP_ADDRESS = "192.168.10.1";

  /*
   * Connection UDP Port.
   */
  public static final Integer UDP_PORT = 8889, UDP_STATUS_PORT = 8890, UDP_VIDEO_PORT = 11111;
  
  private int				battery, height, speed, time, temp, attitude[];
  private int				missionPadId, missionPadxyz[], missionPadpry[];
  private int				heading, headingZeroOffset = 9999, yawZeroOffset = 9999;
  private double			barometer, tof, acceleration[], velocity[];
  private String			sn, sdk;
  private TelloConnection 	telloConnection;
  private TelloMode 		telloMode;
  private boolean			missionModeEnabled, flying;
  private TelloModel		telloModel = TelloModel.EDU;

  // Private constructor, holder class and getInstance() implement this
  // class as a singleton.
	
  private TelloDrone() 
  {
	  telloConnection = TelloConnection.DISCONNECTED;
	  telloMode = TelloMode.NORMAL;
  }
  
  private static class SingletonHolder 
  {
	  public static final TelloDrone INSTANCE = new TelloDrone();
  }
	
  /**
   * Get the global instance of TelloDrone.
   * @return Global TelloDrone instance.
   */
  public static TelloDrone getInstance()
  {
	  return SingletonHolder.INSTANCE;
  }

  @Override
  public Integer getBattery() 
  {
	  return battery;
  }

  @Override
  public void setBattery(Integer battery) 
  {
	  this.battery = battery;
  }

  @Override
  public Integer getSpeed() 
  {
	  return speed;
  }

  @Override
  public void setSpeed(Integer speed) 
  {
	  this.speed = speed;
  }

  @Override
  public Integer getTime() 
  {
	  return time;
  }

  @Override
  public void setTime(Integer time) 
  {
	  this.time = time;
  }

  @Override
  public TelloConnection getConnection() 
  {
	  return telloConnection;
  }

  @Override
  public void setConnection(TelloConnection telloConnection) 
  {
	  this.telloConnection = telloConnection;
  }

  @Override
  public TelloMode getMode() 
  {
	  return telloMode;
  }

  @Override
  public void setMode(TelloMode telloMode) 
  {
	  this.telloMode = telloMode;
  }

  @Override
  public void setTemp( int temp )
  {
	  this.temp = temp;
  }

  @Override
  public int getTemp()
  {
	  return temp;
  }

  @Override
  public void setBarometer( double barometer )
  {
	  this.barometer = barometer;
  }

  @Override
  public double getBarometer()
  {
	  return barometer;
  }

  @Override
  public void setTof( double tof )
  {
	  this.tof = tof;
  }

  @Override
  public double getTof()
  {
	  return tof;
  }

  @Override
  public void setSN( String sn )
  {
	  this.sn = sn;
  }

  @Override
  public String getSN()
  {
	  return sn;
  }

  @Override
  public void setHeight( int height )
  {
	  this.height = height;
  }

  @Override
  public int getHeight()
  {
	  return height;
  }

  @Override
  public void setAttitude( int[] pry )
  {
	  attitude = pry;
	
	  updateHeading();
  }

  @Override
  public int[] getAttitude()
  {
	  return attitude;
  }

  @Override
  public void setAcceleration( double[] xyz )
  {
	  acceleration = xyz;
  }

  @Override
  public double[] getAcceleration()
  {
	  return acceleration;
  }

  @Override
  public void setSDK( String sdk )
  {
	  this.sdk = sdk;
  }

  @Override
  public String getSDK()
  {
	  return sdk;
  }

  @Override
  public void setVelocity( double[] xyz )
  {
	  this.velocity = xyz;
  }

  @Override
  public double[] getVelocity()
  {
	  return velocity;
  }

  @Override
  public void setMissionMode( boolean enabled )
  {
	  missionModeEnabled = enabled;
  }

  @Override
  public boolean isMissionModeEnabled()
  {
	  return missionModeEnabled;
  }

  @Override
  public void setMissionPadId( int id )
  {
	  missionPadId = id;
  }

  @Override
  public int getMissionPadId()
  {
	  return missionPadId;
  }

  @Override
  public void setMissionPadxyz( int[] xyz )
  {
	  missionPadxyz = xyz;
  }

  @Override
  public int[] getMissionPadxyz()
  {
	  return missionPadxyz;
  }

  @Override
  public void setMissionPadpry( int[] pry )
  {
	  missionPadpry = pry;	
  }

  @Override
  public int[] getMissionPadpry()
  {
	  return missionPadpry;
  }

  @Override
  public int getRawYaw()
  {
	  return getAttitude()[2];
  }
  
  private void updateHeading()
  {
	  int yaw = getRawYaw();
	  
	  if (headingZeroOffset == 9999) 
	  {
		  resetHeadingZero();
		  resetYawZero();
	  }

	  yaw -= headingZeroOffset;
	  
	  if (yaw < 0)
		  heading = 360 + yaw;
	  else
		  heading = yaw;
  }
  
  @Override
  public int getHeading()
  {
	  return heading;
  }
  
  @Override
  public void resetHeadingZero()
  {
	  headingZeroOffset = getRawYaw();
  }

  @Override
  public int getYaw()
  {
	  return getRawYaw() - yawZeroOffset;
  }

  @Override
  public void resetYawZero()
  {
	  yawZeroOffset = getRawYaw();
  }

  @Override
  public void setModel( TelloModel model )
  {
	  telloModel = model;
  }

  @Override
  public TelloModel getModel()
  {
	  return telloModel;
  }

  @Override
  public void setFlying( boolean flying )
  {
	  this.flying = flying;
  }

  @Override
  public boolean isFlying()
  {
	  return flying;
  }

  @Override
  public boolean isConnected()
  {
	  if (telloConnection == TelloConnection.CONNECTED)
		  return true;
	  else
		  return false;
  }
}