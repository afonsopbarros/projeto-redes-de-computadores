package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;


class BSshutdownHook extends Thread{
  byte[] buf;
  InetAddress address;
  int _CSport;
  int _BSport;
  DatagramSocket socket;

  public BSshutdownHook(byte[] buff, InetAddress ad, int csport, int bsport, DatagramSocket sock){
    buf = buff;
    address = ad;
    _CSport = csport;
    _BSport = bsport;
    socket = sock;
  }

  @Override
  public void run(){
    try{
    System.out.println("Entrou no Hook");
    String message = new String("UNR localhost " + _BSport);
    buf  = message.getBytes("UTF-8");
    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, _CSport);
    socket.send(packet); //envia mensagem de unregister

    /*
    packet = new DatagramPacket(buf, buf.length);
    socket.receive(packet);
    String received = new String(packet.getData(), 0, packet.getLength());
    System.out.println(received); */

    socket.close();
  }catch(Exception e){e.printStackTrace();}
  }
}
