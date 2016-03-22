package helper;

import java.lang.reflect.Array;

public class ArrayHelpers {
  /* 
   * source: http://stackoverflow.com/questions/80476/how-can-i-concatenate-two-arrays-in-java 
   * 
   * author: jeannicolas
   * 
   */
  public static byte[] concat(byte[] a, byte[] b) {
    int aLen = a.length;
    int bLen = b.length;
    byte[] c= new byte[aLen+bLen];
    System.arraycopy(a, 0, c, 0, aLen);
    System.arraycopy(b, 0, c, aLen, bLen);
    return c;
 }
  
  public static String byteArrayToString(byte[] data){
    String out = "";
    for(byte temp: data){
      out +=(temp & 0xFF);
    }
    return out;
  }
}
