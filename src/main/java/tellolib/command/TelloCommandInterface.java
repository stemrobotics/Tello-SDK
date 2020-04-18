package tellolib.command;

/**
 * Base class for the various Tello commands.
 */
public interface TelloCommandInterface {
  /**
   * Compose the command with all the parameters necessary.
   *
   * @return Composed command.
   */
  String composeCommand();
}