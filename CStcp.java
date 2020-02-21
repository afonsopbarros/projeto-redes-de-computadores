package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class CStcp extends Thread{
  DataInputStream _in; //used to communicate with user
  DataOutputStream _out;
  Socket _socket;
  Map<Integer, String> _login;
  Map<BSPair, BackupInfo> _backuplist;
  DatagramSocket _udpSocket;
  int current_user;
  String current_user_pass;

  public CStcp(Socket socket, DataInputStream in, DataOutputStream out, Map<Integer, String> login, Map<BSPair, BackupInfo> bckplist){
    System.out.println("thread created");
    _socket = socket;
    _in = in;
    _out = out;
    _login = login;
    _backuplist = bckplist;
  }

  @Override
  public void run(){
    String message;
    String response;
    String item;
    boolean exit = false;
    String d = new String();
    String dir = new String();
    BackupInfo ContainsBackup = new BackupInfo();
    boolean exists = false;
    int N;
    InetAddress address;
    DatagramPacket packet;
    byte[] buff;
    String msg = new String();
    String ipBS = new String();
    int portUDP = 0;
    int portBS = 0;
    String send;
    byte[] bytes;
    //Set keyset = new Set();

    try{
      while(exit == false){
          bytes = new byte[1024];
          _in.read(bytes);
          response = new String(bytes, "US-ASCII");
          //reads message from user

          System.out.println("From User:" + response);
          StringTokenizer st = new StringTokenizer(response, " \n");
          item = st.nextToken();

          switch(item){

            case "AUT":
              String username = new String() ;
              String pass = new String();
              username = st.nextToken();
              pass = st.nextToken();

              //verifies login requirements
              if (username.length() > 5  || !(pass.matches("[a-zA-Z0-9]+")) ){
                send = "ERR\n";
                bytes = send.getBytes("US-ASCII");
                _out.write(bytes, 0, bytes.length);
                break;
              }
              try{//verifies username is an integer
                Integer user = Integer.valueOf(username);
              }catch (NumberFormatException e){
                send = "ERR\n";
                bytes = send.getBytes("US-ASCII");
                _out.write(bytes, 0, bytes.length);
                }

              Integer user = Integer.valueOf(username);

              if (_login.containsKey(user)){//user exists

                if(_login.get(user).equals(pass)){
                  send = "AUR OK\n";
                  bytes = send.getBytes("US-ASCII");
                  _out.write(bytes, 0, bytes.length);
                  current_user = user;
                  current_user_pass = pass;
                  System.out.print("User: " + current_user + " \t");
                  break;
                }
                else{

                  send = "AUR NOK\n";
                  bytes = send.getBytes("US-ASCII");
                  _out.write(bytes, 0, bytes.length);
                  exit = true;
                  break;
                }
              }

              else{//creates new user
                _login.put(user, pass);
                send = "AUR NEW\n";
                bytes = send.getBytes("US-ASCII");
                _out.write(bytes, 0, bytes.length);
                exit = true;
                current_user = user;
                current_user_pass = pass;
                System.out.println(_login);
                System.out.println("New user: " + current_user);
                break;
              }
              //user aunthentication


            case "BCK":
              dir = st.nextToken();
              N = Integer.valueOf(st.nextToken());
              ipBS = new String();
              portBS = 0;
              exists = false; //tracks if directory is already backup up
              boolean existsUser = false; //tracks if user is already registered with a BS
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
              ContainsBackup = new BackupInfo();

              for(BackupInfo bi : _backuplist.values()){
                if(bi.containsUser(current_user) == true){
                  existsUser = true;
                  if(bi.containsDirectory(dir, current_user) == true){
                    exists = true;
                    ipBS = bi._ipBS;
                    portBS = bi._portBS;
                    portUDP = bi._portUDP;
                    ContainsBackup = bi;
                  }
                }
              }
              //checks if directory/user exist in a BS

              if(exists == false){ //directory is not backed up anywhere

                List<BSPair> keysAsArray = new ArrayList<BSPair>(_backuplist.keySet());
                Random r = new Random();
                BackupInfo bi = _backuplist.get(keysAsArray.get(r.nextInt(keysAsArray.size())));
                ipBS = bi._ipBS;
                portBS = bi._portBS;
                portUDP = bi._portUDP;
                //selects a random BS

                if(existsUser == false){ //user does not exist in any BS

                  bi.addUser(current_user);
                  //adds user to BS information

                  buff = new byte[512];
                  msg = "LSU "+ current_user + " " + current_user_pass+"\n";
                  buff  = msg.getBytes("US-ASCII");
                  address = InetAddress.getByName(ipBS);
                  _udpSocket = new DatagramSocket(portUDP, address);
                  packet = new DatagramPacket(buff, buff.length, address, portUDP);
                  _udpSocket.send(packet);
                  //Registers User with BS

                }

                bi.addDirectory(dir, current_user);
                //adds directory to BS information

                for(int i=0; i<N; i++){
                  String name = st.nextToken();
                  LocalDateTime datetime = LocalDateTime.parse(st.nextToken() + " " + st.nextToken(), formatter);
                  long size = Long.parseLong(st.nextToken());
                  FileInfo file = new FileInfo(dir, name, datetime, size);
                  //file parsing

                  bi.addFile(dir, file, current_user);
                }
                //adds files to BS information

                System.out.println("BCK " + current_user + " " + dir + " " + ipBS + " " + portBS);
                send = "BKR " + ipBS + " " + portBS + response.substring(4 + dir.length()) + "\n";
                bytes = send.getBytes("US-ASCII");
                _out.write(bytes, 0, bytes.length);
                //replies to the user

              }

              else{   //directory exists in BS

                for(int i=0; i<N; i++){
                  String name = st.nextToken();
                  LocalDateTime datetime = LocalDateTime.parse(st.nextToken() + " " + st.nextToken(), formatter);
                  long size = Long.parseLong(st.nextToken());
                  FileInfo file = new FileInfo(dir, name, datetime, size);
                  System.out.println("File Info parsed:" + file);
                  ContainsBackup.updateFile(dir, file, current_user);
                }
                //updates BS information with what the User sent, so we can later compare it to what the BS contains

                buff = new byte[512];
                msg = "LSF "+ current_user + " " + dir+ "\n";
                buff  = msg.getBytes("US-ASCII");
                address = InetAddress.getByName(ipBS);
                _udpSocket = new DatagramSocket(portUDP, address);
                packet = new DatagramPacket(buff, buff.length, address, portUDP);
                _udpSocket.send(packet);
                //sends LSF request to BS (is awnsered in CSudp.java)

                ServerSocket server = new ServerSocket(1234);
                Socket tcpSocket = server.accept();
                DataInputStream in2 = new DataInputStream(tcpSocket.getInputStream());
                String LFD = in2.readUTF();
                server.close();
                tcpSocket.close();
                in2.close();
                //CSudp.java receives the LFD reply and sends it to this thread by TCP

                String BKR = new String();
                N = 0;
                StringTokenizer st1 = new StringTokenizer(LFD, " \n");
                int nmax = Integer.valueOf(st1.nextToken());


                for(int i=0; i<nmax; i++){
                  String filei = new String();
                  String name = st1.nextToken();
                  String dateF = st1.nextToken();
                  String timeF = st1.nextToken();
                  LocalDateTime datetime = LocalDateTime.parse(dateF + " " + timeF, formatter);
                  long size = Long.parseLong(st1.nextToken());
                  FileInfo file = new FileInfo(dir, name, datetime, size);
                  //file parsing
                  if( ContainsBackup._users.get(current_user).get(dir).get(name).compareTo(file) == 1 ){
                    BKR = BKR + name + " " + dateF + " " + timeF + " " + size + " ";
                    N++;
                  } //compares the DateTime from the User with what the BS has, counts number of files
                }

                BKR = "BKR " + ContainsBackup._ipBS + " " + ContainsBackup._portBS + " " + N + " " + BKR + "\n";

                send = BKR;
                bytes = send.getBytes("US-ASCII");
                _out.write(bytes, 0, bytes.length);
                //replies to the user
              }
              exit = true;
              break;


          case "RST":
            exists = false;
            dir = st.nextToken();
            ContainsBackup = new BackupInfo();
            exists= false;

            for(BackupInfo bi : _backuplist.values()){
              if(bi.containsDirectory(dir, current_user) == true){
                ipBS = bi._ipBS;
                portBS = bi._portBS;
                portUDP = bi._portUDP;
                ContainsBackup = bi;
                exists = true;
              }
            }
            //searches for directory in available BSs

            if(exists == true){
              send = "RSR " + ipBS + " " + portBS + "\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }

            else{
              send = "RSR EOF\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }
            exit = true;
            break;

          case "LSD":
            exists = false;
            for(BackupInfo bi : _backuplist.values()){
              if(bi.containsUser(current_user) == true){
                  exists = true;
                  ipBS = bi._ipBS;
                  portBS = bi._portBS;
                  portUDP = bi._portUDP;
                  ContainsBackup = bi;
              }
            }

            if(exists == true){
              Set keyset = ContainsBackup._users.get(current_user).keySet(); //set of directories backed up by user
              N = keyset.size(); //number of directories
              Object[] dirs = keyset.toArray(); //array of directories
              String LDR = "LDR " + N;

              for(int i = 0; i < N; i++){
                LDR = LDR + " " + dirs[i];
              }
              send = LDR + "\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }

            else{
              send = "LDR 0\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }
              exit=true;
              break;

          case "LSF":
            exists = false;
            dir = st.nextToken();
            ContainsBackup = new BackupInfo();
            exists= false;

            for(BackupInfo bi : _backuplist.values()){
              if(bi.containsDirectory(dir, current_user) == true){
                ipBS = bi._ipBS;
                portBS = bi._portBS;
                portUDP = bi._portUDP;
                ContainsBackup = bi;
                exists = true;
              }
            }

            buff = new byte[512];
            msg = "LSF "+ current_user + " " + dir+ "\n";
            buff  = msg.getBytes("US-ASCII");
            address = InetAddress.getByName(ipBS);
            _udpSocket = new DatagramSocket(portUDP, address);
            packet = new DatagramPacket(buff, buff.length, address, portUDP);
            _udpSocket.send(packet);
            //sends LSF request to BS (is awnsered in CSudp.java)

            ServerSocket server = new ServerSocket(1234);
            Socket tcpSocket = server.accept();
            DataInputStream in2 = new DataInputStream(tcpSocket.getInputStream());
            String LFD = in2.readUTF();
            server.close();
            tcpSocket.close();
            in2.close();
            //CSudp.java receives the LFD reply and sends it to this thread by TCP

            send = "LFD " + ipBS + " " + portBS + LFD;
            bytes = send.getBytes("US-ASCII");
            _out.write(bytes, 0, bytes.length);

            exit=true;
            break;

          case "DEL":
            exists = false;
            dir = st.nextToken();
            ContainsBackup = new BackupInfo();
            exists= false;

            for(BackupInfo bi : _backuplist.values()){
              if(bi.containsDirectory(dir, current_user) == true){
                ipBS = bi._ipBS;
                portBS = bi._portBS;
                portUDP = bi._portUDP;
                ContainsBackup = bi;
                exists = true;
              }
            }

            if(exists == true){
              buff = new byte[512];
              msg = "DLB "+ current_user + " " + dir + "\n";
              buff  = msg.getBytes("US-ASCII");
              address = InetAddress.getByName(ipBS);
              _udpSocket = new DatagramSocket(portUDP, address);
              packet = new DatagramPacket(buff, buff.length, address, portUDP);
              _udpSocket.send(packet);
              //sends DLB request to BS

              ContainsBackup.removeDirectory(dir, current_user);

              send = "DBR OK\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }

            else{
              send = "DBR NOK\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }

            exit=true;
            break;

          case "DLU":
            exists = false;
            for(BackupInfo bi : _backuplist.values()){
              if(bi.containsUser(current_user) == true){
                  exists = true;
                  ipBS = bi._ipBS;
                  portBS = bi._portBS;
                  portUDP = bi._portUDP;
                  ContainsBackup = bi;
              }
            }
            if(exists == false){
              _login.remove(current_user);
              send = "DLR OK\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }
            else{
              send = "DLR NOK\n";
              bytes = send.getBytes("US-ASCII");
              _out.write(bytes, 0, bytes.length);
            }

            exit=true;
            break;

          case "EXIT":
            exit = true;
            break;

          default:
            exit = true;
            break;
        }
      }
      _socket.close();
      _in.close();
      _out.close();
    }catch (Exception e){e.printStackTrace();}
  }
}
