package tellolib.control;

import java.util.ArrayList;
import java.util.logging.Level;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

import tellolib.camera.MissionDetectionCamera;
import tellolib.command.TelloFlip;
import tellolib.communication.TelloConnection;
import tellolib.drone.TelloDroneInterface;

/**
 * Higher level interface to Tello Drone library.
 */
public interface TelloControlInterface 
{
  /**
   * set logging level. Defaults to OFF.
   * @param logLevel Desired log level.
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
   * @param x X axis offset (20-500 cm).
   * @param y Y axis offset (20-500 cm).
   * @param z Z axis offset (20-500 cm).
   * @param speed Speed of movement (10-100 cm/s).
   */
  void goTo(Integer x, Integer y, Integer z, Integer speed);
  
  /**
   * Fly by remote control.
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
   * Requires SDK 1.3 or later.
   */
  void startStatusMonitor();
  
  /**
   * Stop monitoring the status updates sent by the Tello.
   * Requires SDK 1.3 or later. Monitor will be stopped when
   * disconnect() is called.
   */
  void stopStatusMonitor();
  
  /**
   * Start keep alive thread that pings the Tello every 10 seconds
   * with a get battery level command to keep the Tello from shutting
   * down automatically if it receives no commands for 15 seconds.
   */
  void startKeepAlive();
  
  /**
   * Stops the keep alive thread. Thread will be stopped when disconnect()
   * is called.
   */
  void stopKeepAlive();
  
  /**
   * Return the TelloDrone instance maintained by TelloControl.
   * @return The drone instance.
   */
  //TelloDroneInterface getDrone();
  
  /**
   * Start capture of video stream from drone for processing by
   * this program. This function may take several seconds to complete.
   * @param liveWindow True to display video feed in a live window.
   */
  //void startVideoCapture(boolean liveWindow);
  
  /**
   * Stop video stream capture thread.
   */
  //void stopVideoCapture();
  
  /**
   * Save the current image from the video feed to a file in
   * the named folder.
   * @param folder Location to save image.
   * @return True if image saved, false if failed.
   */
  //boolean takePicture(String folder);
  
  /**
   * Turn on recording of the video feed to an .avi file in
   * the named folder.
   * @param folder Location to save the video file.
   * @return True if recording started, false if failed.
   */
  //boolean startRecording(String folder);
  
  /**
   * Stop recording the video feed.
   */
  //void stopRecording();
  
  /**
   * Returns recording state.
   * @return True if recording in progress, false if not.
   */
  //boolean isRecording();

  /**
   * Returns the current image from the video feed.
   * @return The current image.
   */
  //Mat getImage();
	
  /**
   * Add a target rectangle to be drawn on the camera feed images.
   * Width of lines defaults to 1 pixel, color defaults to 0,0,255 (red).
   * @param rectangle Rectangle to draw, null to clear all rectangles.
   */
  //void addTarget(Rect target);
	
  /**
   * Add a target rectangle to be drawn on the camera feed images.
   * @param rectangle Rectangle to draw, null to clear all rectangles.
   * @param width Pixel width of rectangle lines.
   * @param color Set the line color used to draw rectangles. B,G,R color values. 
   */
  //void addTarget(Rect target, int width, Scalar color);

  /**
   * Set mission mode state. In mission mode, status monitoring
   * will record information about any mission pad detected by the
   * selected camera(s).
   * @param enabled True to enable, false to disable.
   * @param camera Select which camera(s) used for detection when enabled.
   */
  void setMissionMode(boolean enabled, MissionDetectionCamera camera);
  
  /**
   * Returns mission mode state.
   * @return True if enabled, false if disabled.
   */
  //boolean isMissionModeEnabled();

	/**
	 * Get last recorded mission pad id detected.
	 * Only valid when status monitoring enabled.
	 * Only valid when in mission mode and a mission pad is detected.
	 * @return Mission pad id 1-8 or negative if no pad detected.
	 */
	//int getMissionPadId();

  	/**
	 * Get last drone mission pad x y z values.
	 * Only valid when status monitoring enabled.
  	 * Only valid when mission pad detected.
	 * @return Mission pad pitch, roll, yaw values.
	 */
  	//int[] getMissionPadxyz();

  	/**
  	 * Get last drone mission pad pitch roll yaw.
	 * Only valid when status monitoring enabled.
  	 * Only valid when mission pad detected.
  	 * @return Mission pad pitch, roll, yaw values.
  	 */
  	//int[] getMissionPadpry();
 	
  	/**
  	 * Return last recorded raw yaw value. Raw yaw starts at zero when
  	 * drone turned on with whatever direction drone is pointing
  	 * as zero. This direction is maintained as zero as long as
  	 * power stays on. Raw yaw is yaw relative to that zero point.
 	 * Only valid when status monitoring enabled.
 	 * @return Yaw value. Right of zero is 1 to 179, left of zero
  	 * is -1 to -179 and if you pass 179 the sign reverses.
  	 */
  	//int getRawYaw();
  	
  	/**
  	 * Return yaw from direction drone was pointing at power on or
  	 * at last call to resetYawZero().
	 * Only valid when status monitoring enabled.
  	 * @return Yaw value in degrees. Right of zero is 1 to 179, left of zero
  	 * is -1 to -179.
  	 */
  	//int getYaw();
  	
  	/**
  	 * Get drone heading based on last recorded yaw value.
  	 * Resets to heading 0 matching the direction the drone 
  	 * is pointing when the program is restarted.
	 * Only valid when status monitoring enabled.
  	 * @return Heading 0-359.
  	 */
  	//public int getHeading();
  	
  	/**
  	 * Reset drone heading so 0 matches the direction the
  	 * drone is current pointing.
  	 */
  	//public void resetHeadingZero();

  	/**
  	 * Reset yaw tracking to current direction as zero.
	 * Only valid when status monitoring enabled.
  	 */
  	//public void resetYawZero();
  	
	/**
	 * Perform Aruco marker detection on the current camera image.
	 * Only valid if video feed is in progress.
	 * @return True if marker(s) detected, false if not.
	 */
  	//public boolean detectArucoMarkers();
  	
	/**
	 * Get the number of markers detected on last call to 
	 * detectArucoMarkers().
	 * Only valid if video feed is in progress.
	 * @return Number of detected markers.
	 */
  	//public int getArucoMarkerCount();
  	
	/**
	 * Get the marker id of the selected marker detected by the
	 * last call to detectArucoMarkers().
	 * Only valid if video feed is in progress.
	 * @param index Marker to select indexed from 0.
	 * @return Marker id number or -1 if no markers or index out of range.
	 */
  	//public int getArucoMarkerId(int index);
	
	/**
	 * Get detected markers as rectangles located within the image used in
	 * last call to detectMarkers() as x,y,h,w.
	 * @return List of target rectangles or null if no markers found.
	 */
	//public ArrayList<Rect> getArucoMarkerTargets();
	
	/**
	 * Get the contours of detected markers as an array points for each
	 * marker from the last call to detectMarkers().
	 * @return Array of vector of 4 corner points for each marker. Null if
	 * no markers found.
	 */
	//public ArrayList<MatOfPoint> getArucoMarkerContours();
	
	/**
	 * Set an array of MatOfPoint vectors, one vector for each contour to be
	 * drawn on the camera feed images. Each vector can contains as many points
	 * as needed to draw the contour.
	 * @param contours Array of MatOfPoint objects, one for each contour, null to
	 * clear all contours.
	 */
	//public void setContours(ArrayList<MatOfPoint> contours);
	
	/**
	 * Set an array of MatOfPoint vectors, one vector for each contour to be
	 * drawn on the camera feed images. Each vector can contains as many points
	 * as needed to draw the contour.
	 * @param contours Array of MatOfPoint objects, one for each contour, null to
	 * clear all contours.
	 * @param width Pixel width of rectangle lines.
	 * @param color Set the line color used to draw rectangles. B,G,R color values. 
	 */
	//public void setContours(ArrayList<MatOfPoint> contours, int width, Scalar color);
}