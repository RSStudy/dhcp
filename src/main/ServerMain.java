/**
 * 
 */
package main;

import udp.ReceiveData;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class ServerMain {

  /**
   * @param args
   * @throws InterruptedException 
   */
  public static void main(String[] args) throws InterruptedException {
    ReceiveData data = new ReceiveData();
    data.start();
    Thread.sleep(5000);
    data.stop();
    Thread.sleep(5000);
    System.err.println("program end");
    while(true);
  }

}
