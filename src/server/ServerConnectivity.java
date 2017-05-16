package server;

import java.io.*;
import java.net.*;

//SERVERN SOM LYSSNAR PÅ INKOMMANDE TRAFIK OCH ANVÄNDER SERVERCONTROLLER FÖR 
//ATT VETA VAD SOM SKA SKE
public class ServerConnectivity {
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
			while (true) {
				Socket socket = serverSocket.accept(); //blocks
				System.out.println(socket.getInetAddress().toString() + " connected");
				new Thread(new clientThread(socket)).start(); // starts new thread
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class clientThread implements Runnable {
		private Socket socket;
		private String user;

		public clientThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			while (!socket.isClosed()) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					String data = br.readLine();
					if(data == null) { //socket closed, loggade ut eller avbröts
						socket.close();
						controller.endConnection(user);
					} else {
						String user = controller.processData(data, socket);
						if(!user.equals("")){
							this.user = user;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}