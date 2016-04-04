import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

class Client {

  public Client(String ipAddress, int port){
    try {
         Socket s = new Socket(ipAddress, port);
         System.out.println("Local Port: " + s.getLocalPort());
         System.out.println("Server Port: " + s.getPort());

         ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
         ObjectInputStream ois  = new ObjectInputStream(s.getInputStream());

         BigInteger q = (BigInteger) ois.readObject();
         System.out.println("Client: Recived modulus (q). ");

         BigInteger a = (BigInteger) ois.readObject();
         System.out.println("Client: Recived prime root (a). ");

         System.out.println("Client: Computing public key (Ya)...");
         DiffieHellman dh = new DiffieHellman();
         BigInteger y = dh.computePublicKey(q,a);

         oos.writeObject(y);
         System.out.println("Client: Sending my Ya...");

         System.out.println("Client: Waiting for Server's public key (Yb)...");
         BigInteger yb = (BigInteger)ois.readObject();
         System.out.println("Client: Recieved Yb.");

         BigInteger mkey = dh.computeMasterKey(yb,q);

         System.out.println("Client: Creating symmetric Key...");

          String keyHash = DiffieHellman.createHashString(mkey);
          System.out.println("Client: Symmetric Key Created. \n");
          System.out.println("Hash:" + keyHash);

          System.out.println("--------- Secured Communication Initialized -----------");
          Scanner scanner = new Scanner(System.in);
          //AES aes = new AES(keyHash);

          SecureConnection ml = new SecureConnection(ois, oos, keyHash);
          ml.setDaemon(false);
          ml.start();

        while (scanner.hasNext()){
              String message = scanner.nextLine();
              INFO info = new INFO("Rixing", message);
              // SealedObject sb = aes.encrypt(info);s
              // oos.writeObject(sb);
              ml.sendMessage(info);
          }




 oos.close();
 ois.close();

 /*
  * Close connection
  */
 s.close();
    }
    catch(Exception e) {
       System.err.print("[ERROR] ::" + e);
    }
 }



}
