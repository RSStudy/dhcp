package dhcp.constants;
/* 
 * list of all option codes
 * http://www.iana.org/assignments/bootp-dhcp-parameters/bootp-dhcp-parameters.xhtml
 * */

public enum OptionCodes{
  SubnetMask(1),
  Router(3),
  DomainServer(6),
  DomainName(15),
  AddressRequest(50),
  AddressTime(51),
  DhcpMessageType(53),
  DhcpServerId(54),
  ParameterList(55)
  ;
  private byte value;
  OptionCodes(int value){
    this.value = (byte)value;
  }
  public byte getValue(){
    return value;
  }
}
