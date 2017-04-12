package arduinoTest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JPanel;


public class ClientGUI extends JPanel implements ActionListener {
	private JPanel panel = new JPanel(new GridLayout(2, 2));
	private JButton On = new JButton("Öppna");
	private JButton On2 = new JButton("Tänd Lampa");
	private JButton Off = new JButton("Lås");
	private JButton Off2 = new JButton("Släck Lampa");
	private ClientController send = new ClientController();

	public ClientGUI() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		add(panel, BorderLayout.CENTER);
		panel.add(On);
		panel.add(On2);
		panel.add(Off);
		panel.add(Off2);
		On.addActionListener(this);
		On2.addActionListener(this);
		Off.addActionListener(this);
		Off2.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Öppnar Låset
		if (e.getSource() == On) {
			send.changeStatus("1E"); //H för high och E för 14hex
//			send.sendLog("Tände lampa 1", "Kalle"); 
//			send.sendStatusLog("lampa 1", "tänd");
		}
		//Tänder lampa
		if (e.getSource() == On2) {
			send.changeStatus("2C");
//			send.sendLog("Tände lampa 2", "Kalle");
//			send.sendStatusLog("lampa 2", "tänd");
		}
		//Låser låset
		if (e.getSource() == Off) {
			send.changeStatus("3E");
//			send.sendLog("Släckte lampa 1", "Kalle");
//			send.sendStatusLog("lampa 1", "släckt");
		}
		//Släcker lampa
		if (e.getSource() == Off2) {
			send.changeStatus("4C");
//			send.sendLog("Släckte lampa 2", "Kalle");
//			send.sendStatusLog("lampa 2", "släckt");
		}

	}

}
