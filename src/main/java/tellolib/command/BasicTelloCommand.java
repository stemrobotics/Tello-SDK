package tellolib.command;

import java.util.Objects;

/**
 * Holds a basic (without parameters) command to be sent to drone.
 */
public class BasicTelloCommand extends AbstractTelloCommand 
{
  public BasicTelloCommand(String command) 
  {
    super(command);
  }

  @Override
  public String composeCommand() 
  {
    return command;
  }

  @Override
  public boolean equals(Object o) 
  {
    if (this == o) return true;
    
    if (o == null || getClass() != o.getClass()) return false;
    
    BasicTelloCommand that = (BasicTelloCommand) o;
    return Objects.equals(command, that.command);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(command);
  }

  @Override
  public String toString() 
  {
    return "BasicTelloCommand{"
        + "command='" + command + '\''
        + '}';
  }
}
