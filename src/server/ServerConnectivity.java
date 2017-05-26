package server;

import java.io.*;
import java.net.*;

/*
 * Class that handles the connections to the server using a serveSocket
 */
public class ServerConnectivity {
	private ServerController controller;
	private int port;

	public ServerConnectivity(int port, ServerController controller) {
		this.port = port;
		this.controller = controller;
		getConnection();
	}

	/*
	 * Creates a new socket when a client connects and starts a new thread
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
	/*
	 * Inner class that handlers the client that connected to the server
	 */
	private class clientThread implements Runnable {
		private Socket socket;
		private String id;
		
		public clientThread(Socket socket) {
			this.socket = socket;
		}
		/*
		 * The run method which is called when a new thread is started. 
		 * Listens to incoming messages and processes them
		 */
		public void run() {
			while (!socket.isClosed()) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
					String data = br.readLine();
					if(data == null) { //socket closed, loggade ut eller avbröts
						socket.close();
						if(id != null) { //check if its a lock, lock has no id, prevents errors	
							controller.endConnection(id); //terminates the session
						}
					} else {
						String id = controller.processData(data, socket);
						if(!id.equals("")){
							this.id = id;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					try {
						socket.close(); //prevents error when internet connection i lost
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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