/**
 * 
 */
package main;

import java.io.IOException;
import java.net.DatagramPacket;

import dhcp.Processor;
import dhcp.constants.DHCPMessageType;
import dhcp.dataTypes.DCHPMessage;
import udp.ReceiveData;
import udp.SendData;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class ServerMain {
  ReceiveData receive = null ;
  /**
   * @param args
   * @throws IOException 
   * @throws InterruptedException 
   */
  
  public ServerMain() throws IOException{
    State state = State.receive;
    receive = new ReceiveData();
    receive.start();
    DatagramPacket inPacket = null;
    DatagramPacket outPacket = null;
    byte[] packetData = null;
    DCHPMessage message = null;
    while(true){
      
      switch(state){
      case receive:
        inPacket = receive.getDatagramPacket();
        packetData = inPacket.getData();
        
        state = State.deserialize;
        break;
        
      case deserialize:
        message = new DCHPMessage();
        message.DeSerialize(packetData);
        
        state = State.process;
        break;
            
      case process:
        System.out.println("the incomming message");
        System.out.println(message.toString());
        
        Processor.Process(message);
        
        System.out.println("the outgoing message");
        System.out.println(message.toString());   
        
        state = State.serialise;
        break;
            
      case serialise:
        packetData = message.serialize();
        outPacket = new DatagramPacket(packetData, packetData.length, inPacket.getAddress(), inPacket.getPort());
        
        state = State.send;
        break;
              
      case send:
        new SendData(outPacket);
        
        state = State.receive;
        break;
      }
    }
  }
  
  private enum State{
    receive, deserialize, process, serialise, send;
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    new ServerMain();
  }
}
