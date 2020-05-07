package tellolib.communication;

import tellolib.command.TelloCommandInterface;
import tellolib.drone.TelloDrone;
import tellolib.exception.TelloCommandException;
import tellolib.exception.TelloConnectionException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Low level support interface for sending and receiving data from Tello drone.
 */
public class TelloCommunication implements TelloCommunicationInterface
{
	private final Logger logger = Logger.getLogger("Tello");

  /**
   * Datagram sockets for UDP communication with the Tello drone.
   */
  private DatagramSocket ds, dsStatus;

  /**
   * Drone IP address.
   */
  private InetAddress ipAddress;

  /**
   * Drone UDP ports and timeout.
   */
  private Integer udpPort, udpStatusPort, socketTimeout = 10000;
	
  // Private constructor, holder class and getInstance() implement this
  // class as a singleton.
	
  private TelloCommunication() throws TelloConnectionException 
  {
    try 
    {
      ipAddress = InetAddress.getByName(TelloDrone.IP_ADDRESS);
      udpPort = TelloDrone.UDP_PORT;
      udpStatusPort = TelloDrone.UDP_STATUS_PORT;
    } catch (Exception e) {
      throw new TelloConnectionException(e);
    }
  }
  
  private static class SingletonHolder 
  {
    public static final TelloCommunication INSTANCE = new TelloCommunication();
  }
	
  /**
   * Get the global instance of TelloCommunication.
   * @return Global TelloCommunication instance.
   */
  public static TelloCommunication getInstance()
  {
	return SingletonHolder.INSTANCE;
  }

  @Override
  public void connect() throws TelloConnectionException
  {
    try 
    {
      logger.info("Connecting to drone...");
      
      ds = new DatagramSocket(udpPort);	// new dg socket to send/receive commands.
      
      ds.setSoTimeout(socketTimeout);	// timeout on socket operations.
      
      ds.connect(ipAddress, udpPort);
      
      if (!ipAddress.isReachable(100)) throw new TelloConnectionException("Tello not responding");
      
      dsStatus = new DatagramSocket(udpStatusPort);	// new dg socket to receive status feed.
      
      dsStatus.setSoTimeout(socketTimeout);	// timeout on socket operations.
      
      logger.info("Connected!");
    } catch (Exception e) {
      if (dsStatus != null) dsStatus.close();
      if (ds != null) ds.close();
      //e.printStackTrace();
      throw new TelloConnectionException("Connect failed" , e);
    }
  }

  @Override
  public synchronized void executeCommand(final TelloCommandInterface telloCommand) throws TelloConnectionException, 
  											TelloCommandException
  {
	String response;

	if (telloCommand == null) throw new TelloCommandException("Command was null");
     
    if (!ds.isConnected()) throw new TelloConnectionException("No connection");

    final String command = telloCommand.composeCommand();
    
    logger.fine("executing command: " + command);

    try 
    {
      sendData(command);
      response = receiveData();
    } catch (Exception e) {
      throw new TelloConnectionException(e);
    } 

    logger.finer("response: " + response);

    if (response.toLowerCase().startsWith("forced stop")) return;
    if (response.toLowerCase().startsWith("unknown command")) throw new TelloCommandException("unknown command");
    if (response.toLowerCase().startsWith("out of range")) throw new TelloCommandException("invalid parameter");
    if (!response.toLowerCase().startsWith("ok")) throw new TelloCommandException("command failed: " + response);
  }

  @Override
  public synchronized void executeCommandNoWait(final TelloCommandInterface telloCommand) throws TelloConnectionException, 
  												TelloCommandException  
  {
	if (telloCommand == null) throw new TelloCommandException("Command was null");
     
    if (!ds.isConnected()) throw new TelloConnectionException("No connection");

    final String command = telloCommand.composeCommand();
    
    logger.finer("executing command: " + command);

    try 
    {
      sendData(command);
    } catch (Exception e) {
      throw new TelloConnectionException(e);
    } 
  }

  @Override
  public Map<String, String> getTelloOnBoardData(List<String> valuesToBeObtained) 
  {
    Map<String, String> dataMap = new HashMap<>();

    return dataMap;
  }

  public synchronized String executeReadCommand(TelloCommandInterface telloCommand) throws TelloConnectionException, 
	TelloCommandException 
  {
	String response;
	  
	if (telloCommand == null) throw new TelloCommandException("Command was null");
    
    if (!ds.isConnected()) throw new TelloConnectionException("No connection");

    final String command = telloCommand.composeCommand();
    
    if (command != "battery?") logger.fine("executing command: " + command);

    try 
    {
      sendData(command);
      response = receiveData();
    } catch (Exception e) {
        throw new TelloConnectionException(e);
    }

    logger.finer("response: " + response);

    if (response.toLowerCase().startsWith("unknown command")) throw new TelloCommandException("unknown command");
    // Original Tello (not edu) has misspelled error return.
    if (response.toLowerCase().startsWith("unkown command")) throw new TelloCommandException("unknown command");
    if (response.toLowerCase().startsWith("out of range")) throw new TelloCommandException("invalid parameter");
    if (response.toLowerCase().startsWith("error")) throw new TelloCommandException("command failed: " + response);
    
    return response;
  }

  @Override
  public void executeCommands(List<TelloCommandInterface> telloCommandList) 
  {

  }

  @Override
  public void disconnect() 
  {
	if (dsStatus != null) dsStatus.close();
	
	if (ds != null) ds.close();
	
	logger.info("Disconnected!");
  }
  
  private void sendData(String data) throws IOException 
  {
    byte[] sendData = data.getBytes();
    final DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, udpPort);
    ds.send(sendPacket);
  }

  private String receiveData() throws IOException 
  {
    byte[] receiveData = new byte[1024];
    final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    ds.receive(receivePacket);
    return trimExecutionResponse(receiveData, receivePacket);
  }

  public String receiveStatusData() throws IOException 
  {
    byte[] receiveData = new byte[1024];
    final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    dsStatus.receive(receivePacket);
    return trimExecutionResponse(receiveData, receivePacket);
  }

  private String trimExecutionResponse(byte[] response, DatagramPacket receivePacket) 
  {
    response = Arrays.copyOf(response, receivePacket.getLength());
    return new String(response, StandardCharsets.UTF_8);
  }
  
  public void setTimeout(int ms) 
  {
	  socketTimeout = ms;
  }
  
  public int getTimeout() 
  {
	  return socketTimeout;
  }
}
