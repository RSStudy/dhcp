package dhcp.ip;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import dhcp.ip.dataTypes.IPAddressType;
import helper.Mac;
import lombok.Getter;

public class IPPool {
  private List<IPAddressType> addressList;
  private Map<Mac,IpTime> userList;
  private static IPPool instance = null;
  @Getter private IPAddressType router = null;
  public static IPPool newInstance()
  {
    byte[] start = new byte[]{(byte)192, (byte)168, (byte)1, (byte)1};
    byte[] stop = new byte[]{(byte)192, (byte)168, (byte)2, (byte)255};
    return instance = new IPPool(start, stop);
  }
  public static IPPool newInstance(byte[] start, byte[] stop){
    if (start.length == 4 && stop.length == 4)
      return instance = new IPPool(start, stop);
    return null;
    
  }
  public static IPPool getInstance(){
    if (instance == null)
      newInstance();
    return instance;
  }
  
  public IPAddressType reserveAddress(Mac mac, byte[] reqIp) {
    int index = -1;
    IPAddressType address = null;
    for (IPAddressType tempAddress: addressList)
      if (tempAddress.equals(reqIp)){
        index = addressList.indexOf(tempAddress);
      }
      if (index != -1)
        address = addressList.remove(index);
      if (address != null){
        IpTime ipTime = new IpTime();
        ipTime.time = System.currentTimeMillis() + 30 * 1000;
        ipTime.ip = address;
        
        userList.put(mac, ipTime);
        return address;
      }
      return reserveAddress(mac);
  }
  
  public IPAddressType reserveAddress(Mac mac){
    IPAddressType address = null;
    if (addressList.size() == 0)
      clearUserList();
    if (addressList.size() > 0)
      address = addressList.remove(0);
    if (address != null){
      IpTime ipTime = new IpTime();
      ipTime.time = System.currentTimeMillis() + 30 * 1000;
      ipTime.ip = address;
      
      userList.put(mac, ipTime);
    }
    return address;
  }
  
  public IPAddressType ExtendLease(Mac mac, IPAddressType ipaddress, long time){
    IpTime ipTime = userList.get(mac);
    if (ipTime != null){
      if(ipTime.ip.equals(ipaddress)){
        ipTime.time = System.currentTimeMillis() + time;
        return ipaddress;
      }else
        return null;
    } else {
      int index = -1;
      for (IPAddressType tempAddress: addressList)
        if (tempAddress.equals(ipaddress)){
          index = addressList.indexOf(tempAddress);
          break;
        }
        if (index != -1){
          addressList.remove(index);
          IpTime tempIpTime = new IpTime();
          tempIpTime.ip = ipaddress;
          tempIpTime.time = System.currentTimeMillis() + time;
          userList.put(mac, ipTime);
          return ipaddress;
        }
    }
    
    return null;
    
  }
  /**
   * checks if the start and the stop are in the right order;
   * if not switches them
   * 
   * fills the addressList with all addresses between start and stop
   * @param start a byte[] with length 4
   * @param stopa byte[] with length 4
   */
  private IPPool(byte[] start, byte[] stop){
    try {
      router  = new IPAddressType(InetAddress.getLocalHost().getAddress());
    } catch (UnknownHostException e) {
      router = new IPAddressType(new byte[]{0,0,0,0});
    }
    addressList = new ArrayList<IPAddressType>();
    userList = new HashMap<Mac,IpTime>();
    
    int vars = 0; /* the amount of variables in the array */
    for (int i = 0; i <4; i++){
      /* if the start address is small then the stop address switch them */
      if ((int)(start[i]& 0xff) > (int)(stop[i]& 0xff)){
        byte[] temp = start;
        start = stop;
        stop = temp;
        vars = 4 - i;
        break;
      }else if((int)(stop[i]& 0xff) > (int)(start[i]& 0xff)){
        vars = 4 - i;
        break;
      }
    }
    if (vars == 0)
      addressList.add(new IPAddressType(start));
    else {
      
      /* 
       * convert the arrays to a long using a bitmask and bitshifting to make life easier 
       * the bitmask 0xFF is used to convert the negative numbers to positive numbers (unsigned to signed conversion in C)
       */
      
      long startAddr = byteArrayToLong(start);
      long stopAddr = byteArrayToLong(stop);
      
      /* create the addresses */
      for (long i =startAddr; i < stopAddr; i++){
        byte[] addr = longToByteArray(i);
        

        /* 
         * check if the address is not a reserved address 
         * if not add it to the address list
         * */
        if ((addr[3]&0xFF) != 0  && (addr[3]&0xFF) != 255)
          addressList.add(new IPAddressType(addr));
      }
    }
  }
  private long byteArrayToLong(byte[] in){
    long out = 0;
    for (int i=0; i<4; i++) {
      out += (((long)in[i] &0xFF) << 8*(3-i));
    }
    return out;
  }
  
  private byte[] longToByteArray(long in){
    byte[] out = new byte[4];
    /* reconstruct the byte array */
    for (int j = 3; j >=0; j--){
      out[j] = (byte)((in >> 8*(3-j)) &0xFF);
    }
    return out;
  }
  public String toString(){
    String out = "----------------------------\r\n";
           out +="----------IP Pool:----------\r\n";
           out +="----------------------------\r\n";
    for(IPAddressType addr: addressList)
      out += addr + "\r\n";
    
    out +="----------------------------\r\n";
    out +="-----reserved addresses-----\r\n";
    out +="----------------------------\r\n";
    Iterator<Entry<Mac, IpTime>> it = userList.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Mac, IpTime> pair = (Map.Entry<Mac, IpTime>)it.next();
        out += "ip: " + pair.getValue().ip.toString() + ", time left: " + (pair.getValue().time - System.currentTimeMillis())/1000 + " seconds, mac: " + pair.getKey().toString();
        it.remove(); // avoids a ConcurrentModificationException
    }
    return out;
  }

  public void clearUserList() {
    /* 
     * this function is based on the post of karmin79
     * http://stackoverflow.com/questions/1066589/iterate-through-a-hashmap
     */
    Iterator<Entry<Mac, IpTime>> it = userList.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry<Mac, IpTime> pair = (Map.Entry<Mac, IpTime>)it.next();
        if (pair.getValue().time < System.currentTimeMillis()){
          userList.remove(pair.getKey());
          addressList.add(pair.getValue().ip);
        }
        it.remove(); // avoids a ConcurrentModificationException
    }
  }
  
  private class IpTime{
    public IPAddressType ip;
    public long time;
  }

  public byte[] getSubnetMask() {
    
    return new byte[]{(byte)255, (byte)255, (byte)255, (byte)0};
  }
  public IPAddressType getServerIp() {
    // TODO Auto-generated method stub
    return new IPAddressType(new byte[]{(byte)192,(byte)168,(byte)1,(byte)1});
  }


}
