package TestKryptering;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ClientGUI extends JPanel implements ActionListener {
	private JButton btnSend = new JButton("Skicka");
	private JTextField tfText = new JTextField();
	private TestClient client = new TestClient(this);
	private TextArea textarea = new TextArea();
	private JPanel panel1 = new JPanel(new GridLayout(2,1)	);
	
	public ClientGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(700, 500));
		add(panel1, BorderLayout.SOUTH);
		panel1.add(tfText);
		panel1.add(btnSend);
		add(textarea, BorderLayout.CENTER);
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
