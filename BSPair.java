package rc;

import java.net.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.text.*;
import java.time.*;

public class BSPair{
  String ipBS;
  int portBS;

  public BSPair(String ip, int port){
    ipBS = ip;
    portBS = port;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
        return false;
    }

    if (!BSPair.class.isAssignableFrom(obj.getClass())) {
        return false;
    }

    final BSPair other = (BSPair) obj;
    if ((this.ipBS == null) ? (other.ipBS != null) : !this.ipBS.equals(other.ipBS)) {
        return false;
    }

    if (this.portBS != other.portBS) {
        return false;
    }

    return true;
}
}
