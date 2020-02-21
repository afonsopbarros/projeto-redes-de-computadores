package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

/* As comunicacoes em UDP sao feitas atraves de DatagramPackets, que sao arrays de bytes enviados atraves de
  DatagramSockets */
public class BS{

  public static void main(String[] args) throws IOException{

    int _BSport = 4567;
    String _CSname = "localhost";
    int _CSport = 5678;
    InetAddress address = InetAddress.getByName("localhost");
    DatagramSocket socket = new DatagramSocket(4567);
    byte[] buf = new byte[512];

    Runtime.getRuntime().addShutdownHook(new BSshutdownHook(buf, address, _CSport, _BSport, socket));

    String message = new String("REG localhost " + _BSport);
    buf  = message.getBytes("UTF-8");
    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, _CSport);
    socket.send(packet); //envia mensagem de registo ao CS

    packet = new DatagramPacket(buf, buf.length);
    socket.receive(packet);
    String received = new String(packet.getData(), 0, packet.getLength());
    System.out.println(received);

    while(true){
      buf = new byte[512];
      packet = new DatagramPacket(buf, buf.length);
      socket.receive(packet);
      received = new String(packet.getData(), 0, packet.getLength());
      StringTokenizer st = new StringTokenizer(received, " \n");
      String item = st.nextToken(); //REG
      switch(item){
        case "LSU":
        message = "LUR OK";
        buf = message.getBytes("UTF-8");
        packet = new DatagramPacket(buf, buf.length, address, _CSport);
        socket.send(packet);
          break;
        case "LSF":
          message = "LFD 2 crl.txt 02.10.2018 14:50:26 7 fds.txt 03.10.2018 14:50:34 4";
          buf = message.getBytes("UTF-8");
          packet = new DatagramPacket(buf, buf.length, address, _CSport);
          socket.send(packet);
          break;
    }

  }
}
}



//Nota
/*In general, UTF-16 is usually better for in-memory representation because BE/LE (qualquer coisa relacionada com os bits mais/menos significativos)
is irrelevant there (just use native order) and indexing is faster.
UTF-8, on the other hand, is extremely good for text files and -> network protocols <- because
there is no BE/LE issue and null-termination often comes in handy, as well as ASCII-compatibility.*/
