package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

public class CSudp extends Thread{
  Map<BSPair, BackupInfo> backuplist;
  DatagramSocket udpSocket;
  DataOutputStream out2;

  public CSudp(Map<BSPair, BackupInfo> bckplist, DatagramSocket sock){
    backuplist = bckplist;
    udpSocket = sock;
  }

  @Override
  public void run(){
    try{
    String item = new String();
    String ipBS = new String();
    int portBS;

    while(true){
        byte[] buff = new byte[512];
        String message = new String();

        //receiving message from BS
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        udpSocket.receive(packet);
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("From BS " + received);

        //splitting string
        StringTokenizer st = new StringTokenizer(received, " \n");
        item = st.nextToken(); //REG
        switch(item){
          case "REG":
          //adding new BS to list
          ipBS = st.nextToken();
          portBS = Integer.parseInt(st.nextToken());
          BackupInfo bs = new BackupInfo(ipBS, portBS, port);
          backuplist.put(new BSPair(ipBS,portBS), bs);
          System.out.println("+BS: " + ipBS + " " + portBS);
          //resposta
          if(backuplist.size() < 20)
            message = new String("RGR OK\n");
          else
            message = new String("RGR NOK\n");
          buff  = message.getBytes("US-ASCII");
          packet = new DatagramPacket(buff, buff.length, address, port);
          udpSocket.send(packet);
          break;

          case "UNR":
            ipBS = st.nextToken();
            portBS = Integer.parseInt(st.nextToken());
            backuplist.remove(new BSPair(ipBS, portBS));
            message = new String("UAR OK\n");
            buff  = message.getBytes("US-ASCII");
            packet = new DatagramPacket(buff, buff.length, address, port);
            udpSocket.send(packet);
            System.out.println("-BS: " + ipBS + " " + portBS);
            break;

          case "LFD":
            InetAddress host = InetAddress.getByName("localhost");
            Socket socket = new Socket(host,1234);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(received.substring(4));
            //receives LFD from BS, sends it to CStcp via a TCP conection on port 1234
            break;
        }

    }
    }catch(Exception e){e.printStackTrace();}
  }
}
