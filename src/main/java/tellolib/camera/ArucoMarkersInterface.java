package tellolib.camera;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

import java.util.ArrayList;

/**
 * Convenience functions for OpenCV Aruco Markers feature.
 */
public interface ArucoMarkersInterface
{
	/**
	 * Perform Aruco marker detection on the supplied image.
	 * @param frame Image to analyze for markers.
	 * @return True if marker(s) detected, false if not.
	 */
	public boolean detectMarkers(Mat frame);
	
	/**
	 * Perform Aruco marker detection on the current frame held by camera, 
	 * camera must be streaming.
	 * @return True if marker(s) detected, false if not.
	 */
	public boolean detectMarkers();
	
	/**
	 * Get the number of markers detected on last call to 
	 * detectMarkers().
	 * @return Number of detected markers.
	 */
	public int getMarkerCount();
	
	/**
	 * Get the marker id of the selected marker detected by the
	 * last call to detectMarkers().
	 * @param index Marker to select indexed from 0.
	 * @return Marker id number or -1 if no markers or index out of range.
	 */
	public int getMarkerId(int index);
	
	/**
	 * Get detected markers as rectangles located within the image used in
	 * last call to detectMarkers() as x,y,h,w.
	 * @return List of target rectangles or null if no markers found.
	 */
	public ArrayList<Rect> getMarkerTargets();

	/**
	 * Get the contours of detected markers as an array points for each
	 * marker from the last call to detectMarkers().
	 * @return Array of vectors of 4 corner points for each marker. Null if
	 * no markers found.
	 */
	public ArrayList<MatOfPoint> getMarkerContours();
}
