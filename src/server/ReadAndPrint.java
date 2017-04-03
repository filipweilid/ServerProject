package server;

import java.io.*;
import java.net.*;

/**
 * Reads Strings from a ServerSocket and prints them to standard out.
 */
public class ReadAndPrint {
	BufferedReader sockIn;

	public ReadAndPrint(Socket s) throws IOException {
		sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
	}

	public void service() {
		String msg;

		try {
			/* readLine() returns null if the end of the stream i reached. */
			while ((msg = sockIn.readLine()) != null) {
				System.out.println("Server got: " + msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(4711);

		while (true) {
			Socket s = ss.accept();
			/* A connection is established when accept() returns. */
			System.out.println("Connection established from " + s.getInetAddress().getHostName());
			ReadAndPrint rap = new ReadAndPrint(s);
			rap.service();
		}

	}

}