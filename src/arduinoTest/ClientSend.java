package arduinoTest;

import java.io.IOException;
import java.net.*;

public class ClientSend {
	private String ipAddress;
	private int port;
	private DatagramSocket socket;

	public ClientSend(String ipAddress, int port) throws SocketException {
		this.ipAddress = ipAddress;
		this.port = port;
		socket = new DatagramSocket();
		new Thread(new SendBees()).start();
		new Thread(new RecieveBees()).start();

	}

	public class SendBees implements Runnable {

		public void send() {
			byte[] beeMass;
			int massLength;

			for (int i = 0; i < 10; i++) {
				String beeNr = "Bi nummer" + (i+1);
				beeMass = beeNr.getBytes();
				massLength = beeMass.length;
				try {
					DatagramPacket packet = new DatagramPacket(beeMass, massLength, InetAddress.getByName(ipAddress),
							port);
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

		@Override
		public void run() {
			SendBees SB = new SendBees();
			SB.send();
		}

	}

	public class RecieveBees implements Runnable {
		byte[] buffer = new byte[30];
		
		@Override
		public void run() {
			
			while(true){
			DatagramPacket recPacket = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(recPacket);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			recPacket.getData();
			System.out.println(new String(recPacket.getData()) +
					 " klarade sig till boet!");
			}
		
	

	}
		

}
	public static void main(String[] args) throws SocketException {
		new ClientSend("192.168.0.10", 8888);
	}
}