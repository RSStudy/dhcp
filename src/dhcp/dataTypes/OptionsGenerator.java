package dhcp.dataTypes;

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

  private static void Discover() {

  }

  private static void Offer() {

  }

  private static void Request() {

  }

  private static void Ack() {

  }
}
