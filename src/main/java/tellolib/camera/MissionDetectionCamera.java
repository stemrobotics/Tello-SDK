package tellolib.camera;

/**
 * Enum for mission detection camera selection.
 */

public enum MissionDetectionCamera
{
	  downward,
	  forward,
	  both;
		  
	  public static String toCommand(MissionDetectionCamera camera)
	  {
		  switch (camera)
		  {
			  case downward:
				  return "0";
			  	  
			  case forward:
				  return "1";
				  
			  case both:
				  return "2";
		  }
		  
		  return "";
	  }
}
