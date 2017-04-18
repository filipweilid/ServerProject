package server;

import java.io.*;
import java.net.*;


//SERVERN SOM LYSSNAR PÅ INKOMMANDE TRAFIK OCH ANVÄNDER SERVERCONTROLLER FÖR 
//ATT VETA VAD SOM SKA SKE
public class ServerConnectivity {
	
	private Socket socket;
	//private ServerGUI gui;
	private ServerController controller;

	public ServerConnectivity(int port, ServerController controller) {
		this.controller = controller;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			System.out.println("Server Started and listening to the port 25000");
			// Server is running always. This is done using this while(true)
			// loop
			while (true) {
				// Reading the message from the client
				socket = serverSocket.accept();
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(isr);
				String data = br.readLine();
				controller.proccesData(data, socket); //serverControllern hanterar datan
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