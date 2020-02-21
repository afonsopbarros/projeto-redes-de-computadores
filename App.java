package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;
import java.util.concurrent.TimeUnit;

public class App{
  public static void main(String[] args) throws IOException{
    String username = new String();
    String password = new String();

    User user = new User(5678, "guadiana.tecnico.ulisboa.pt");

    BufferedWriter menu = new BufferedWriter(new OutputStreamWriter(System.out));
    BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    try{
      String s = new String();
      boolean exit = false;
      String item = new String();
      String reply = new String();
      String saux = new String();
      String d = new String();

      s = "login with your credentials:";
      menu.write(s, 0, s.length());
      menu.newLine();
      menu.flush();

      while(exit == false){
        s = input.readLine();
        StringTokenizer st = new StringTokenizer(s, " \n");
        item = st.nextToken();
        switch(item){
          case "login":
            username = st.nextToken();
            password = st.nextToken();
            reply = user.Communicate("AUT" + " " + username + " " + password);
            saux = new String();
            if(reply.equals("AUR OK")){
              saux = "user validated";
              menu.write(saux, 0, saux.length());
              menu.newLine();
              menu.flush();
            }
            else if (reply.equals("AUR NOK")){
              saux = "wrong password";
              menu.write(saux, 0, saux.length());
              menu.newLine();
              menu.flush();
            }
            else{
              saux = "user successfuly created";
              menu.write(saux, 0, saux.length());
              menu.newLine();
              menu.flush();
              user.Close();
            }
            break;

          case "backup":
            user = new User(5678, "localhost");
            reply = user.Communicate("AUT" + " " + username + " " + password);
            if(reply.equals("AUR NOK")){
              System.out.println("Error, wrong password");
              break;
            }
            d = st.nextToken();
            File dir = new File(d);
            File files[] = dir.listFiles();
            String info = new String();
            int N = 0;
            DecimalFormat f = new DecimalFormat("00");
            for(File file: files){
              N++;
              LocalDateTime i = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());
              info +=  file.getName() + " " + f.format(i.getDayOfMonth()) + "." + f.format(i.getMonthValue()) + "." + i.getYear() + " " + f.format(i.getHour()) + ":" + f.format(i.getMinute())
                + ":" + f.format(i.getSecond()) + " " + file.length() + " ";
            }

            reply = user.Communicate("BCK " + d + " " + N + " " + info);
            //System.out.println(reply);
            user.Close();
            //reply = user.Communicate("BKR");
            break;

          case "restore":
            user = new User(5678, "localhost");
            reply = user.Communicate("AUT" + " " + username + " " + password);
            if(reply.equals("AUR NOK")){
              System.out.println("Error, wrong password");
              break;
            }

            d = st.nextToken();

            reply = user.Communicate("RST " + d);
            break;

          default:
            saux = "no command detected";
            menu.write(saux, 0, saux.length());
            break;
          case "exit":
            user.Communicate("EXIT");
            exit = true;
        }
      }
      menu.close();
      input.close();
      TimeUnit.SECONDS.sleep(2);
      System.exit(0);
    }
    catch(Exception e){e.printStackTrace();}

  }
}
