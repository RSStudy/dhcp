/**
 * 
 */
package helper;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class Mac {
  private byte[] mac = null;
  
  /**
   * get the mac address from the network card.
   * @throws UnknownHostException
   * @throws SocketException
   */
  public Mac() throws UnknownHostException, SocketException{
    InetAddress address = InetAddress.getLocalHost();
    NetworkInterface ni = NetworkInterface.getByInetAddress(address);
    setMac(ni.getHardwareAddress());
  }

  /**
   * Set a mac address manually for simulation purposes
   * @param mac the mac address
   */
  public Mac(byte[] mac){
    setMac(mac);
  }
  
  private void setMac(byte[] mac){
    this.mac = mac;
  }
  
  /**
   * returns the mac address from the computer or the mac address with is set manually
   * @return the mac address if set else null;
   */
  public byte[] getMac(){
    return mac;
  }
  
  
  /**
   * This functions overwrites the standard toString() function and allows us to print the string
   */
  public String toString(){
    String out = "";
    for (int i =0; i < mac.length; i++){
      out += String.format("%02X", mac[i]);     /* Format the string as a hexadecimal number */
      out += (i%2 == 0)? ":" : "";              /* add the : to make the address easier to read */
    }
    return out;
  }
}
