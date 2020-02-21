package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

public class TCPhandler{
	boolean exit;
	ServerSocket server;
	Map<Integer, String> login;
	Map<BSPair, BackupInfo> backuplist;
	InputStream pin;

	public TCPhandler(ServerSocket serv, Map<Integer, String> log, Map<BSPair, BackupInfo> bckplist){
		try{
		exit = false;
		login = log;
		backuplist = bckplist;
		server = serv;

		while(exit == false){
    	  Socket tcpSocket = server.accept();
    	  //each connection has its own stream to allow multi threading
    	  DataInputStream in = new DataInputStream(tcpSocket.getInputStream());
    	  DataOutputStream out = new DataOutputStream(tcpSocket.getOutputStream());
	  	  //creates a thread for the user connection
	  	  Thread thread = new CStcp(tcpSocket, in, out, login, backuplist);
	  	  thread.start();

			}
		}
		catch (Exception e){e.printStackTrace();}
}

	public void tcpClose() throws IOException{
		server.close();
		exit = true;
	}
}
