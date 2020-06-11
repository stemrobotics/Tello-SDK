package tellolib.command;

/**
 * Commands for the DJI Tello drone. Source: https://github.com/grofattila/dji-tello/blob/experimental/Tello_SDK.pdf
 * and: https://dl-cdn.ryzerobotics.com/downloads/Tello/Tello SDK 2.0 User Guide.pdf
 */
public class TelloCommandValues 
{
  /**
   * Enter command mode.
   */
  public static final String COMMAND_MODE = "command";

  /**
   * Auto take off.
   */
  public static final String TAKE_OFF = "takeoff";

  /**
   * Auto landing.
   */
  public static final String LAND = "land";

  /**
   * Enable video stream.
   */
  public static final String ENABLE_VIDEO_STREAM = "streamon";

  /**
   * Disable video stream.
   */
  public static final String DISABLE_VIDEO_STREAM = "streamoff";

  /**
   * Stops motors immediately.
   */
  public static final String EMERGENCY = "emergency";

  /**
   * Fly upward xx (xx = 20 - 500 cm).
   */
  public static final String UP = "up";

  /**
   * Fly downward xx (xx = 20 - 500 cm).
   */
  public static final String DOWN = "down";

  /**
   * Fly left xx (xx = 20 - 500 cm).
   */
  public static final String LEFT = "left";

  /**
   * Fly right xx (xx = 20 - 500 cm).
   */
  public static final String RIGHT = "right";

  /**
   * Fly forward xx (xx = 20 - 500 cm).
   */
  public static final String FORWARD = "forward";

  /**
   * Fly backward xx (xx = 20 - 500 cm).
   */
  public static final String BACK = "back";

  /**
   * Rotate clockwise xx (xx = 1-3600°).
   */
  public static final String CW = "cw";

  /**
   * Rotate counter-clockwise xx (xx = 1-3600°).
   */
  public static final String CCW = "ccw";

  /**
   * Flip x (l = left, r = right, f = forward, b = back, bl = back/left, rb = back/right), fl =
   * front/left, fr = front/right ).
   */
  public static final String FLIP = "flip";


  /**
   * Fly to x y z offset (cm) from current position at speed (cm/s). go x y z speed.
   * x = -500 to +500 y = -500 to +500 z = -500 to +500 speed = 10 to 100. Note: x, y, and z values
   * can't be set between -20 and +20 simultaneously. go x y z speed.
   * 
   * Also 
   * Fly to x y z of the mission pad at speed (cm/s). go x y z speed mid. 
   * x = -500 to +500 y = -500 to +500 z = -500 to +500 speed = 10 to 100. Note: x, y, and z values 
   * can't be set between -20 and +20 simultaneously. mid = m1-m8.
   */
  public static final String GO = "go";
  
  /**
   * Fly to x y z of mission pad 1 and recognize coordinates 0 0 z of mission pad 2 then rotate to 
   * the yaw value. jump x y z speed yaw mid1 mid2.
   * x = -500 to +500 y = -500 to +500 z = -500 to +500 speed = 10 to 100. Note: x, y, and z values can't be set 
   * between -20 and +20 simultaneously. mid1/2 = m1-m8.
   * 
   */
  public static final String JUMP = "jump";

  /**
   * Fly at a curve from current position according to the two given offset coordinates at speed (cm/s). 
   * curve x1 y1 z1 x2 y2 z2 speed. 
   * If the arc radius is not within a range of 0.5 to 10 meters, it will respond with an error. 
   * x, y, z = -500 to +500 (cm), speed = 10 to 60. Note: x, y, and z values 
   * can't be set between -20 and +20 simultaneously.
   * 
   * Also
   * Fly at a curve according to the two given coordinates of the mission pad at speed (cm/s). 
   * curve x1 y1 z1 x2 y2 z2 speed mid.
   * If the arc radius is not within a range of 0.5 to 10 meters, it will respond with an error. 
   * x, y, z = -500 to +500 (cm), speed = 10 to 60. Note: x, y, and z 
   * values can't be set between -20 and +20 simultaneously. mid = m1-m8
   */
  public static final String CURVE = "curve";

  /**
   * Hovers in the air. Works any time.
   */
  public static final String STOP = "stop";

  /**
   * Set current speed as xx (xx = 1-100 cm/s ).
   */
  public static final String SPEED = "speed";

  /**
   * Obtain current speed (cm/s).
   */
  public static final String CURRENT_SPEED = "speed?";

  /**
   * Obtain current battery percentage.
   */
  public static final String CURRENT_BATTERY = "battery?";

  /**
   * Obtain current flight time in seconds.
   */
  public static final String CURRENT_FLY_TIME = "time?";

  /**
   * Obtain the Tello SDK version. Requires EDU with SDK 1.3 or later.
   */
  public static final String SDK = "sdk?";

  /**
   * Obtain the Tello serial number. Requires EDU with SDK 1.3 or later.
   */
  public static final String SN = "sn?";

  /**
   * Obtain the Tello barometric pressure in millibars.
   */
  public static final String CURRENT_BAROMETER = "baro?";

  /**
   * Obtain the Tello tof value in millimeters.
   */
  public static final String CURRENT_TOF ="tof?";

  /**
   * Obtain the Tello temperature C.
   */
  public static final String CURRENT_TEMPERATURE = "temp?";

  /**
   * Obtain the Tello attitude in 3 axes, pitch, roll, yaw.
   */
  public static final String CURRENT_ATTITUDE = "attitude?";

  /**
   * Obtain the Tello height in decimeters.
   */
  public static final String CURRENT_HEIGHT = "height?";

  /**
   * Obtain the Tello acceleration in 3 axes, x y z.
   */
  public static final String CURRENT_ACCELERATION = "acceleration?";

  /**
   * Set remote control values. 4 channels, left/right, forward/backward, up/down and yaw.
   * All values in the range -100 to +100 representing speed(cm/s).
   */
  public static final String RC = "rc";
  
  /**
   * Enable mission mode.
   */
  public static final String MON = "mon";
  
  /**
   * Disable misson mode.
   */
  public static final String MOFF = "moff";
  
  /**
   * Select mission mode camera(s).
   */
  public static final String MDIRECTION = "mdirection";
  
  /**
   * Put drone in station mode. In this mode the drone is not a WIFI access point.
   * Instead it will connect to the named network using the provided password.
   */
  public static final String STATION_MODE = "ap";
}
