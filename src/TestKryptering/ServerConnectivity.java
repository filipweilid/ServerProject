package TestKryptering;

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

//SERVERN SOM LYSSNAR PÅ INKOMMANDE TRAFIK OCH ANVÄNDER SERVERCONTROLLER FÖR 
//ATT VETA VAD SOM SKA SKE
public class ServerConnectivity {
	private ServerController controller;

	public ServerConnectivity(int portklient, int portarduino, ServerController controller) {
		SSLConnection socket1 = new SSLConnection(portklient);
		ArduinoConnection socket2 = new ArduinoConnection(portarduino);
		this.controller = controller;
		Thread serverthread1 =new Thread(socket1);
        Thread serverthread2 =new Thread(socket2);
        serverthread1.start();
        serverthread2.start();
	}

	/*
	 * Method that listens to the serversocket and executes the action
	 */
	private class SSLConnection implements Runnable {
		private SSLSocket socket;
		private int port;

		public SSLConnection(int port) {
			this.port = port;
		}
		public void run() {
			try {
				// Server is running always. This is done using this while(true)
				ServerSocket serversocket = ((SSLServerSocketFactory) SSLServerSocketFactory.getDefault())
						.createServerSocket(port);
				// loop
				while (true) {
					// Reading the message from the client
					System.out.println("lyssnar på port" + port);
					socket = (SSLSocket) serversocket.accept();
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					try {
						String data = br.readLine();
						controller.proccesData(data, socket);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("HELLO CRASH BOOM BOOM ");
					}
					System.out.println("test");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("test");
		}
	}

	private class ArduinoConnection implements Runnable {
		private Socket socket;
		private int port;

		public ArduinoConnection(int port) {
			this.port = port;
		}

		public void run() {
			try {
				// Server is running always. This is done using this while(true)
				ServerSocket serversocket = new ServerSocket(port);
				// loop
				while (true) {
					// Reading the message from the client
					System.out.println("lyssnar på port" + port);
					socket = serversocket.accept();
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					String data = br.readLine();
					controller.proccesData(data, socket);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("test");
			
		}
	}
}