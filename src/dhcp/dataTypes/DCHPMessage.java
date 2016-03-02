/**
 * 
 */
package dhcp.dataTypes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import helper.Mac;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class DCHPMessage {
/* source https://en.wikipedia.org/wiki/Dynamic_Host_Configuration_Protocol */
/* http://www.tcpipguide.com/free/t_DHCPMessageFormat.htm */
/* https://www.ietf.org/rfc/rfc2131.txt*/
  @Setter @Getter private byte    op;      /* operation code 1 request 2 reply */
  @Setter @Getter private HType   htype;   /* hardware type */
  @Setter @Getter private byte    hlen;    /* hardware address length */
  @Setter @Getter private byte    hops;    /* */
  @Setter @Getter private byte[]  xid;     /* transaction indentifier */
  @Setter @Getter private byte[]  secs;    /* */
  @Setter @Getter private byte[]  flags;   /* */ 
  @Setter @Getter private byte[]  ciaddr;  /* cleints ip address */
  @Setter @Getter private byte[]  yiaddr;  /* your ip address */
  @Setter @Getter private byte[]  siaddr;  /* server ip address */
  @Setter @Getter private byte[]  giaddr;  /* gateway ip address */
  @Setter @Getter private byte[]  chaddr;  /* client hardware address */
  @Setter @Getter private byte[]  sname;   /* optional field, null termited string, server host name */
  @Setter @Getter private byte[]  file;    /* boot file name */
  @Setter @Getter private byte[]  options; /* the dhcp options */

  public DCHPMessage(){
    op        = 1;
    htype     = HType.Ethernet;
    hlen      = 6;
    hops      = 0;
    xid       = new byte[4];
    secs      = new byte[2];
    flags     = new byte[2];
    ciaddr    = new byte[4];
    yiaddr    = new byte[4];
    siaddr    = new byte[4];
    giaddr    = new byte[4];
    chaddr    = new byte[16];
    sname     = new byte[64];
    file      = new byte[128];
    //http://www.iana.org/assignments/bootp-dhcp-parameters/bootp-dhcp-parameters.xhtml
    
  }
  public byte[] generateMessage() throws IOException{
    return generateMessage(DHCPMessageGeneratorType.discover);
  }
  public byte[] generateMessage(DHCPMessageGeneratorType type) throws IOException{
    return generateMessage(type,null);
    
  }
  public byte[] generateMessage(DHCPMessageGeneratorType type, DCHPMessage message) throws IOException{
    this.options = OptionsGenerator.GenerateOptions(type);
    switch(type){
    case discover: Discover();
      break;
    case offer: Offer();
      break;
    case request: Request();
      break;
    case ack: Ack();
      break;
    }
      return serialize();
    
  }
  private void Discover() throws UnknownHostException, SocketException{
    Mac mac = new Mac();
    System.arraycopy( mac.getMac(), 0, chaddr, 0, mac.getMac().length );
  }
  
  private void Offer(){
    
  }
  
  private void Request(){
    
  }
  
  private void Ack(){
    
  }
  private  byte[] serialize() throws IOException{
    int size = 232 + this.options.length;
    ByteArrayOutputStream bOutSt = new ByteArrayOutputStream(size);
    DataOutputStream      outSt  = new DataOutputStream(bOutSt);

    outSt.writeByte (this.op              );
    outSt.writeByte (this.htype.getValue());
    outSt.writeByte (this.hlen            );
    outSt.writeByte (this.hops            );
    outSt.write     (this.xid,     0,    4);
    outSt.write     (this.secs,    0,    2);
    outSt.write     (this.flags,   0,    2);
    outSt.write     (this.ciaddr,  0,    4);
    outSt.write     (this.yiaddr,  0,    4);
    outSt.write     (this.siaddr,  0,    4);
    outSt.write     (this.giaddr,  0,    4);
    outSt.write     (this.chaddr,  0,   16);
    outSt.write     (this.sname,   0,   64);
    outSt.write     (this.file,    0,  128);
    outSt.write     (this.options, 0, this.options.length);
    
    return  bOutSt.toByteArray();
  }
  
  public void DeSerialize(byte[] message){
    this.op = message[0];
    //this.htype = message[1];
  }
  
  public String toString(){
    return "implement this function";
    
  }
  
}
