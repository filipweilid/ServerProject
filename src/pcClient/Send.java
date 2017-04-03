package pcClient;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class Send {
	boolean fis = false;

	public boolean button() {


		return fis;
	}

	public void Sender() throws UnknownHostException, IOException {

		Socket s = new Socket("localhost", 4711);
		PrintWriter sockOut = new PrintWriter(s.getOutputStream());
		System.out.println("Client connected");
		if (fis = true) {
			sockOut.append("Accept");
		}
		sockOut.close(); // Flushes the out stream.
		s.close();
	}

	public static void main(String[] args) throws IOException, UnknownHostException {
		
		Send b = new Send();
		b.button();
		b.Sender();
		// Socket s = new Socket("localhost", 4711);
		// PrintWriter sockOut = new PrintWriter(s.getOutputStream());
		// System.out.println("Client connected");
		//
		// sockOut.append("Accept");
		//
		//
		// sockOut.close(); //Flushes the out stream.
		// s.close();

	}

}
