package arduinoTest;

import java.awt.*;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.net.InetAddress;
//import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
/*
 * Viktor Werngren
 */

public class ClientGUI extends JPanel implements ActionListener {
	private JPanel panel1 = new JPanel(new GridLayout(2, 2));
	private JPanel panel2 = new JPanel(new GridLayout(1, 1));
	private JButton On = new JButton("Öppna");
	private JButton On2 = new JButton("Tänd Lampa");
	private JButton Off = new JButton("Lås");
	private JButton Off2 = new JButton("Släck Lampa");
	private JLabel lblStatus1 = new JLabel("Status = ");
	private JLabel lblStatus2 = new JLabel("Status = ");
	private ClientController send = new ClientController();

	public ClientGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		add(panel1, BorderLayout.CENTER);
		add(panel2, BorderLayout.NORTH);
		panel2.add(lblStatus1);
		panel2.add(lblStatus2);
		panel1.add(On);
		panel1.add(On2);
		panel1.add(Off);
		panel1.add(Off2);
		On.addActionListener(this);
		On2.addActionListener(this);
		Off.addActionListener(this);
		Off2.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Öppnar Låset
		if (e.getSource() == On) {
			send.changeStatus("1"); //H för high och E för 14hex
			lblStatus1.setText("Status = Open");
//			send.sendLog("Tände lampa 1", "Kalle"); 
//			send.sendStatusLog("lampa 1", "tänd");
		}
		//Tänder lampa
		if (e.getSource() == On2) {
			send.changeStatus("2C");
			lblStatus2.setText("Status = Open");
//			send.sendLog("Tände lampa 2", "Kalle");
//			send.sendStatusLog("lampa 2", "tänd");
		}
		//Låser låset
		if (e.getSource() == Off) {
			send.changeStatus("2");
			lblStatus1.setText("Status = Locked");
//			send.sendLog("Släckte lampa 1", "Kalle");
//			send.sendStatusLog("lampa 1", "släckt");
		}
		//Släcker lampa
		if (e.getSource() == Off2) {
			send.changeStatus("4C");
			lblStatus2.setText("Status = Locked");
//			send.sendLog("Släckte lampa 2", "Kalle");
//			send.sendStatusLog("lampa 2", "släckt");
		}

	}

}
