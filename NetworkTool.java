

import java.net.*;
import java.io.*;
import java.util.*;


public class NetworkTool{

public static String requestPublicIPAddress(){
    try{
        for(Enumeration en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
          NetworkInterface intf = (NetworkInterface)en.nextElement();
          for (Enumeration enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
            InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address){
             return inetAddress.getHostAddress();
            }
          }
        }
    }catch(SocketException e){
        System.out.println("Address not found!" + e);
    }
    return null;
}
//
//public static boolean serverAvailabilityCheck(String server_address, int port) {
//    try (Socket s = new Socket(server_address, port)) {
//        s.close();
//        return true;
//    } catch (IOException ex) {
//        System.out.println("Server not available!");
//    }
//    return false;
//}

}