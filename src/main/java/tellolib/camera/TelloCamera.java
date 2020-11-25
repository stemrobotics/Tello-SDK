package tellolib.camera;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Supplier;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.videoio.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import tellolib.communication.TelloConnection;
import tellolib.drone.TelloDrone;

/**
 * Convenience functions for Tello camera.
 */
public class TelloCamera implements TelloCameraInterface
{
	private final 				Logger logger = Logger.getLogger("Tello");

	private boolean				recording;
	private Thread				videoCaptureThread;
	private VideoCapture		camera;
	private Mat					image;
	private VideoWriter			videoWriter;
	private Dimension 			screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Size				videoFrameSize = new Size(screenSize.width - 400, screenSize.height - 100);
	private double				videoFrameRate = 30;
	private SimpleDateFormat	df = new SimpleDateFormat("yyyy-MM-dd.HHmmss");
	private JFrame				jFrame;
	private JLabel				jLabel;
	private String				statusBar = null;
	private Supplier			<String>statusBarMethod = null;
	private Object				lockObj = new Object();
	
	private ArrayList<Rect>			targetRectangles;
	private ArrayList<MatOfPoint>	contours = null;
	private Scalar 					targetColor = new Scalar(0, 0, 255), contourColor = new Scalar(255, 0, 0);
	private int						targetWidth = 1, contourWidth = 1;
	
	// Private constructor, holder class and getInstance() implement this
	// class as a singleton.
	
	private TelloCamera()
	{
		// Load OpenCV library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		image = new Mat();
	}
    
	private static class SingletonHolder 
	{
        public static final TelloCamera INSTANCE = new TelloCamera();
    }
	
	/**
	 * Get the global instance of TelloCamera.
	 * @return Global TelloCamera instance.
	 */
	public static TelloCamera getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	@Override
	public void startVideoCapture(boolean liveWindow)
	{
		logger.fine("starting video capture");
		
		if (camera != null) return;
		
		// Create VideoCapture object to accept video feed from drone.
		camera = new VideoCapture();
		
	 	camera.setExceptionMode(true);

	 	// Start capture object listening for video packets.
		camera.open("udp://0.0.0.0:" + Integer.toString(TelloDrone.UDP_VIDEO_PORT), Videoio.CAP_FFMPEG);
		
		logger.fine("video camera open:" + camera.isOpened());

		// Create window to display live video feed.

		if (liveWindow)
		{
			// Create window and image display component using Java Swing library.
	        jFrame = new JFrame("Tello Controller Test");
	        jFrame.setPreferredSize(new Dimension((int) videoFrameSize.width, (int) videoFrameSize.height));
	        jLabel = new JLabel();
	        jFrame.getContentPane().add(jLabel);
	        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        jFrame.pack();
	        jFrame.setVisible(true);
		}
		
        // Start thread to process images from video feed.
		
		videoCaptureThread = new VideoCaptureThread();
		videoCaptureThread.start();
	}
	
	@Override
	public void stopVideoCapture()
	{
		if (camera == null) return;

		if (recording) stopRecording();
		
		if (videoCaptureThread != null)
		{
			logger.fine("stopping video capture thread");

			try
			{
				// Signal thread to stop.
				videoCaptureThread.interrupt();
				// Wait at most 2 sec for thread to stop.
				videoCaptureThread.join(2000);
				logger.fine("after join");
			} catch (Exception e) {e.printStackTrace();}
		}
		
		if (jFrame != null) 
		{
			jFrame.setVisible(false);
			jFrame.dispose();
		}
		
		camera.release();
		image.release();
		image = null;
		camera = null;
	}
	
	@Override
	public Mat getImage()
	{
		// Make a copy of the current image and return to the caller. This allows
		// the caller to work with the copy while the the internal image in this
		// class is continues to be updated by the video processing thread. The
		// synchronized key word prevents the processing thread from changing
		// the internal image while we are making the copy.
		
	    synchronized (lockObj) 
	    {
	    	if (image == null)
	    		return null;
	    	else
	    		return image.clone();
	    }
	}

	// Thread to read the images of the video stream and process them
	// as appropriate.
	private class VideoCaptureThread extends Thread
	{
		VideoCaptureThread()
		{
			logger.fine("video thread constructor");
			
			this.setName("VideoCapture");
	    }
		
	    public void run()
	    {
	    	Mat	imageRaw = new Mat();
	    	
			logger.fine("video capture thread started");
			
	    	try
	    	{
	    		// Loop reading images from the video feed storing the current image
	    		// in the image variable.
	    		
	    		while (!isInterrupted())
	    		{
	    		    camera.read(imageRaw);
	    			
	    			synchronized (lockObj) 
	    			{
		    		    // Resize raw image to window (frame) size.
	    				
	    				Imgproc.resize(imageRaw, image, videoFrameSize);
	
	    		    	// Draw target rectangles/contours on image.
	    		    
		    			if (targetRectangles != null)
		    				for (Rect rect: targetRectangles) 
		    					Imgproc.rectangle(image, 
		    							new Point(rect.x, rect.y), 
		    							new Point(rect.x + rect.width, rect.y +  rect.height), 
		    							targetColor, targetWidth);
		    			
		    			if (contours != null) Imgproc.drawContours(image, contours, -1, contourColor, contourWidth);
	
		    			// Draw status bar text on image.
		    			
		    			if (statusBar != null && statusBarMethod == null)
		    				Imgproc.putText(image, statusBar, new Point(0, image.height() - 25), Imgproc.FONT_HERSHEY_PLAIN, 
		    						1.5, new Scalar(255, 255, 255), 2, Imgproc.FILLED);
	
		    			if (statusBarMethod != null)
		    				Imgproc.putText(image, statusBarMethod.get(), new Point(0, image.height() - 25), Imgproc.FONT_HERSHEY_PLAIN, 
		    						1.5, new Scalar(255, 255, 255), 2, Imgproc.FILLED);
	    			}
	    		
	    			// Write image to live window if open.
	    		    if (jFrame != null)	updateLiveWindow(image);
	    			
	    			// Write image to recording file if recording.
	    			if (recording) videoWriter.write(image);
	    		}
	    	}
	    	catch (Exception e) 
	    	{ 
	    		logger.severe("video capture failed: " + e.getMessage()); 
	    		// Error on status monitor most likely means drone has shut down.
	    		TelloDrone.getInstance().setConnection(TelloConnection.DISCONNECTED);	    	}
	    	finally {}
    		
    		logger.fine("Video capture thread ended");

	    	videoCaptureThread = null;
	    }
	}

	@Override
	public boolean takePicture( String folder )
	{
		String	fileName = null;
		boolean	result = false;
		Mat		image;
		
		if (camera == null) 
		{
			logger.warning("No video stream");
			return result;
		}
		
		// Get a copy of the current image to work with.
		image = getImage();
		
		if(image != null && !image.empty())
		{
			fileName = folder + "\\" + df.format(new Date()) + ".jpg";
			
			logger.info("h=" + image.height() + ";w=" + image.width());
			
			if (Imgcodecs.imwrite(fileName, image))
			{
				logger.fine("Picture saved to " + fileName);
				result = true;
			} else
				logger.warning("Picture file save failed");
		} else
			logger.warning("Take Picture failed: image not available");
		
		return result;
	}

	// Update the live window with the supplied image.
	private void updateLiveWindow(Mat image)
	{
		try
		{
			// Reset frame size to current window size, allows for window resize.
			// Takes effect on next update.
			videoFrameSize = new Size(jFrame.getWidth(), jFrame.getHeight());
			
	        // Convert image Mat to a jpeg image.
	        Image img = HighGui.toBufferedImage(image);
	        
	        // Set label component content of the live window to new image.
	        jLabel.setIcon(new ImageIcon(img));
		}
		catch (Exception e) {logger.warning("live window update failed: " + e.toString());}
	}
	
	@Override
	public boolean startRecording( String folder )
	{
		boolean		result = false;
		String		fileName;
		
		if (camera == null) 
		{
			logger.warning("No video stream");
			return result;
		}
		
		// Determine folder and name of video file.
		fileName = folder + "\\" + df.format(new Date()) + ".avi";

		// Create a writer to write images to the file.
		videoWriter = new VideoWriter(fileName, VideoWriter.fourcc('M', 'J', 'P', 'G'), videoFrameRate, 
									  videoFrameSize, true);

		if (videoWriter != null && videoWriter.isOpened())
		{
			recording = result = true;
		
			logger.fine("Video recording started to " + fileName);
		} else
			logger.warning("Video recording failed");
		
		return result;
	}

	@Override
	public void stopRecording()
	{
		if (camera == null || !recording) return;

		recording =  false;

		videoWriter.release();
		
		logger.fine("Video recording stopped");
	}
	
	@Override
	public boolean isRecording()
	{
		return recording;
	}

	@Override
	public void addTarget( Rect target )
	{
		synchronized(lockObj)
		{
			if (target == null)
			{
				targetRectangles = null;
				return;
			}
		
			if (targetRectangles == null) targetRectangles = new ArrayList<Rect>();
		
			targetRectangles.add(target);
		}
	}

	@Override
	public void addTarget( Rect target, int width, Scalar color )
	{
		targetWidth = width;
		targetColor = color;
		
		addTarget(target);
	}
	
	@Override
	public void setContours(ArrayList<MatOfPoint> contours)
	{
		synchronized(lockObj) {this.contours = contours;}
	}

	@Override
	public void setContours( ArrayList<MatOfPoint> contours, int width, Scalar color )
	{
		contourWidth = width;
		contourColor = color;
		
		setContours(contours);
	}
	
	public Size getImageSize()
	{
		synchronized(lockObj)
		{
			if (image == null) return new Size(0,0);
		
			return new Size(image.width(), image.height());
		}
	}

	@Override
	public void setStatusBar( String message )
	{
		synchronized(lockObj) {statusBar = message;}
	}

	@Override
	public void setStatusBar( Supplier<String> method )
	{
		synchronized(lockObj) {statusBarMethod = method;}
	}

	@Override
	public void setVideoFrameSize( int width, int height )
	{
		videoFrameSize = new Size(width, height);		
	}
}
