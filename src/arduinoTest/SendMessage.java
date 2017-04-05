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
 * Klassen ClientGUI använder denna klassen för att skika ett meddelande till Arduino
 * @author Grupp4
 *
 */
public class SendMessage {
	
	private static Socket socket;
	private String host = "192.168.0.10";
	private int port = 8888;

	public void Message(String message) {
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
}
