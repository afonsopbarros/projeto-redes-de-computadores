package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

public class User{
  private DataInputStream in;
  private DataOutputStream out;
  private Socket socket;
  private int port;
  private String hostname;

  public User(int prt, String hstname){
    try{
    port = 58011;
    hostname = hstname;
    InetAddress host = InetAddress.getByName(hostname);
    socket = new Socket(host, port);
    in = new DataInputStream(socket.getInputStream());
    out = new DataOutputStream(socket.getOutputStream());
    }catch(Exception e){e.printStackTrace();}
  }

  public String Communicate(String message) throws IOException{
    try{
    String response = new String();
    String item = new String();
    boolean exit = false;

    while(exit == false){
      StringTokenizer st = new StringTokenizer(message, " \n");
      item = st.nextToken();
      switch(item){
        case "AUT":
          //System.out.println(message + "msg User");
          out.writeUTF(message);

          //clears stringtokenizer
          st.nextToken();
          st.nextToken();

          response = in.readUTF();
          System.out.println("From CS:" + response);
          return response; //returns to App


        case "BCK":
          out.writeUTF(message);
          response = in.readUTF();
          System.out.println("From CS:" + response);
          return response; //returns to App

        case "RST":
          out.writeUTF(message);
          response = in.readUTF();
          System.out.println("From CS:" + response);
          return response; //returns to App

        case "EXIT":
          out.writeUTF(message);
          //socket.close();
          exit = true;
          break;

        default:
          //something is wrong
          out.writeUTF("ERR");
          exit = true;
          break;
      }
    }
    }catch(Exception e){e.printStackTrace();}
    return "0";
  }

  public  void Close() throws IOException{
    socket.close();
    in.close();
    out.close();
  }
}
