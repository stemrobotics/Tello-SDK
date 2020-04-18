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
		
		Mat	grayFrame = new Mat();

		ids = new Mat();
		corners = new Vector<Mat>();
		
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		
		Aruco.detectMarkers(grayFrame, dict, corners, ids);
		
		if (ids.empty()) return false;
		
		logger.fine("ids=" + ids.dump());
		
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
		ArrayList<Rect>			markers = getMarkerTargets();
		ArrayList<Point>		points;
		
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
		
		for (int i = 0; i < getMarkerCount(); i++)
		{
			Mat mat = corners.get(i);

			//logger.fine("corners(" + i + ")=" + mat.dump());

			// The marker corner mat (matrix) contains an array of the locations
			// of the 4 corners of the detected marker starting with the upper
			// left corner, upper right, lower right, lower left. For each corner
			// the location is given as x,y. This quite different than the Rect
			// class description of a rectangle: upper left corner as x,y and
			// height and width. So we convert.
			
			//logger.fine("x0=" + mat.get(0, 0)[0] + " y0=" + mat.get(0, 0)[1]);
			//logger.fine("x1=" + mat.get(0, 1)[0] + " y1=" + mat.get(0, 1)[1]);
			//logger.fine("x2=" + mat.get(0, 2)[0] + " y2=" + mat.get(0, 2)[1]);
			//logger.fine("x3=" + mat.get(0, 3)[0] + " y3=" + mat.get(0, 3)[1]);
			
			int x0 = (int) mat.get(0, 0)[0];
			int y0 = (int) mat.get(0, 0)[1];
			int x1 = (int) mat.get(0, 1)[0];
			int y1 = (int) mat.get(0, 1)[1];
			//int x2 = (int) mat.get(0, 2)[0];
			//int y2 = (int) mat.get(0, 2)[1];
			//int x3 = (int) mat.get(0, 3)[0];
			int y3 = (int) mat.get(0, 3)[1];

			Rect rect = new Rect();
			rect.x = x0;
			rect.y = y0;
			rect.width = x1 - x0;
			rect.height = y3 - y0;
			
			targetRectangles.add(rect);
			
			logger.fine("rect=" + rect.toString());
		}
		
		return targetRectangles;
	}
}
