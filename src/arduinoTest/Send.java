package arduinoTest;

import java.io.IOException;
import java.net.*;

public class Send {
	private String ipAddress;
	private int port;
	private DatagramSocket socket;

	public Send(String ipAddress, int port) throws SocketException {
		this.ipAddress = ipAddress;
		this.port = port;
		socket = new DatagramSocket();
		new Thread(new SendUdpPackage()).start();
		new Thread(new RecievePackage()).start();

	}
	
	public void send(String message){
		SendUdpPackage SB = new SendUdpPackage();
		SB.send(message);
	}

	public class SendUdpPackage implements Runnable {
		public void send(String message) {
			byte[] udpMass;
			int udpLength;
			String beeNr = message;
			udpMass = beeNr.getBytes();
			udpLength = udpMass.length;
			try {
				DatagramPacket packet = new DatagramPacket(udpMass, udpLength, InetAddress.getByName(ipAddress), port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
		}
	}

	public class RecievePackage implements Runnable {
		byte[] buffer = new byte[15];

		@Override
		public void run() {
			while (true) {
				DatagramPacket recPacket = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(recPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				recPacket.getData();
				System.out.println("Medelandet "+ new String(recPacket.getData()) + "kom fram");
			}
		}
	}
}
