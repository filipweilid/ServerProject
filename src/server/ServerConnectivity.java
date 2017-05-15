package server;

import java.io.*;
import java.net.*;

//SERVERN SOM LYSSNAR PÅ INKOMMANDE TRAFIK OCH ANVÄNDER SERVERCONTROLLER FÖR 
//ATT VETA VAD SOM SKA SKE
public class ServerConnectivity {
	// private Socket socket;
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
			ServerSocket serverSocket = new ServerSocket(port);
			// loop
			while (true) {
				// Reading the message from the client
				Socket socket = serverSocket.accept();
				System.out.println(socket.getInetAddress().toString() + " connected");
				new Thread(new clientThread(socket)).start(); // starts new
																// thread
				// BufferedReader br = new BufferedReader(new
				// InputStreamReader(socket.getInputStream(), "UTF-8"));
				// String data = br.readLine();
				// controller.processData(data, socket); // serverControllern
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class clientThread implements Runnable {
		private Socket socket;

		public clientThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			while (!socket.isClosed()) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					String data = br.readLine();
					if(data == null) {
						socket.close();
					} else {
						controller.processData(data, socket);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}