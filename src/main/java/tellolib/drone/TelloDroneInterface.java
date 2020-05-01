package tellolib.drone;

import tellolib.communication.TelloConnection;

/**
 * Holds information about the Tello Drone.
 */
public interface TelloDroneInterface 
{
	/** 
	 * Return last recorded battery level
	 * @return Battery level 0-100%.
	 */
	Integer getBattery();

	/**
	 * Record battery level.
	 * @param battery Battery level to record 0-100%.
	 */
	void setBattery(Integer battery);

	/**
	 * Return last set speed value.
	 * @return Speed 0-100 cm/s.
	 */
	Integer getSpeed();

	/**
	 * Set flight speed.
	 * @param speed 10-100 cm/s.
	 */
	void setSpeed(Integer speed);

	/**
	 * Get current flight time.
	 * @return Flight time in seconds.
	 */
	Integer getTime();

	/**
	 * Set flight time.
	 * @param time Flight time in seconds.
	 */
	void setTime(Integer time);

	/**
	 * Get connection status.
	 * @return Connection status enum.
	 */
	TelloConnection getConnection();

	/**
	 * Record connection status.
	 * @param telloConnection The connection status value.
	 */
	void setConnection(TelloConnection telloConnection);

	/**
	 * Get last recorded operation mode.
	 * @return The current operation mode.
	 */
	TelloMode getMode();

	/**
	 * Record drone operation mode.
	 * @param telloMode Current operation mode value.
	 */
	void setMode(TelloMode telloMode);

	/**
	 * Record the current temperature.
	 * @param temp Temperature in degrees C.
	 */
	void setTemp( int temp );

	/**
	 * Get last recorded temperature.
	 * @return Temperature in degrees C.
	 */
	int getTemp();

	/**
	 * Record barometric pressure.
	 * @param pressure Pressure in millibars.
	 */
	void setBarometer( double pressure );

	/**
	 * Get the last recorded barometric pressure.
	 * @return Pressure in millibars.
	 */
	double getBarometer();

	/**
	 * Set TOF value.
	 * @param tof Value to set.
	 */
	void setTof( double tof );

	/**
	 * Get the last recorded TOF value.
	 * @return TOF value.
	 */
  	double getTof();

  	/**
  	 * Record drone serial number.
  	 * @param sn Serial number.
  	 */
  	void setSN( String sn );

	/**
	 * Get the last recorded serial number.
	 * @return Serial number.
	 */
  	String getSN();

  	/**
  	 * Record drone height.
  	 * @param height Height in cm.
  	 */
  	void setHeight( int height );

	/**
	 * Get the last recorded height.
	 * @return Height in cm..
	 */
  	int getHeight();

  	/**
  	 * Record drone attitude. Also updates drone heading.
  	 * @param pry Attitude vectors pitch, roll, yaw in degrees.
  	 */
  	void setAttitude( int[] pry );

	/**
	 * Get the last recorded attitude vectors.
	 * Only valid when status monitoring enabled.
	 * @return Attitude vectors pitch, roll, yaw in degrees.
	 */
  	int[] getAttitude();

  	/**
  	 * Record drone acceleration vectors.
  	 * @param xyz Acceleration vectors x,y,z in cm/s/s.
  	 */
  	void setAcceleration( double[] xyz );

	/**
	 * Get the last recorded acceleration vectors.
	 * Only valid when status monitoring enabled.
	 * @return Acceleration vectors x,y,z in cm/s/s.
	 */
  	double[] getAcceleration();

  	/**
  	 * Record drone velocity vectors.
	 * Only valid when status monitoring enabled.
  	 * @param xyz Velocity vectors x,y,z in cm/s.
  	 */
  	void setVelocity( double[] xyz );

	/**
	 * Get the last recorded velocity vectors.
	 * Only valid when status monitoring enabled.
	 * @return Velocity vectors x,y,z in cm/s.
	 */
  	double[] getVelocity();

  	/**
  	 * Record the drone sdk version.
  	 * @param version SDK version.
  	 */
  	void setSDK( String version );

	/**
	 * Get the last recorded SDK version.
	 * @return The SDK version.
	 */
  	String getSDK();
  	
  	/**
  	 * Record mission mode state. In mission mode, status monitoring
  	 * will record information about any mission pad detected by the
  	 * selected camera(s).
  	 * @param enabled True if mission mode enabled, false if not.
  	 */
  	void setMissionMode(boolean enabled);
  	
  	/**
  	 * Get the last recorded mission mode state.
  	 * @return True if mission mode is enabled, false if not.
  	 */
  	boolean isMissionModeEnabled();
  	
  	/**
  	 * Record last detected mission pad id.
  	 * @param id Mission pad id 1-8 or negative if no pad detected.
  	 */
  	void setMissionPadId(int id);

  	/**
  	 * Get last recorded mission pad id detected.
	 * Only valid when status monitoring enabled.
  	 * Only valid when in mission mode and a mission pad is detected.
  	 * @return Mission pad id 1-8 or negative if no pad detected.
  	 */
  	int getMissionPadId();

  	/**
  	 * Record drone mission pad x y z.
  	 * @param xyz Mission pad x y z values.
  	 */
  	void setMissionPadxyz( int[] xyz );

  	/**
	 * Get last drone mission pad x y z values.
	 * Only valid when status monitoring enabled.
  	 * Only valid when mission pad detected.
	 * @return Mission pad pitch, roll, yaw values.
	 */
  	int[] getMissionPadxyz();

  	/**
  	 * Record drone mission pad pitch roll yaw.
  	 * @param pry Mission pad pitch, roll, yaw values.
  	 */
  	void setMissionPadpry( int[] pry );

  	/**
  	 * Get last drone mission pad pitch roll yaw.
	 * Only valid when status monitoring enabled.
  	 * Only valid when mission pad detected.
  	 * @return Mission pad pitch, roll, yaw values.
  	 */
  	int[] getMissionPadpry();
  	
  	/**
  	 * Return last recorded raw yaw value. raw yaw starts at zero when
  	 * drone turned on with whatever direction drone is pointing
  	 * as zero. This direction is maintained as zero as long as
  	 * power stays on. Raw yaw is yaw relative to that zero point.
	 * Only valid when status monitoring enabled.
  	 * @return Yaw value in degrees. Right of zero is 1 to 179, left of zero
  	 * is -1 to -179 and if you pass 179 the sign reverses.
  	 */
  	int getRawYaw();
  	
  	/**
  	 * Return yaw from direction drone was pointing at power on or
  	 * at last call to resetYawZero().
	 * Only valid when status monitoring enabled.
  	 * @return Yaw value in degrees. Right of zero is 1 to 179, left of zero
  	 * is -1 to -179.
  	 */
  	int getYaw();
  	
  	/**
  	 * Get drone heading based on last recorded yaw value.
  	 * Resets to heading 0 matching the direction the drone 
  	 * is pointing when the program is restarted.
	 * Only valid when status monitoring enabled.
  	 * @return Heading 0-359 in degrees.
  	 */
  	public int getHeading();
  	
  	/**
  	 * Reset drone heading so 0 matches the direction the
  	 * drone is current pointing.
  	 */
  	public void resetHeadingZero();

  	/**
  	 * Reset yaw tracking to current direction as zero.
	 * Only valid when status monitoring enabled.
  	 */
  	public void resetYawZero();
  	
  	/**
  	 * Set the drone model.
  	 * @param model Drone model (basic/EDU).
  	 */
  	public void setModel(TelloModel model);
  	
  	/**
  	 * Get the drone model setting.
  	 * @return The drone model value.
  	 */
  	public TelloModel getModel();
  	
  	/**
  	 * Set the flying status of the drone. Set true after successful
  	 * take off and false after landing.
  	 * @param flying True if drone is flying, false if not.
  	 */
  	public void setFlying(boolean flying);
  	
  	/**
  	 * Return flying status.
  	 * @return True if flying, false if not.
  	 */
  	public boolean isFlying();
  	
  	/**
  	 * Return drone connection status.
  	 * @return True if connected, false if not.
  	 */
  	public boolean isConnected();
}
