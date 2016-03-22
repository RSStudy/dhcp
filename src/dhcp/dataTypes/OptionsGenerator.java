package dhcp.dataTypes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import helper.ArrayHelpers;

public class OptionsGenerator {
  static byte[] GenerateOptions(DHCPMessageGeneratorType type) {
    byte[] out = null;

    switch (type) {
    case discover:
      Discover();
      break;
    case offer:
      Offer();
      break;
    case request:
      Request();
      break;
    case ack:
      Ack();
      break;
    }
    return null;

  }

  private static byte[] Discover() {
    
    //ArrayHelpers.concat((new Options()).serialize;, b)
    return null;

  }

  private static byte[] Offer() {
    return null;

  }

  private static byte[] Request() {
    return null;

  }

  private static byte[] Ack() {
    return null;

  }
  

}
