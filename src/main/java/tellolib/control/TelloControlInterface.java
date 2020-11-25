package tellolib.control;

import java.util.logging.Level;

import tellolib.camera.MissionDetectionCamera;
import tellolib.command.TelloFlip;
import tellolib.communication.TelloConnection;

/**
 * Higher level interface to Tello Drone library.
 */
public interface TelloControlInterface 
{
  /**
   * set logging level. Defaults to OFF.
   * @param logLevel Desired log level.
   * Level.SEVERE: a serious failure, which prevents normal execution of the program, for end users and system administrators.
   * Level.WARNING: a potential problem, for end users and system administrators.
   * Level.INFO: reasonably significant informational message for end users and system administrators.
   * Level.CONFIG: hardware configuration, such as CPU type.
   * Level.FINE, Level.FINER, Level.FINEST: three levels used for providing tracing information for the software developers.
   * Level.OFF: turn off logging.
   * Level.ALL: turn on all levels of logging.
   */
  void setLogLevel(Level logLevel);
  
  /**
   * Establishes connection to the Tello Drone.
   */
  void connect();

  /**
   * Disconnecting from the drone. If the drone is not landed yet, it will start an automatic
   * landing.
   */
  void disconnect();


  /**
   * Enter command mode. You can only execute commands after this call.
   * Will determine and record drone model.
   */
  void enterCommandMode();

  /**
   * Taking off from the ground.
   */
  void takeOff();

  /**
   * Landing on the ground.
   */
  void land();

  /**
   * Doing a flip in the chosen direction.
   *
   * @param telloFlip Type of the flip.
   */
  void doFlip(TelloFlip telloFlip);

  /**
   * Set the drone's speed.
   *
   * @param speed Chosen speed (10-100 cm/s).
   */
  void setSpeed(Integer speed);

  /**
   * Move forward.
   * @param distance (20-500 cm).
   */
  void forward(Integer distance);

  /**
   * Move backward.
   * @param distance (20-500 cm).
   */
  void backward(Integer distance);

  /**
   * Move right.
   * @param distance (20-500 cm).
   */

  void right(Integer distance);

  /**
   * Move left.
   * @param distance (20-500 cm).
   */

  void left(Integer distance);

  /**
   * Move up.
   * @param distance (20-500 cm).
   */

  void up(Integer distance);

  /**
   * Move down.
   * @param distance (20-500 cm).
   */

  void down(Integer distance);

  /**
   * rotate right.
   * @param angle (0-3600 deg).
   */
  void rotateRight(Integer angle);

  /**
   * rotate left.
   * @param angle (0-3600 deg).
   */
  void rotateLeft(Integer angle);
  
  /**
   * Fly to these offsets from current position.
   * @param x X axis offset (-500 to 500 cm).
   * @param y Y axis offset (-500 to 500 cm).
   * @param z Z axis offset (-500 to 500 cm).
   * @param speed Speed of movement (10 to 100 cm/s).
   */
  void goTo(Integer x, Integer y, Integer z, Integer speed);
  
  /**
   * Fly at a curve starting at drone current position according to the two given offset coordinates at speed (cm/s). 
   * The first coordinate is the intermediate point on the curve and second is the end point of the curve.
   * If the arc radius is not within a range of 0.5 to 10 meters, it will respond with an error. 
   * x, y and z values (cm) = -500 to +500, speed = 10 to 60. 
   * Note: x, y, and z values can't be set between -20 and +20 simultaneously.
   */
  void curve(Integer x1, Integer y1, Integer z1, Integer x2, Integer y2, Integer z2, Integer speed);
  
  /**
   * Fly by remote control. Units are speed in cm/s.
   * @param lr Left/Right (-100 to 100).
   * @param fb forward/backward (-100 to 100).
   * @param ud up/down (-100 to 100).
   * @param yaw yaw value in degrees.
   */
  void flyRC(Integer lr, Integer fb, Integer ud, Integer yaw);

  /**
   * Get current battery level.
   * @return Battery level %.
   */
  int getBattery();
  
  /**
   * Get current speed setting.
   * @return Speed (1-100 cm/s).
   */
  int getSpeed();
  
  /**
   * Get flight time.
   * @return Flight time in seconds.
   */
  int getTime();
  
  /**
   * Get drone height.
   * @return Height (0-3000 cm).
   */
  int getHeight();
  
  /**
   * Get drone temperature.
   * @return Temperature in degrees C (0-90).
   */
  int getTemp();
  
  /**
   * Get barometric pressure.
   * @return Pressure in millibars.	
   */
  double getBarometer();
  
  /**
   * Get IMU attitude data.
   * Only valid when status monitoring enabled.
   * @return Pitch, roll, yaw.
   */
  int[] getAttitude();
  
  /**
   * Get IMU acceleration.
   * Only valid when status monitoring enabled.
   * @return Angular acceleration x, y, z (.001 g).
   */
  double[] getAcceleration();
  
  /**
   * Get distance from TOF.
   * @return Distance (30-1000 cm).
   */
  double getTof();
  
  /**
   * Get drone serial number. Requires EDU with SDK 1.3 or later.
   * @return Serial Number.
   */
  String getSN();

  /**
   * Get drone connection status.
   * @return Connection status.
   */
  TelloConnection getConnection();
  
  /**
   * Get drone sdk version. Requires EDU with SDK 1.3 or later.
   * @return Version.
   */
  String getSDK();
  
  /**
   * Stop drone motion, goes into hover.
   */
  void stop();
  
  /**
   * Stop all motors.
   */
  void emergency();
  
  /**
   * Turn on video stream.
   */
  void streamOn();
  
  /**
   * Turn off video stream.
   */
  void streamOff();
  
  /**
   * Start monitoring the status updates sent by the Tello.
   * Requires SDK 1.3 or later. Monitor will set drone connection 
   * state to disconnected if it detects loss of status updates.
   * Monitor will also watch status data for crash indication
   * and set drone connection state to disconnected.
   */
  void startStatusMonitor();
  
  /**
   * Stop monitoring the status updates sent by the Tello.
   * Requires SDK 1.3 or later. Monitor will also be stopped when
   * {@link #disconnect()} is called.
   */
  void stopStatusMonitor();
  
  /**
   * Start keep alive thread that pings the Tello every 10 seconds
   * with a get battery level command to keep the Tello from shutting
   * down automatically if it receives no commands for 15 seconds.
   * Thread will set drone connection state to disconnected if 
   * it detects an error on ping of drone.
   */
  void startKeepAlive();
  
  /**
   * Stops the keep alive thread. Thread will also be stopped when {@link #disconnect()}
   * is called.
   */
  void stopKeepAlive();
  
  /**
   * Set mission mode state. In mission mode, status monitoring
   * will record information about any mission pad detected by the
   * selected camera(s).
   * @param enabled True to enable, false to disable.
   * @param camera Select which camera(s) used for detection when enabled.
   */
  void setMissionMode(boolean enabled, MissionDetectionCamera camera);
  
  /**
   * Set drone into 'station' mode. Turns off access point and connects to
   * WIFI access point.
   * @param ssid Name of the access point.
   * @param password Password for the access point.
   */
  void setStationMode(String ssid, String password);
}