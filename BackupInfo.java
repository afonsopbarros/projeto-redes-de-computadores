package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

public class BackupInfo implements Serializable{
  String _ipBS;
  int _portBS;
  int _portUDP;
  //Map<Integer, boolean> _flags;
  Map<Integer, Map<String, Map<String, FileInfo> > > _users;

  public BackupInfo(String ipBS, int portBS, int portUDP){
    _users = new HashMap<Integer, Map<String, Map<String, FileInfo>> >();
    //_flags = new HashMap<Integer, boolean>();
    //_directories = new HashMap<String, List<FileInfo>>();
    _ipBS = ipBS;
    _portBS = portBS;
    _portUDP = portUDP;
  }

  public BackupInfo(){
  }

  public void addUser(Integer user){
    //_flags.put(user, false);
    Map<String, Map<String, FileInfo>> directories = new HashMap<String, Map<String, FileInfo>>();
    _users.put(user, directories);
  }

  public void removeUser(Integer user){
    _users.remove(user);

  }
  public void addDirectory(String dir, Integer user){
    Map<String, FileInfo> filelist = new HashMap<String, FileInfo>();
    _users.get(user).put(dir, filelist);
  }

  public void removeDirectory(String dir, Integer user){
      _users.get(user).remove(dir);
      if(_users.get(user).isEmpty())
        _users.remove(user);
  }

  public void addFile(String dir, FileInfo file, Integer user){
    _users.get(user).get(dir).put(file._filename, file);
  }

  public void removeFile(String dir, FileInfo file, Integer user){
    _users.get(user).get(dir).remove(file._filename);
  }

  public void updateFile(String dir, FileInfo file, Integer user){
    _users.get(user).get(dir).replace(file._filename, file);
  }

  public boolean containsDirectory(String dir, Integer user){
    return _users.get(user).containsKey(dir);
  }

  public boolean containsUser(Integer user){
    return _users.containsKey(user);
  }

  public boolean isEmpty(Integer user){
    return _users.get(user).isEmpty();
  }

  @Override
  public String toString(){
    return "ip:" + _ipBS + " port:" + _portBS;
  }
}
