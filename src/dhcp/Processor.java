package dhcp;

import java.util.Arrays;

import dhcp.constants.DHCPMessageType;
import dhcp.constants.OptionCodes;
import dhcp.dataTypes.DCHPMessage;
import dhcp.ip.IPPool;
import dhcp.ip.dataTypes.IPAddressType;

public class Processor {
  public Processor(){
    
  }
  
  public static int Process(DCHPMessage message){
    byte code = OptionCodes.DhcpMessageType.getValue();
    byte[] content = message.getOptions().getContentFromCode(code);
    /* a dhcp message type is always one byte */
    if (content == null){
      System.err.println("The option field doesnt have a dhcpMessagetype");
    }
    else if (content[0] == DHCPMessageType.DHCPDISCOVER.getValue()){
      /*Revived a DHCPDISCOVER message so we have to create an offer message */
      /* this is a server message */
      
      IPPool ipPool =  IPPool.getInstance();
      IPAddressType assignedaddr;
      
      /* get the required field from the message */
      code = OptionCodes.AddressRequest.getValue();
      byte[] reqIp = message.getOptions().getContentFromCode(code); 
      code = OptionCodes.ParameterList.getValue();
      byte[] reqParam = message.getOptions().getContentFromCode(code); 
      
      /* request an ip address */
      if (reqIp != null){
        assignedaddr = ipPool.reserveAddress(message.getChaddr(), reqIp);
      }
      else
        assignedaddr = ipPool.reserveAddress(message.getChaddr());
      
      /* check if there was an IP address available for the client */
      if (assignedaddr == null) 
        return 1; 
      message.getOptions().emptyOptions();
      message.setSiaddr(ipPool.getServerIp());
      message.setYiaddr(assignedaddr);
      message.getOptions().addOption(OptionCodes.DhcpMessageType.getValue(),DHCPMessageType.DHCPOFFER.getValue());
      for (byte param: reqParam){
        if (param == OptionCodes.SubnetMask.getValue()){
          message.getOptions().addOption(OptionCodes.SubnetMask.getValue(), ipPool.getSubnetMask());
          
        }else if (param == OptionCodes.Router.getValue()){
          message.getOptions().addOption(OptionCodes.Router.getValue(), ipPool.getRouter().Serialize());
        }else if (param == OptionCodes.DomainServer.getValue()){
          /* 
           * for this example we use the google dns servers 
           * note every address is 4 bytes 
           * */
          byte[] dns = new byte[]{(byte)8, (byte)8, (byte)8, (byte)8,     (byte)8, (byte)8, (byte)4, (byte)4};
          message.getOptions().addOption(OptionCodes.DomainServer.getValue(), dns);
        }
          
      }
      
      message.setOp((byte)2);
      message.setFlags(new byte[]{0,0});
      
      message.setYiaddr(assignedaddr);
      return 0;
    
    }
    else if (content[0] == DHCPMessageType.DHCPOFFER.getValue()){
      ClientSettings settings = ClientSettings.getInstance();
      /*offer state */
      /*this is a client messge */
      code = OptionCodes.AddressRequest.getValue();
      byte[] assIp = message.getOptions().getContentFromCode(code); 
      if (assIp == null)
        return -2;
      settings.setIpAddress(new IPAddressType(assIp));
      
      code = OptionCodes.SubnetMask.getValue();
      byte[] subnetMask = message.getOptions().getContentFromCode(code); 
      if (subnetMask != null)
        settings.setSubnetMask(subnetMask);
      
      code = OptionCodes.Router.getValue();
      byte[] router = message.getOptions().getContentFromCode(code); 
      if (router != null){
        settings.setRouter(new IPAddressType(router));
      }
      code = OptionCodes.DomainServer.getValue();
      byte[] dns = message.getOptions().getContentFromCode(code); 
      if (dns != null){
        int l = dns.length/4;
        IPAddressType[] dnsServers = new IPAddressType[l];
        for (int i =0; i <l; i ++)
          dnsServers[i] = new IPAddressType(Arrays.copyOfRange(dns, i*4, 4));
        settings.setDnsServer(dnsServers);
      }
      message.getOptions().emptyOptions();
      message.setOp((byte)1);
      message.setFlags(new byte[]{0,0});
      message.setYiaddr(new IPAddressType(new byte[]{0,0,0,0}));
      message.getOptions().addOption(OptionCodes.DhcpMessageType.getValue(),DHCPMessageType.DHCPREQUEST.getValue());
      message.getOptions().addOption(OptionCodes.AddressRequest.getValue(),settings.getIpAddress().Serialize());
      message.getOptions().addOption(OptionCodes.DhcpServerId.getValue(),message.getSiaddr().Serialize());
      
      return 0;
      
    }
    else if (content[0] == DHCPMessageType.DHCPREQUEST.getValue()){
      int leaseTime = 60; // leasetime in seconds
      /*request state */
      /* this is a server message */
      IPPool ipPool =  IPPool.getInstance();
      IPAddressType assignedaddr;
      
      code = OptionCodes.AddressRequest.getValue();
      byte[] tempaddress = message.getOptions().getContentFromCode(code); 
      assignedaddr = new IPAddressType(tempaddress);
      
      IPAddressType responce = ipPool.ExtendLease(message.getChaddr(), assignedaddr, ((long)leaseTime * (long)1000));
      message.getOptions().emptyOptions();
      message.setOp((byte)2);
      message.setFlags(new byte[]{0,0});
      message.setYiaddr(new IPAddressType(new byte[]{0,0,0,0}));
      message.setSiaddr(ipPool.getServerIp());
      
      if (responce != null){
        message.getOptions().addOption(OptionCodes.DhcpMessageType.getValue(),DHCPMessageType.DHCPACK.getValue());
        message.getOptions().addOption(OptionCodes.SubnetMask.getValue(), ipPool.getSubnetMask());
        message.getOptions().addOption(OptionCodes.Router.getValue(), ipPool.getRouter().Serialize());
        byte[] dns = new byte[]{(byte)8, (byte)8, (byte)8, (byte)8,     (byte)8, (byte)8, (byte)4, (byte)4};
        message.getOptions().addOption(OptionCodes.DomainServer.getValue(), dns);
        message.getOptions().addOption(OptionCodes.DhcpServerId.getValue(), ipPool.getServerIp().Serialize());
        message.getOptions().addOption(OptionCodes.AddressTime.getValue(), longToByteArray(leaseTime));
        message.setYiaddr(assignedaddr);
      }
      else
        message.getOptions().addOption(OptionCodes.DhcpMessageType.getValue(),DHCPMessageType.DHCPNAK.getValue());
      
    }
    else if (content[0] == DHCPMessageType.DHCPACK.getValue()){
      /*ack state */
      ClientSettings settings = ClientSettings.getInstance();
      /*offer state */
      /*this is a client messge */
      
      code = OptionCodes.SubnetMask.getValue();
      byte[] subnetMask = message.getOptions().getContentFromCode(code); 
      if (subnetMask != null)
        settings.setSubnetMask(subnetMask);
      
      code = OptionCodes.Router.getValue();
      byte[] router = message.getOptions().getContentFromCode(code); 
      if (router != null){
        settings.setRouter(new IPAddressType(router));
      }
      code = OptionCodes.DomainServer.getValue();
      byte[] dns = message.getOptions().getContentFromCode(code); 
      if (dns != null){
        int l = dns.length/4;
        IPAddressType[] dnsServers = new IPAddressType[l];
        for (int i =0; i <l; i ++)
          dnsServers[i] = new IPAddressType(Arrays.copyOfRange(dns, i*4, 4));
        settings.setDnsServer(dnsServers);
      }
      
      code = OptionCodes.DhcpServerId.getValue();
      byte[] dhcpServer = message.getOptions().getContentFromCode(code); 
      if (dhcpServer != null){
        settings.setDhcpServer(new IPAddressType(dhcpServer));
      }
      
      code = OptionCodes.AddressTime.getValue();
      byte[] time = message.getOptions().getContentFromCode(code); 
      if (time != null){
        settings.setDhcpLeaseTime(byteArrayToLong(time));
      }
      
      
    }
    else if (content[0] == DHCPMessageType.DHCPNAK.getValue()){
      /*nack state */
      return -2;
    }
    return -1;
  }
  
  private static byte[] longToByteArray(long in){
    byte[] out = new byte[4];
    /* reconstruct the byte array */
    for (int j = 3; j >=0; j--){
      out[j] = (byte)((in >> 8*(3-j)) &0xFF);
    }
    return out;
  }
  
  private static long byteArrayToLong(byte[] in){
    long out = 0;
    for (int i=0; i<4; i++) {
      out += (((long)in[i] &0xFF) << 8*(3-i));
    }
    return out;
  }
}
