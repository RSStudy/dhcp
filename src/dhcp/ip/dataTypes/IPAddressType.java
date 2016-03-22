package dhcp.ip.dataTypes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import lombok.Getter;

public class IPAddressType {
  
  @Getter private byte[] address;
  public IPAddressType(){
    
  }
  public IPAddressType(byte[] start) {
    setAddress(start);
  }

  public IPAddressType(IPAddressType clone) {
    this.address = clone.address;
  }
  public int setAddress(byte[] address){
    if (address.length != 4)
      return 1;
    this.address = address;
    return 0;
  }
  
  public boolean equals(IPAddressType ipaddress){
    return Arrays.equals(address,ipaddress.getAddress() );
  }
  
  public String toString(){
    try {
      InetAddress addr = InetAddress.getByAddress(this.address);
      return addr.toString().replace("/", "");
    } catch (UnknownHostException e) {
      return null;
    }
  }
  public byte[] Serialize() {
    
    return address;
  }
  public void DeSerialize(byte[] data) {
    this.address = data;
    
  }
}
