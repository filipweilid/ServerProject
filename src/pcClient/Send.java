package pcClient;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Send {
	boolean fis = false;

	public boolean button() {


		return fis;
	}
	
	public Send(String ip, int port) throws UnknownHostException, IOException {
		Socket s = new Socket(ip, port);
		PrintWriter sockOut = new PrintWriter(s.getOutputStream(), true);
		System.out.println("Client connected");
		sockOut.close();
		s.close();
	}

	public static void main(String[] args) throws IOException, UnknownHostException {

		new Send("localhost", 4711);
	}
}
