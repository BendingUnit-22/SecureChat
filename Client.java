import javafx.scene.control.*;
import javax.crypto.SealedObject;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.math.BigInteger;

class Client {

    private SecureConnection secCon = null;
    Socket s = null;
    ObjectOutputStream oos = null;
    ObjectInputStream ois = null;

    public Client(String ipAddress, int port, ConnectCallback connectionEstb) throws Exception{
          s = new Socket(ipAddress, port);
         System.out.println("Local Port: " + s.getLocalPort());
         System.out.println("Server Port: " + s.getPort());
          oos = new ObjectOutputStream(s.getOutputStream());
          ois  = new ObjectInputStream(s.getInputStream());

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

          System.out.println("-------------------- Secure AES Communication Initialized ----------------------------");
          System.out.println(keyHash );

        secCon = new SecureConnection(ois, oos, keyHash);
        connectionEstb.ConnectionCallback(secCon);

 }

}
