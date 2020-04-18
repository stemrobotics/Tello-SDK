package tellolib.command;

/**
 * Generic command to be sent to the drone. Can be used when you want to
 * accept either a basic or complex command as both implement this class.
 */
public abstract class AbstractTelloCommand implements TelloCommandInterface 
{

  protected String command, parameters;

  public AbstractTelloCommand(String command) 
  {
    this.command = command;
  }
  
  public AbstractTelloCommand(String command, String parameters) 
  {
	 this.command = command;
	 this.parameters = parameters;
  }
  
  public String getCommand() 
  {
    return command;
  }

  public void setCommand(String command) 
  {
    this.command = command;
  }
  
  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) 
  {
    this.parameters = parameters;
  }
}
