

import java.lang.*;
import java.io.*;
import java.net.*;
import java.math.BigInteger;

public class ClientHandler extends Thread {
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois  = null;
	private BigInteger q = null;
	private BigInteger a = null;
	private ConnectCallback connectionEstb = null;

	public ClientHandler(Socket s, BigInteger modulus, BigInteger pRoot, ConnectCallback ccb) {
		this.q = modulus;
		this.a = pRoot;
		this.connectionEstb = ccb;
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

			System.out.println("-------------------- Secure AES Communication Initialized ----------------------------");
			System.out.println(keyHash );


			SecureConnection secCon = new SecureConnection(ois, oos, keyHash);
			connectionEstb.ConnectionCallback(secCon);

		}
		catch(Exception e) {
			System.err.println(e);
		}

	}


}
