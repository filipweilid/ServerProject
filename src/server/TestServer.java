package server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class TestServer {

	private static Socket socket;
	private ServerGUI gui;
	private ServerController controller;

	public TestServer(int port, ServerController controller) {
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
				String number = br.readLine();
				controller.logDatabase(number, socket.getInetAddress().toString(), null);
				System.out.println("Message received from client is " + number);
				// Multiplying the number by 2 and forming the return message
				String returnMessage;
				try {
					int numberInIntFormat = Integer.parseInt(number);
					int returnValue = numberInIntFormat * 2;
					returnMessage = String.valueOf(returnValue) + "\n";
					
				} catch (NumberFormatException e) {
					// Input was not a number. Sending proper message back to
					// client.
					returnMessage = "Please send a proper number\n";
				}
				
				// Sending the response back to the client.
				OutputStream os = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				bw.write(returnMessage);
				System.out.println("Message sent to the client is " + returnMessage);
				bw.flush();
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
	
	public static void main(String[] args) {
	}
}