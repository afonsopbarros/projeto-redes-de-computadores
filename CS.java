package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

public class CS{
    public static void main(String[] args) throws IOException{
    try{
      int port = 58011;
      /*
      if(args[0].equals("-p"))
        port = Integer.valueOf(args[1]);*/

      //System.out.println(port);

      Map<Integer, String> login = new HashMap<Integer, String>(); //stores login information
      HashMap<BSPair, BackupInfo> backuplist = new HashMap<BSPair, BackupInfo>(); //stores information on BS
      ServerSocket server = new ServerSocket();//TCP socket
      InetSocketAddress endPoint = new InetSocketAddress("localhost", 58011);
      int waitQueueSize = 100;
      server.bind(endPoint, waitQueueSize);
      DatagramSocket udpSocket = new DatagramSocket(59000);

      Thread thread = new CSudp(backuplist, udpSocket);
      thread.start();

      TCPhandler tcp = new TCPhandler(server, login, backuplist);

      //tcp.close() quando fechamos a consola
  }catch(Exception e){e.printStackTrace();}
  }
}
