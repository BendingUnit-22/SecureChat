
import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;
import javax.crypto.*;

public class SecureConnection extends Thread {

  private ObjectInputStream ois = null;
  private ObjectOutputStream oos = null;
  private AES aes = null;
    private ConnectCallback connectCallback = null;

  public SecureConnection(ObjectInputStream in, ObjectOutputStream out,  String passphrase){
        ois  = in;
        oos = out;
        aes = new AES(passphrase);
  }



  public void sendMessage(Serializable info){
    try{
         SealedObject sb = aes.encrypt(info);
         oos.writeObject(sb);
    }catch(Exception e){
         System.out.println("Reciever unreachable : " + e);
    }
  }

  @Override
  public void run(){
      if (connectCallback == null){
          System.out.println("Connection Call back is null...");
          return;
      }
    try {
        while(true){
          SealedObject sealedObj = (SealedObject)ois.readObject();
          INFO info = (INFO)aes.decrypt(sealedObj);
            connectCallback.didRecieveInfo(info);
      }
    }catch(Exception e) {
        System.err.println("Exception: " + e);
  	}
  }

    public void setCallback(ConnectCallback ccb){
        connectCallback = ccb;
    }

}
