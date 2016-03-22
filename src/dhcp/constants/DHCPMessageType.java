package dhcp.constants;
//DHCP Message Type 53 Values
public enum DHCPMessageType {
  DHCPDISCOVER(1), /* this option is implemented */
  DHCPOFFER(2),
  DHCPREQUEST(3),
  DHCPDECLINE(4),
  DHCPACK(5),
  DHCPNAK(6),
  DHCPRELEASE(7),
  DHCPINFORM(8),
  DHCPFORCERENEW(9),
  DHCPLEASEQUERY(10),
  DHCPLEASEUNASSIGNED(11),
  DHCPLEASEUNKNOWN(12),
  DHCPLEASEACTIVE(13),
  DHCPBULKLEASEQUERY(14),
  DHCPLEASEQUERYDONE(15),
  DHCPACTIVELEASEQUERY(16),
  DHCPLEASEQUERYSTATUS(17),
  DHCPTLS(18);

  private byte value;
  DHCPMessageType(int value){
    this.value =(byte)value;
  }
  
  public byte getValue(){
    return value;
  }
}
