package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

/*
 * Class that creates and handles the connection to the locks
 */


public class ArduinoController {
	private MongoDBController mongodb;
	//private String returnMessage;
	
	public ArduinoController(MongoDBController controller){
		this.mongodb = controller;
	}
	/*
	 * Sends a request to the masterlock with the ip of the target lock and 
	 * the command to execute
	 */
	public String sendRequest(String ip, String command){
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(mongodb.getParent(), 8888), 10000); //checks if lock is responding in time
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write(createMessage(ip, command));
			bw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String returnMessage = br.readLine();
			br.close();
			socket.close();
			return returnMessage;
		} catch(Exception e) {
			return "error";
		}
	}
	/*
	 * Creates the message to send to the lock
	 */
	private String createMessage(String ip, String message){
		String newMessage = message+ip+ "\n";
		return newMessage;
	}
}
