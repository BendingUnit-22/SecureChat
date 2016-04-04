
import java.lang.*;
import java.io.*;
import java.net.*;
import java.math.BigInteger;
import javax.crypto.*;
import java.util.Scanner;

public class ClientHandler extends Thread {
	private Socket s = null;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois  = null;
	private BigInteger q = null;
	private BigInteger a = null;

	public ClientHandler(Socket s, BigInteger modulus, BigInteger pRoot) {
		this.s = s;
		this.q = modulus;
		this.a = pRoot;
	try {
	 		oos  = new ObjectOutputStream(s.getOutputStream());
	 		ois  = new ObjectInputStream(s.getInputStream());
		}
		catch (Exception e) {
			System.err.println("Exception: " + e);
		}
	}

	public void run() {
		try {
			System.out.println("Server: Initializing Diffie Hellman protocol...");

        oos.writeObject(q);
        System.out.println("Server: sending modulus (q)...");

        oos.writeObject(a);
        System.out.println("Server: sending prime root (a)...");

        DiffieHellman dh = new DiffieHellman();
        BigInteger y = dh.computePublicKey(q,a);
				System.out.println("Server: Computing public key (Ya)... ");

      	oos.writeObject(y);
        System.out.println("Server: Sending Ya...");

				System.out.println("Server: Waiting for Client's public key (Yb)...");
      	BigInteger yb = (BigInteger)ois.readObject();
      	System.out.println("Server: Recieved Yb.");

				System.out.println("Server: Creating symmetric Key...");
      	BigInteger mkey = dh.computeMasterKey(yb,q);

				String keyHash = DiffieHellman.createHashString(mkey);
				System.out.println("Server: Symmetric Key Created.  \n");
				System.out.println("Hash:" + keyHash);

			 	System.out.println("--------- Secure Communication Initialized -----------");

				Scanner scanner = new Scanner(System.in);
				 SecureConnection ml = new SecureConnection(ois, oos, keyHash);
				 ml.setDaemon(false);
				 ml.start();





				 while (scanner.hasNext()){
							 String message = scanner.nextLine();
							 INFO info = new INFO("Server", message);
							 // SealedObject sb = aes.encrypt(info);
							 // oos.writeObject(sb);
							 ml.sendMessage(info);
					 }




	 		/*
	  		 * Close connection
	  		 */
         	//	s.close();
		}
		catch(Exception e) {
			System.err.println("Exception: " + e);
		}
	}


}
