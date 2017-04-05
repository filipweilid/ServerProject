package arduinoTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

import javax.swing.*;

import arduinoTest.UDPSend.SendUdpPackage;

public class UDPUserGUI extends JPanel implements ActionListener {
	private JButton btnOn = new JButton("Tänd 1");
	private JButton btnOn2 = new JButton("Tänd 2");
	private JButton btnOff = new JButton("Släck 1");
	private JButton btnOff2 = new JButton("Släck 2");
	private UDPSend udpsend;
	private JPanel panel = new JPanel(new GridLayout(2,2));
	
	
	public UDPUserGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		add(panel, BorderLayout.CENTER);
		panel.add(btnOn);
		panel.add(btnOff);
		panel.add(btnOn2);
		panel.add(btnOff2);
		btnOn.addActionListener(this);
		btnOff.addActionListener(this);
		btnOn2.addActionListener(this);
		btnOff2.addActionListener(this);
		
		 try {
			udpsend = new UDPSend("192.168.0.10", 8888);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnOn) {
//			udpsend.send("diod1on");
			udpsend.send("H2");
		}
		if(e.getSource() == btnOn2){
//			udpsend.send("diod2on");
			udpsend.send("H3");
		}
		if(e.getSource() == btnOff){
//			udpsend.send("diod1off");
			udpsend.send("L2");
		}
		if(e.getSource() == btnOff2){
//			udpsend.send("diod2off");
			udpsend.send("L3");
		}
	}
	
	
}