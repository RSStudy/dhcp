/**
 * 
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class SendData {
  public SendData() throws IOException{
    DatagramSocket socket = new DatagramSocket();
    InetAddress IPAddress = InetAddress.getByName("localhost");
    String out = "This is a message from the send class";
    byte[] data = out.getBytes();
    DatagramPacket send = new DatagramPacket(data, data.length, IPAddress, 8001);
    System.err.println(data.length);
    socket.send(send);
    System.out.println("message send" );
    socket.close();
  }
  public SendData(DatagramPacket send) throws IOException{
    DatagramSocket socket = new DatagramSocket();
    socket.send(send);
    socket.close();
  }
  
}
