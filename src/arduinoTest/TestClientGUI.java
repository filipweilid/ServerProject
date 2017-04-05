package arduinoTest;

import javax.swing.JFrame;
/**
 * Startar ClientGUI
 * @author Grupp4
 *
 */

public class TestClientGUI extends JFrame {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Client");
		ClientGUI gui = new ClientGUI();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(500, 500);
		frame.add(gui);
		frame.pack();
		frame.setVisible(true);
	}
}
