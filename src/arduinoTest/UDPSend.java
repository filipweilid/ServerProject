package arduinoTest;

import java.io.IOException;
import java.net.*;

public class UDPSend {
	private String ipAddress;
	private int port;
	private DatagramSocket socket;

	public UDPSend(String ipAddress, int port) throws SocketException {
		this.ipAddress = ipAddress;
		this.port = port;
		socket = new DatagramSocket();
		new Thread(new RecievePackage()).start();

	}
	
	public void send(String message){
		SendUdpPackage SB = new SendUdpPackage();
		SB.send(message);
	}

	public class SendUdpPackage {
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
	}

	public class RecievePackage implements Runnable {
		@Override
		public void run() {
			while (true) {
				byte[] buffer = new byte[15];
				DatagramPacket recPacket = new DatagramPacket(buffer, buffer.length);
				try {
					socket.receive(recPacket);
				} catch (IOException e) {
					e.printStackTrace();
				}
				recPacket.getData();
				System.out.println("Medelandet **"+ new String(recPacket.getData()) + " **kom fram");
			}
		}
	}
}
