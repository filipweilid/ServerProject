package arduinoTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

import javax.swing.*;

import arduinoTest.Send.SendUdpPackage;

public class UserGUI extends JPanel implements ActionListener {
	private JButton btnOn = new JButton("T�nd 1");
	private JButton btnOn2 = new JButton("T�nd 2");
	private JButton btnOff = new JButton("Sl�ck 1");
	private JButton btnOff2 = new JButton("Sl�ck 2");
	private Send udpsend;
	private JPanel panel = new JPanel(new GridLayout(2,2));
	
	public UserGUI() {
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
			udpsend = new Send("192.168.0.10", 8888);
			
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnOn) {
//			udpsend.send("diod1on");
			udpsend.send("1");
		}
		if(e.getSource() == btnOn2){
//			udpsend.send("diod2on");
			udpsend.send("2");
		}
		if(e.getSource() == btnOff){
//			udpsend.send("diod1off");
		}
		if(e.getSource() == btnOff2){
//			udpsend.send("diod2off");
		}
	}
	
	
}