package pcClient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ClientGUI extends JPanel implements ActionListener {
	private JButton btnSend = new JButton("Skicka");
	private JTextField tfText = new JTextField();
	private TestClient client = new TestClient(this);
	private TextArea textarea = new TextArea();
	
	public ClientGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		add(btnSend, BorderLayout.SOUTH);
		add(tfText, BorderLayout.CENTER);
		add(textarea, BorderLayout.EAST);
		btnSend.addActionListener(this);
	}
	
	public void addText(String text){
		textarea.setText(text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnSend) {
			client.sendMessage(tfText.getText());
		}
	}
	
	
}
