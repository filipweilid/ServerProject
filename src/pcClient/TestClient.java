package pcClient;

import java.io.*;
import java.net.*;

public class TestClient {

	private static Socket socket;
	private String host = "localhost";
	private int port = 25000;
	private ClientGUI gui;
	
	public TestClient(ClientGUI gui){
		this.gui = gui;
	}

	public void sendMessage(String message) {
		try {
			InetAddress address = InetAddress.getByName(host);
			socket = new Socket(address, port);
			// Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);

			String sendMessage = message + "\n";
			bw.write(sendMessage);
			bw.flush();
			System.out.println("Message sent to the server : " + sendMessage);
			// Get the return message from the server
			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String returnMessage = br.readLine();
//			if(returnMessage.charAt(0) == 'L'){ //vi fick en log tillbaka
//				String text = returnMessage.substring(1);
//				String[] array = text.split(";");
//				String returntext = "";
//				for(int i= 0; i<array.length; i++){
//					returntext = returntext + array[i] + "\n";
//				}
//				gui.addText(returntext);
//			}
			gui.addText(returnMessage);
			System.out.println("Message received from the server : " + returnMessage);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			// Closing the socket
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
}