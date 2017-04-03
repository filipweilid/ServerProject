package server;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServerGUI extends JPanel {
	JTextArea text = new JTextArea("Server log");
	String log = "Server log \n";
	
	
	public ServerGUI(){
		setLayout(new BorderLayout());
		text.setText(log);
		setPreferredSize(new Dimension(500, 500));
		add(text, BorderLayout.CENTER);
	}
	
	public void addlog(String text){
		log = log + text + "\n";
	}
	
	public static void main(String[] args) {
		ServerGUI test = new ServerGUI();
		JFrame frame = new JFrame("Media Biblotek");
		frame.setLocation(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(test);
		frame.pack();
		frame.setVisible(true);
	}
}
