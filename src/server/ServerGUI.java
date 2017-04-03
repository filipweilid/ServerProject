package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Calendar;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ServerGUI extends JPanel {
	private JTextArea text = new JTextArea("Server log");
	private String log = "Server log \n";
	
	public ServerGUI(){
		setLayout(new BorderLayout());
		text.setText(log);
		setPreferredSize(new Dimension(500, 500));
		add(text, BorderLayout.CENTER);
		
	}
	
	public void addlog(String text, Calendar c){
		log = log + c.getTime() + ": " + text + "\n";
		this.text.setText(log);
	}
	
	public static void main(String[] args) {
		ServerGUI test = new ServerGUI();
		JFrame frame = new JFrame("Media Biblotek");
		frame.setLocation(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(test);
		frame.pack();
		frame.setVisible(true);
		TestServer server = new TestServer(25000, test);
	}
}
