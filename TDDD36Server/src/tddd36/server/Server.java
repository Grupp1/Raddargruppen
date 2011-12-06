package tddd36.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {


	// Default-value: 6789
	private int port;

	/* 
	 * Alla anslutna enheter sparas i detta objekt associerade med sina IP-addresser
	 * Behöver servern veta vilken IP-address en viss användare har så är det från detta
	 * objekt IP-addressen kan hämtas 
	 */
	public static Associations onlineUsers = new Associations();

	public Server() {
		this(4043);
	}

	public Server(int port) {
		this.port = port;
		startServer();
	}

	private void startServer() {
		try {
            System.setProperty("javax.net.ssl.keyStore","assets/serverKeystore.key");
    	    System.setProperty("javax.net.ssl.keyStorePassword","android");
    	    System.setProperty("javax.net.ssl.trustStore","assets/serverTrustStore");
    	    System.setProperty("javax.net.ssl.trustStorePassword","android");
    	    
            SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
            sslserversocket.setEnabledCipherSuites(new String[] { "SSL_RSA_WITH_RC4_128_MD5", "SSL_DH_anon_WITH_RC4_128_MD5" });
            
            System.out.println("Listening on port: " + port + "... ");

			while (true) 
				new Receiver((SSLSocket) sslserversocket.accept());

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
		/*new Thread(new Runnable(){
			@Override
			public void run() {
				Scanner in = new Scanner(System.in);
				while(true){
					LoginManager.logoutUser(in.next());
				}
			}
		}).start();*/
	}	
}
