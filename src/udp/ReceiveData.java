package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

import helper.ThreadEvent;

/**
 * @author ing. R.J.H.M. Stevens
 *
 */
public class ReceiveData implements Runnable {

  private Thread receiveThread = null;
  private ThreadEvent event = new ThreadEvent();
  private int port = 0;
  private int packageSize = 0;

  private Mutex receiveMutex = null;
  private List<DatagramPacket> reveivedData = new LinkedList<DatagramPacket>();

  /**
   * default constructor
   */
  public ReceiveData() {
    this(8001);
  }

  public ReceiveData(int port) {
    this(port, 1024);
  }

  public ReceiveData(int port, int packageSize) {
    this.port = port;
    this.packageSize = packageSize;
    this.receiveMutex = new Mutex();
  }
  ////////////////////////////////////////////////

  public DatagramPacket getDatagramPacket() {
    DatagramPacket out = null;
    while (out == null) {
      try {
        receiveMutex.acquire();
        if (!reveivedData.isEmpty())
          out = reveivedData.remove(0);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        receiveMutex.release();
      }
      if (out == null) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
    return out;
  }

  public void start() {

    receiveMutex = new Mutex();
    reveivedData = new Vector<DatagramPacket>();

    receiveThread = new Thread(this);
    receiveThread.setDaemon(true);

    receiveThread.start();
  }

  @Override
  public void run() {
    System.out.println("thread running");
    DatagramSocket socket = null;
    try {
      socket = new DatagramSocket(this.port);

      byte[] dataIn = new byte[packageSize];
      while (true) {
        /*
         * check if there is data in the receive buffer if there is get it else
         * sleep for 200ms to prevent that shocket.receive blocks the thread
         */
        DatagramPacket inPacket = new DatagramPacket(dataIn, dataIn.length);
        /* WARNING: this function blocks until data is received */
        socket.receive(inPacket);
        System.out.println("data received");
        try {
          receiveMutex.acquire();
          reveivedData.add(inPacket);
        } catch (InterruptedException e) {
          e.printStackTrace();
        } finally {
          receiveMutex.release();
        }

        /* Check if the thread should stop running */

      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

// stopMutex.acquire();
// try {
// run = !stopThread;
// } finally {
// stopMutex.release();
// }
