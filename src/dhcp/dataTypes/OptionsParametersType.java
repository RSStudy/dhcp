package dhcp.dataTypes;

public enum OptionCodes{
  SubnetMask(1),
  Router(3),
  DomainServer(6),
  AddressRequest(50),
  AddressTime(51),
  DhcpMessageType(53),
  DhcpServerId(54),
  ParameterList(55)
  ;
  private byte value;
  OptionsParametersType(int value){
    this.value = (byte)value;
  }
  byte getValue(){
    return value;
  }
}
