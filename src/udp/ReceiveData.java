package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.List;
import java.util.Vector;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class ReceiveData implements Runnable {

  private Thread receiveThread = null;
  private int port = 0;
  private int packageSize = 0;

  private Mutex receiveMutex = null;
  private List<byte[]> reveivedData = null;

  private Mutex stopMutex = null;
  private boolean stopThread = false;

  public ReceiveData(){
    this(8001);
  }
  public ReceiveData(int port){
    this(port, 1024);
  }
  public ReceiveData(int port, int packageSize){
    this.port = port;
    this.packageSize = packageSize;
  }
  
  public void start(){

    receiveMutex = new Mutex();
    reveivedData = new Vector<byte[]>();

    stopMutex = new Mutex();
    stopThread = false;

    receiveThread = new Thread(this);
    receiveThread.setDaemon(true);
    
    receiveThread.start();
  }

  /**
   * Tells the receiveThread that it should stop working.
   * @throws InterruptedException
   */
  public void stop() throws InterruptedException{
    if (receiveThread == null | stopMutex == null){
      System.err.println("The thread is not properly initialised so it cant be stopped");
      return;
    }
    stopMutex.acquire();
    try {
      stopThread = true;;
    } finally {
      stopMutex.release();
    }
  }

  @Override
  public void run() {
    System.out.println("thread running");
    DatagramSocket socket = null;
    try {
      socket = new DatagramSocket(this.port);

      byte[] dataIn = new byte[packageSize];
      boolean run = true;
      while (run){
        /* 
         * check if there is data in the receive buffer
         * if there is get it else sleep for 200ms to prevent that
         * shocket.receive blocks the thread
         */
          DatagramPacket inPacket = new DatagramPacket(dataIn, dataIn.length);
          /* WARNING: this function blocks until data is received */
          socket.receive(inPacket);
          String message = new String (inPacket.getData());
          System.out.println(message);

        /* Check if the thread should stop running */
        stopMutex.acquire();
        try {
          run = !stopThread;
        } finally {
          stopMutex.release();
        }
      }
      System.out.println("thread stopping");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }



}
