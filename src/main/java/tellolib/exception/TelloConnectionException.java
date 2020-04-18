package tellolib.exception;

/**
 * Exception thrown for communication errors.
 */
public class TelloConnectionException extends RuntimeException 
{
	private static final long serialVersionUID = 1L;

	public TelloConnectionException(String message) 
	{
		super(message);
	}
  
	public TelloConnectionException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	public TelloConnectionException(Throwable cause) 
	{
		super(cause.getMessage(), cause);
	}
}
