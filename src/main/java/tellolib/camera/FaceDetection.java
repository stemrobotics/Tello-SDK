package tellolib.camera;

import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

/**
 * Face detection with OpenCV.
 */
public class FaceDetection implements FaceDetectionInterface
{
	private final Logger		logger = Logger.getLogger("Tello");
	
	private CascadeClassifier	faceCascade = new CascadeClassifier();
	
	private Rect[] 				facesArray = null;
	
	private FaceDetection()
	{
		String basePath = System.getProperty("user.dir");
		String classifierPath = basePath + "\\src\\resources\\haarcascade_frontalface_alt.xml";
		
		logger.fine("classifier path=" + classifierPath);
		
		faceCascade.load(classifierPath);
	}
    
	private static class SingletonHolder 
	{
        public static final FaceDetection INSTANCE = new FaceDetection();
    }
	
	/**
	 * Get the global instance of FaceDetection class.
	 * @return Global FaceDetection instance.
	 */
	public static FaceDetection getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	@Override
	public boolean detectFaces()
	{
		Mat image = TelloCamera.getInstance().getImage();
		
		return detectFaces(image);	
	}

	@Override
	public boolean detectFaces(Mat image)
	{
		MatOfRect 			faces = new MatOfRect();
		Mat 				grayFrame = new Mat();
		int 				absoluteFaceSize = 0;
		
		if (image == null) return false;
		
		logger.finer("detectFaces");
		
		// convert the frame in gray scale
		Imgproc.cvtColor(image, grayFrame, Imgproc.COLOR_BGR2GRAY);
		
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		// compute minimum face size (1% of the frame height, in our case)
		int height = grayFrame.rows();

		if (Math.round(height * 0.2f) > 0) absoluteFaceSize = Math.round(height * 0.01f);
				
		//logger.fine("face size=" + absoluteFaceSize + ";height=" + height);
		
		// detect faces
		faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(absoluteFaceSize, absoluteFaceSize), new Size(height,height));
		
		// each rectangle in faces is a face.
		facesArray = faces.toArray();
		
		logger.finer("faces detected = " + facesArray.length);		
		
		if (facesArray.length == 0) return false;
		
		return true;
	}

	@Override
	public int getFaceCount()
	{
		if (facesArray == null) return 0;
		
		return facesArray.length;
	}

	@Override
	public Rect[] getFaces()
	{
		return facesArray;
	}
}