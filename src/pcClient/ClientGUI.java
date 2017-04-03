package pcClient;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;

public class ClientGUI extends JPanel {
	private JButton btnSend = new JButton("Skicka");
	private JTextField tfText = new JTextField();
	
	public ClientGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		add(btnSend, BorderLayout.SOUTH);
		add(tfText, BorderLayout.CENTER);
	}
}
