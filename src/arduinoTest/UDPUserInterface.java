package arduinoTest;


import javax.swing.JFrame;


public class UDPUserInterface extends JFrame {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Client");
		UDPUserGUI gui = new UDPUserGUI();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500, 500);
		frame.add(gui);
		frame.pack();
		frame.setVisible(true);
	}
}