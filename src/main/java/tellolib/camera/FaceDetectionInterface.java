package tellolib.camera;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * Convenience functions for Face detection with OpenCV.
 */
public interface FaceDetectionInterface
{
	/**
	 * Perform face detection function on an image.
	 * @param frame Mat to examine for faces.
	 * @return True if face(s) detected, false if not.
	 */
	public boolean detectFaces(Mat frame);

	/**
	 * Perform face detection function on the current image
	 * in the camera stream.
	 * @return True if face(s) detected, false if not.
	 */
	public boolean detectFaces();
	
	/**
	 * Get the number of faces detected in the last call to
	 * detectFaces().
	 * @return Number of faces. May be zero.
	 */
	public int getFaceCount();
	
	/**
	 * Return bounding rectangles for faces detected in last
	 * call to detectFaces().
	 * @return Array of rectangles bounding faces.
	 */
	public Rect[] getFaces();

}
