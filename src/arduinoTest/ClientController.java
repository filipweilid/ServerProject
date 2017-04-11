package arduinoTest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
/**
 * Klassen ClientGUI använder denna klassen för att skicka ett meddelande till Arduino
 * @author Grupp4
 *
 */
public class ClientController {
	
	private static Socket socket;
	private String hostArduino = "192.168.1.101";
	private int portArduino = 8888;
	private String hostServer = "localhost";
	private int portServer = 25000;

	public void changeStatus(String status) {
		try {
			InetAddress address = InetAddress.getByName(hostArduino);
			socket = new Socket(address, portArduino);
			// Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			String sendStatus = status + "\n";
			bw.write(sendStatus);
			bw.flush();
			System.out.println("Message sent to the server : " + sendStatus);
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
	
	public void sendLog(String text, String username) {
		try {
			InetAddress address = InetAddress.getByName(hostServer);
			socket = new Socket(address, portServer);
			// Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			String sendLog = "log" + ";" + text + ";" + username + "\n";
			bw.write(sendLog);
			bw.flush();
			System.out.println("Message sent to the server : " + sendLog);
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
	
	public void sendStatusLog(String lock, String status) {
		try {
			InetAddress address = InetAddress.getByName(hostServer);
			socket = new Socket(address, portServer);
			// Send the message to the server
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
			BufferedWriter bw = new BufferedWriter(osw);

			String sendStatusLog = "status" + ";" + lock + ";" + status + "\n";
			bw.write(sendStatusLog);
			bw.flush();
			System.out.println("Message sent to the server : " + sendStatusLog);
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
}
