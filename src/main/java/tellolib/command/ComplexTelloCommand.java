package tellolib.command;

import java.util.Objects;

/**
 * Holds a complex (with parameters) command to be sent to drone.
 */
public class ComplexTelloCommand extends AbstractTelloCommand 
{
  public ComplexTelloCommand(String command, String parameters) 
  {
    super(command, parameters);
  }

  @Override
  public String composeCommand() 
  {
    return command + " " + parameters;
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
    return "ComplexTelloCommand{"
        + "command='" + command + " " + parameters + '\''
        + '}';
  }
}

