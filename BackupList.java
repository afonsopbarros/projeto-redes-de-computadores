package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;

public class BackupList implements Serializable{
  int _ipBS;
  int _portBS;
  ArrayList<String> _directories;

  public BackupList(int ipBS, int portBS){
    _ipBS = ipBS;
    _portBS = portBS;
  }

  public void addDirectory(String name){
    _directories.add(name);
  }

  public void removeDirectory(String name){
    _directories.remove(name);
  }

  public boolean containsDirectory(String name){
    return _directories.contains(name);
  }

  public boolean isEmpty(){
    return _directories.isEmpty();
  }
}
