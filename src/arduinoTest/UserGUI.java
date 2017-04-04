package arduinoTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

import javax.swing.*;

public class UserGUI extends JPanel implements ActionListener {
	private JButton btnSend = new JButton("Skicka");
	//private ClientSend client = new ClientSend();
	
	public UserGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		add(btnSend, BorderLayout.CENTER);
		btnSend.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnSend) {
			try {
				ClientSend client = new ClientSend(TOOL_TIP_TEXT_KEY, 8888);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	
}