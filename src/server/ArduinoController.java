package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/*
 * Class that creates socketconnection with the masterlock and sends
 * appropriate information
 */


public class ArduinoController {
	private MongoDBController mongodb = new MongoDBController();
	private Socket socket;
	private String returnMessage;
	
	public String sendRequest(String internalIP, String command){
		try {
			//socket = new Socket(mongodb.getParent(), 8888);
			socket = new Socket("192.168.1.101", 8888);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//bw.write(createMessage(internalIP, command));
			bw.write(createMessage("192.168.1.103", command));
			bw.flush();
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			returnMessage = br.readLine();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				socket.close();
				return returnMessage;
			} catch (Exception e) {
			}
		}
		return "error";
	}
	
	public String createMessage(String ip, String message){
		String newMessage = message+ip+ "\n";
		return newMessage;
	}
}
