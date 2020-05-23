package tellolib.camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.opencv.aruco.*;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.*;

/**
 * Convenience functions for OpenCV Aruco Markers feature.
 */
public class ArucoMarkers implements ArucoMarkersInterface
{
	private final Logger		logger = Logger.getLogger("Tello");

	private Dictionary			dict;
	private Mat					ids;
	private List<Mat> 			corners;
	
	private ArucoMarkers()
	{
		dict = Aruco.getPredefinedDictionary(Aruco.DICT_ARUCO_ORIGINAL);
	}
    
	private static class SingletonHolder 
	{
        public static final ArucoMarkers INSTANCE = new ArucoMarkers();
    }
	
	/**
	 * Get the global instance of ArucoTracking class.
	 * @return Global ArucoTracking instance.
	 */
	public static ArucoMarkers getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	@Override
	public boolean detectMarkers()
	{
		Mat image = TelloCamera.getInstance().getImage();
		
		return detectMarkers(image);	
	}
	
	@Override
	public boolean detectMarkers(Mat frame)
	{
		if (frame == null) return false;
		
		// Create empty Mat to receive the grayscale input Mat (image).
		
		Mat	grayFrame = new Mat();

		// Get new empty Mat to receive the list of marker Ids.
		ids = new Mat();
		
		// Create new empty vector (array) of marker corner sets which are each
		// a Mat (array) of 4 entries describing the 4 corners of a marker.
		corners = new Vector<Mat>();
		
		// Convert color image to grayscale (back & white).
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		
		// Use OpenCV Aruco class to perform marker detection. Returns array
		// of detected maker id numbers and an array of information (matrices)
		// describing the corner locations of each marker.
		
		Aruco.detectMarkers(grayFrame, dict, corners, ids);
		
		if (ids.empty()) return false;
		
		logger.finer("ids=" + ids.dump());
		
		return true;
	}

	@Override
	public int getMarkerCount()
	{
		if (ids == null) return 0;

		return (int) ids.total();
	}

	@Override
	public int getMarkerId( int index )
	{
		if (ids == null) return -1;

		if (index >= ids.total() || index < 0) return -1;
		
		return (int) ids.get(index, 0)[0];
	}

	@Override
	public ArrayList<MatOfPoint> getMarkerContours()
	{
		if  (ids == null) return null;
		
		ArrayList<MatOfPoint> 	contours = new ArrayList<MatOfPoint>();
		ArrayList<Point>		points;
		
		// Convert each marker corner matrix into a contour bounding the marker image.
		
		for (int i = 0; i < getMarkerCount(); i++)
		{
			Mat mat = corners.get(i);

			//logger.fine("corners(" + i + ")=" + mat.dump());
			
			points = new ArrayList<Point>();
			
			for (int j = 0; j < 4; j++)
			{
				//logger.fine("x" + j + "=" + mat.get(0, j)[0] + " y" + j + "=" + mat.get(0, j)[1]);

				int x = (int) mat.get(0, j)[0];
				int y = (int) mat.get(0, j)[1];
				
				points.add(new Point(x, y));
			}

			MatOfPoint contour = new MatOfPoint();

			//logger.fine("number of points=" + points.size());
			
			contour.fromList(points);
			
			contours.add(contour);
		}
		
		//logger.fine("number of contours=" + contours.size());
		
		return contours;
	}
	
	@Override
	public ArrayList<Rect> getMarkerTargets()
	{
		ArrayList<Rect>	targetRectangles = new ArrayList<Rect>();
		
		if  (ids == null) return null;
		
		// Convert each corner set into a rectangle. This works best when marker image is
		// displayed in level orientation and aligned with the drone camera.
		
		for (int i = 0; i < getMarkerCount(); i++)
		{
			// Get the Mat (2d array/matrix) of the marker we are processing. This Mat is
			// an array describing the 4 corners of the marker in the processed image.
			Mat mat = corners.get(i);

			//logger.finer("corners(" + i + ")=" + mat.dump());

			// The marker corner Mat (matrix) contains an array of the locations
			// of the 4 corners of the detected marker starting with the upper
			// left corner, upper right, lower right, lower left. For each corner
			// the location is given as x,y. This quite different than the OpenCV Rect
			// class description of a rectangle: upper left corner as x,y and
			// height and width. So we convert to rectangle using the upper left and
			// lower right corners as the two point that define a rectangle.
			
			
			int x0 = (int) mat.get(0, 0)[0];	// upper left x pos.			
			int y0 = (int) mat.get(0, 0)[1];	// upper left y pos.
			//int x1 = (int) mat.get(0, 1)[0];	// upper right x pos.
			//int y1 = (int) mat.get(0, 1)[1];	// upper right y pos.
			int x2 = (int) mat.get(0, 2)[0];	// lower right x pos.
			int y2 = (int) mat.get(0, 2)[1];	// lower right y pos.
			//int x3 = (int) mat.get(0, 3)[0];	// lower left x pos.
			//int y3 = (int) mat.get(0, 3)[1];	// lower left y pos.

			Rect rect = new Rect(new Point(x0, y0), new  Point(x2, y2));
			
			targetRectangles.add(rect);
			
			logger.finer("rect=" + rect.toString());
		}
		
		return targetRectangles;
	}
}
