package TestKryptering;

import java.io.*;
import java.net.*;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

//SERVERN SOM LYSSNAR PÅ INKOMMANDE TRAFIK OCH ANVÄNDER SERVERCONTROLLER FÖR 
//ATT VETA VAD SOM SKA SKE
public class ServerConnectivity {
	private SSLSocket socket;
	// private ServerGUI gui;
	private ServerController controller;
	private int port;
	

	public ServerConnectivity(int port, ServerController controller) {
		this.port = port;
		this.controller = controller;
		System.out.println("Server Started and listening to the port 25000");
		getConnection();
	}
	
	
	/*
	 * Method that listens to the serversocket and executes the action
	 */
	public void getConnection() {
		try {
			// Server is running always. This is done using this while(true)
			ServerSocket serversocket = ((SSLServerSocketFactory) SSLServerSocketFactory.getDefault()).createServerSocket(25000);
			
			// loop
			while (true) {
				// Reading the message from the client
				socket = (SSLSocket) serversocket.accept();
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				try{
					String data = br.readLine();
					controller.proccesData(data, socket);
				}catch (Exception e){
					e.printStackTrace();
				}
//				controller.proccesData(data, socket); 	// serverControllern
														// hanterar datan
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}
}