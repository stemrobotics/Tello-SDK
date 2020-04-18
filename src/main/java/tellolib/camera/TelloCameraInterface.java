package tellolib.camera;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

/**
 * Convenience functions for Tello camera.
 */
public interface TelloCameraInterface
{
  /**
   * Start capture of video stream from drone for processing by
   * this program. This function may take several seconds to complete.
   * @param liveWindow True to display video feed in a live window.
   */
  void startVideoCapture(boolean liveWindow);
  
  /**
   * Stop video stream capture thread.
   */
  void stopVideoCapture();

  /**
   * Save the current image from the video feed to a file in
   * the named folder.
   * @param folder Location to save image.
   * @return True if image saved, false if failed.
   */
  boolean takePicture(String folder);
  
  /**
   * Turn on recording of the video feed to an .avi file in
   * the named folder.
   * @param folder Location to save the video file.
   * @return True if recording started, false if failed.
   */
  boolean startRecording(String folder);
  
  /**
   * Stop recording the video feed.
   */
  void stopRecording();
  
  /**
   * Returns recording state.
   * @return True if recording in progress, false if not.
   */
  boolean isRecording();

  /**
   * Returns the current image from the video feed.
   * @return The current image.
   */
  Mat getImage();
	
  /**
   * Add a target rectangle to be drawn on the camera feed images.
   * Width of lines defaults to 1 pixel, color defaults to 0,0,255 (red).
   * @param target Rectangle to draw, null to clear all rectangles.
   */
  void addTarget(Rect target);
	
  /**
   * Add a target rectangle to be drawn on the camera feed images.
   * @param target Rectangle to draw, null to clear all rectangles.
   * @param width Pixel width of rectangle lines.
   * @param color Set the line color used to draw rectangles. B,G,R color values. 
   */
  void addTarget(Rect target, int width, Scalar color);
  
  /**
   * Set an array of MatOfPoint vectors, one vector for each contour to be
   * drawn on the camera feed images. Each vector can contains as many points
   * as needed to draw the contour.
   * @param contours Array of MatOfPoint objects, one for each contour, null to
   * clear all contours.
   */
  public void setContours(ArrayList<MatOfPoint> contours);
  
  /**
   * Set an array of MatOfPoint vectors, one vector for each contour to be
   * drawn on the camera feed images. Each vector can contains as many points
   * as needed to draw the contour.
   * @param contours Array of MatOfPoint objects, one for each contour, null to
   * clear all contours.
   * @param width Pixel width of rectangle lines.
   * @param color Set the line color used to draw rectangles. B,G,R color values. 
   */
  public void setContours(ArrayList<MatOfPoint> contours, int width, Scalar color);
  
  /**
   * Set a string of data to be displayed across the bottom of the video feed.
   * @param message The data to be displayed or null to clear.
   */
  public void setStatusBar(String message);
}
