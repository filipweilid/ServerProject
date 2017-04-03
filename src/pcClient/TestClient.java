package pcClient;

import java.io.*;
import java.net.*;

public class TestClient {

	private static Socket socket;
	private String host = "10.2.7.202";
	private int port = 25000;

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

	public static void main(String args[]) {
		
	}
}