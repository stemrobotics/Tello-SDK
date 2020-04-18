package tellolib.exception;

/**
 * Exception thrown for Tello command errors.
 */
public class TelloCommandException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;

	public TelloCommandException(String message) 
	{
		super(message);
	}
  
	public TelloCommandException(String message, Throwable cause) 
	{
	  super(message, cause);
	}

	public TelloCommandException(Throwable cause) 
	{
		super(cause.getMessage(), cause);
	}
}
