package arduinoTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

import javax.swing.*;

import arduinoTest.Send.SendUdpPackage;

public class UserGUI extends JPanel implements ActionListener {
	private JButton btnSend = new JButton("Skicka");
	private Send udpsend;
	//private ClientSend client = new ClientSend();
	
	public UserGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		add(btnSend, BorderLayout.CENTER);
		btnSend.addActionListener(this);
		
		 try {
			udpsend = new Send("192.168.0.10", 8888);
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnSend) {
			udpsend.send();
			
		}
	}
	
	
}