package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;

/*
 * Class that creates socketconnection with the masterlock and sends
 * appropriate information
 */


public class ArduinoController {
	private MongoDBController mongodb;
	private String returnMessage;
	
	public ArduinoController(MongoDBController controller){
		this.mongodb = controller;
	}
	
	public String sendRequest(String lockname, String command){
		try {
			Socket socket = new Socket(mongodb.getParent(), 8888);
			//socket = new Socket(mongodb.getParent(), 8888);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//bw.write(createMessage(internalIP, command));
			bw.write(createMessage(lockname, command));
			bw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			returnMessage = br.readLine();
			br.close();
			socket.close();
			return returnMessage;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}
	
	public String createMessage(String ip, String message){
		String newMessage = message+ip+ "\n";
		return newMessage;
	}
}
