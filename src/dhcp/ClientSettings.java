/**
 * 
 */
package dhcp;

import dhcp.ip.dataTypes.IPAddressType;
import helper.Mac;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class ClientSettings {
  private static ClientSettings instance = null;
  public static ClientSettings getInstance(){
    if (instance == null)
      instance = new ClientSettings();
    return instance;
  }
  
  @Getter @Setter IPAddressType ipAddress = null;
  @Getter @Setter IPAddressType router = null;
  @Getter @Setter IPAddressType dhcpServer = null;
  @Getter @Setter IPAddressType[] dnsServer = null;
  @Getter @Setter Mac macAddress = null;
  @Getter @Setter byte[] subnetMask = null;
  @Getter long dhcpLeaseEnd = 0;
  @Getter long dhcpLeaseRenew = 0;
  @Getter long dhcpLeaseTime = 0;
  
  public void setDhcpLeaseTime(long time){
    this.dhcpLeaseTime = time;
    /* calculate the lease end time and add a correction for the data transfer/ processing */
    this.dhcpLeaseEnd = System.currentTimeMillis() + this.dhcpLeaseTime - (long)((double)(0.5*1000.0));
    /* calculate the lease renew time and add a correction for the data transfer/ processing */
    this.dhcpLeaseRenew = System.currentTimeMillis() + (this.dhcpLeaseTime/2) - (long)((double)(0.5*1000.0));
  }
}
