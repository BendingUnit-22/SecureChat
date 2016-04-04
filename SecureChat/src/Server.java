import java.lang.*;
import java.io.*;
import java.net.*;
import java.math.BigInteger;




public class Server extends Thread {

  private ServerSocket servSock = null;
	private ClientHandler clientHandler = null;
	private ConnectCallback connectionEstb = null;

	public Server(int port, ConnectCallback ccb ) throws Exception {

			servSock = new ServerSocket(port, 5);
			connectionEstb = ccb;
			System.out.println("Listening on port " + port);

	}


	public void run() {
		while(true) {
			try {
				System.out.println("Waiting for connections...");
   				Socket s = servSock.accept();
   				System.out.println("Server accepted connection from: " + s.getInetAddress().getHostAddress());
				BigInteger q = DiffieHellman.getModulous();
				BigInteger a = DiffieHellman.getPrimitiveRoot();
				 clientHandler = new ClientHandler(s,q,a, connectionEstb);
				clientHandler.setDaemon(true);
				clientHandler.start();
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
    
	}


	private void printInfo(Socket s) {
		InetAddress ia;
		System.out.println("\tLocal Port : " + s.getLocalPort());
		System.out.println("\tRemote Port: " + s.getPort());

		ia = s.getInetAddress();		// REMOTE
		System.out.println("\t==> Remote IP: " +ia.getHostAddress());
		System.out.println("\t==> Remote Name: " +ia.getHostName());
		System.out.println("\t==> Remote DNS Name: " +ia.getCanonicalHostName());

		ia = s.getLocalAddress();		// LOCAL
		System.out.println("\t==> Local IP: " +ia.getHostAddress());
		System.out.println("\t==> Local Name: " +ia.getHostName());
		System.out.println("\t==> Local DNS Name: " +ia.getCanonicalHostName());
	}



}
