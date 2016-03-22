/**
 * 
 */
package dhcp.dataTypes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import dhcp.ip.dataTypes.IPAddressType;
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
  @Setter @Getter private byte                op;           /* operation code 1 request 2 reply */
  @Setter @Getter private byte                htype;        /* hardware type */
  @Setter @Getter private byte                hlen;         /* hardware address length */
  @Setter @Getter private byte                hops;         /* */
  @Setter @Getter private byte[]              xid;          /* transaction indentifier */
  @Setter @Getter private byte[]              secs;         /* */
  @Setter @Getter private byte[]              flags;        /* */ 
  @Setter @Getter private IPAddressType       ciaddr;       /* clients ip address */
  @Setter @Getter private IPAddressType       yiaddr;       /* your ip address */
  @Setter @Getter private IPAddressType       siaddr;       /* server ip address */
  @Setter @Getter private IPAddressType       giaddr;       /* gateway ip address */
  @Setter @Getter private Mac                 chaddr;       /* client hardware address */
  @Setter @Getter private byte[]              sname;        /* optional field, null termited string, server host name */
  @Setter @Getter private byte[]              file;         /* boot file name */
  @Setter @Getter private byte[]              magicCookie;  /* */
  @Setter @Getter private DHCPOptions         options;      /* the dhcp options */

  /**
   * a copy constructor 
   * @param clone
   */
  public DCHPMessage(DCHPMessage clone){
   // throw new IllegalArgumentException("this function should be implemented");

    this.op = clone.op;
    this.htype = clone.htype;
    this.hlen = clone.hlen;
    this.hops = clone.hops;
    ////
    this.xid = clone.xid.clone();
    this.secs = clone.secs.clone();
    this.flags = clone.flags.clone();
    //////
    this.ciaddr = new IPAddressType(clone.ciaddr);
    this.yiaddr = new IPAddressType(clone.yiaddr);
    this.siaddr = new IPAddressType(clone.siaddr);
    this.giaddr = new IPAddressType(clone.giaddr);
    this.chaddr = new Mac(clone.chaddr);
    this.sname = clone.sname.clone();
    this.file = clone.file.clone();
    this.magicCookie = clone.magicCookie.clone();
    this. options = new DHCPOptions(clone.options);
    
  }
  public DCHPMessage(){
    op          = 1;
    htype       = HType.Ethernet.getValue();
    hlen        = 6;
    hops        = 0;
    xid         = new byte[4];
    secs        = new byte[2];
    flags       = new byte[2];
    ciaddr      = new IPAddressType(new byte[4]);
    yiaddr      = new IPAddressType(new byte[4]);
    siaddr      = new IPAddressType(new byte[4]);
    giaddr      = new IPAddressType(new byte[4]);
    chaddr      = new Mac(new byte[4]);
    sname       = new byte[64];
    file        = new byte[128];
    magicCookie = new byte[4];
    options     = new DHCPOptions();
    //http://www.iana.org/assignments/bootp-dhcp-parameters/bootp-dhcp-parameters.xhtml
    
    
  }
  
  public  byte[] serialize(){
    try {
      
      /* convert everything to byte arrays to make life easier*/
      byte[] tempOptions = this.options.Serialize();
      byte[] tempCiaddr = this.ciaddr.Serialize();
      byte[] tempYiaddr = this.yiaddr.Serialize();
      byte[] tempSiaddr = this.siaddr.Serialize();
      byte[] tempGiaddr = this.giaddr.Serialize();
      byte[] tempChaddr = new byte[16];  
      System.arraycopy( this.chaddr.getMac(), 0, tempChaddr,  0, this.chaddr.getMac().length  );
      
      /*calculate the size of the message */
      int size = 4 + this.xid.length + this.secs.length + this.flags.length + tempCiaddr.length + 
        tempYiaddr.length + tempSiaddr.length + tempGiaddr.length + tempChaddr.length + 
        this.sname.length + this.file.length + this.magicCookie.length + tempOptions.length;
      
      ByteArrayOutputStream bOutSt = new ByteArrayOutputStream(size);
      DataOutputStream      outSt  = new DataOutputStream(bOutSt);

      outSt.writeByte (this.op                                        );
      outSt.writeByte (this.htype                                     );
      outSt.writeByte (this.hlen                                      );
      outSt.writeByte (this.hops                                      );
      outSt.write     (this.xid,          0, this.xid.length          );
      outSt.write     (this.secs,         0, this.secs.length         );
      outSt.write     (this.flags,        0, this.flags.length        );
      outSt.write     (tempCiaddr,        0, tempCiaddr.length        );
      outSt.write     (tempYiaddr,        0, tempYiaddr.length        );
      outSt.write     (tempSiaddr,        0, tempSiaddr.length        );
      outSt.write     (tempGiaddr,        0, tempGiaddr.length        );
      outSt.write     (tempChaddr,        0, tempChaddr.length        );
      outSt.write     (this.sname,        0, this.sname.length        );
      outSt.write     (this.file,         0, this.file.length         );
      outSt.write     (this.magicCookie,  0, this.magicCookie.length  );
      outSt.write     (tempOptions,       0, tempOptions.length       );
      
      return  bOutSt.toByteArray();
    } catch (IOException e) {
      return null;
    }
  }
  
  public void DeSerialize(byte[] message){
    this.op     = message[0];
    this.htype  = message[1];
    this.hlen   = message[2];
    this.hops   = message[3];
    int i = 4;
    System.arraycopy( message, i, this.xid,         0, this.xid.length          );
    i += this.xid.length;
    System.arraycopy( message, i, this.secs,        0, this.secs.length         );
    i += this.secs.length;
    System.arraycopy( message, i, this.flags,       0, this.flags.length        );
    i += this.flags.length;
    
    byte[] tempCiaddr = new byte[4];
    System.arraycopy( message, i, tempCiaddr,       0, tempCiaddr.length       );
    this.ciaddr.DeSerialize(tempCiaddr);
    i += tempCiaddr.length;
    
    byte[] tempYiaddr = new byte[4];
    System.arraycopy( message, i, tempYiaddr,      0, tempYiaddr.length       );
    this.yiaddr.DeSerialize(tempCiaddr);
    i += tempYiaddr.length;
    
    byte[] tempSiaddr = new byte[4];
    System.arraycopy( message, i, tempSiaddr,      0, tempSiaddr.length       );
    this.yiaddr.DeSerialize(tempSiaddr);
    i += tempSiaddr.length;
    
    byte[] tempGiaddr = new byte[4];
    System.arraycopy( message, i, tempGiaddr,      0, tempGiaddr.length       );
    this.giaddr.DeSerialize(tempGiaddr);
    i += tempGiaddr.length;
    
    byte[] tempChaddr = new byte[16];
    System.arraycopy( message, i, tempChaddr,      0, tempChaddr.length       );
    byte[] tempMac = new byte[6];
    System.arraycopy( tempChaddr, i, tempMac,      0, tempMac.length       );
    this.chaddr.DeSerialize(tempMac);
    i += tempChaddr.length;
    
    System.arraycopy( message, i, this.sname,       0, this.sname.length        );
    i += this.sname.length;
    System.arraycopy( message, i, this.file,        0, this.file.length         );
    i += this.file.length;
    System.arraycopy( message, i, this.magicCookie, 0, this.magicCookie.length  );
    i += this.magicCookie.length;
    /* create a new options array with the correct length */
    byte[] tempOptions = new byte[message.length-i];
    System.arraycopy( message, i, tempOptions,      0, tempOptions.length       );
    i += tempOptions.length;
    options = new DHCPOptions(tempOptions);
    
  }
  
  public String toString(){
    return "implement this function for options";
    
  }
  
}
