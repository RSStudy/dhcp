package dhcp.dataTypes;

public enum HType {
  Ethernet(1), 
  IEEE802(6), 
  ARCNET(7), 
  LoaclTalk(11), 
  LocalNet(12), 
  SMDS(14), 
  FrameReplay(15), 
  ATM(16), 
  HDLC(17), 
  FiberChannel(18), 
  ATM19(19), 
  SerialLine(20);

  private final byte value;
  HType(int value) {
    this.value = (byte)value;
  }

  public byte getValue() {
    return this.value;
  }
}
