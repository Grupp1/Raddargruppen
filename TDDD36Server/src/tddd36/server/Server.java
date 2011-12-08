package tddd36.server;

import java.io.IOException;
import java.util.Scanner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {

	// Default-value: 6789
	private int port;

	/*
	 * Alla anslutna enheter sparas i detta objekt associerade med sina
	 * IP-addresser Beh�ver servern veta vilken IP-address en viss anv�ndare
	 * har s� �r det fr�n detta objekt IP-addressen kan h�mtas
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
			System.setProperty("javax.net.ssl.keyStore",
					"assets/serverKeystore.key");
			System.setProperty("javax.net.ssl.keyStorePassword", "android");
			System.setProperty("javax.net.ssl.trustStore",
					"assets/serverTrustStore");
			System.setProperty("javax.net.ssl.trustStorePassword", "android");

			SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory
					.createServerSocket(port);
			sslserversocket
					.setEnabledCipherSuites(new String[] { "SSL_DH_anon_WITH_RC4_128_MD5" });

			System.out.println("Listening on port: " + port + "... ");

			while (true)
				new Receiver((SSLSocket) sslserversocket.accept());

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	public static void main(String[] args) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner in = new Scanner(System.in);
				while (true) {
					String str = in.next();
					System.out.println("TEMPTEMP Logga ut " + str);
					LoginManager.logoutUser(str);
				}
			}
		}).start();
		new Server();
	}
}
