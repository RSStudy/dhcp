/**
 * 
 */
package main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import dhcp.constants.DHCPMessageType;
import dhcp.constants.OptionCodes;
import dhcp.dataTypes.DCHPMessage;
import dhcp.dataTypes.DHCPOptions;
import dhcp.ip.IPPool;
import helper.ConsolCommand;
import helper.Mac;
import pdf.LatexGenerator;
import udp.SendData;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class ClientMain {

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
   // IPPool ipPool = IPPool.getInstance();
   // ipPool.reserveAddress(new Mac());
   // System.out.println(ipPool.toString());
    
    DCHPMessage message = new DCHPMessage();
    message.setChaddr(new Mac());
    new java.util.Random().nextBytes(message.getXid()); /* set an random xid */
    DHCPOptions options = new DHCPOptions();
    options.addOption(OptionCodes.DhcpMessageType.getValue(), DHCPMessageType.DHCPDISCOVER.getValue());
    message.setOptions(options);
    LatexGenerator.CreateLatexFile("test.tex", "output", message);
    //pdflatex <filename>
    System.out.println(ConsolCommand.executeCommand("ping -c 3 google.com"));
    int a =0;
    int b = a;
   // byte[] out = message.serialize();
    //InetAddress IPAddress = InetAddress.getByName("localhost");
    //DatagramPacket outPacket = new DatagramPacket(out, out.length, IPAddress, 8001);
    //new SendData(outPacket);
  }

}
