package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

public class FileInfo implements Serializable, Comparable<FileInfo>{
  String _filename;
  String _dir;
  LocalDateTime _datetime;
  long _size;

  public FileInfo(String dir, String filename, LocalDateTime datetime, long size){
    _dir = dir;
    _filename = filename;
    _datetime = datetime;
    _size = size;
  }

  public boolean equal(FileInfo file){
    return _filename.equals(file._filename);
  }
  @Override
  public int compareTo(FileInfo file){
    return _datetime.compareTo(file._datetime);
  }

  @Override
  public String toString(){
    return (_filename + " " +  _datetime + " " + _size);
  }
}
