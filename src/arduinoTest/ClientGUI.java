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
	private JButton On = new JButton("Sätt på Lampa 1");
	private JButton On2 = new JButton("Sätt på Lampa 2");
	private JButton Off = new JButton("Släck Lampa 1");
	private JButton Off2 = new JButton("Släck Lampa 2");
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
		
		if (e.getSource() == On) {
			send.changeStatus("HE"); //H för high och E för 14hex
//			send.sendLog("Tände lampa 1", "Kalle"); 
//			send.sendStatusLog("lampa 1", "tänd");
		}
		if (e.getSource() == On2) {
			send.changeStatus("HC");
//			send.sendLog("Tände lampa 2", "Kalle");
//			send.sendStatusLog("lampa 2", "tänd");
		}
		if (e.getSource() == Off) {
			send.changeStatus("LE");
//			send.sendLog("Släckte lampa 1", "Kalle");
//			send.sendStatusLog("lampa 1", "släckt");
		}
		if (e.getSource() == Off2) {
			send.changeStatus("LC");
//			send.sendLog("Släckte lampa 2", "Kalle");
//			send.sendStatusLog("lampa 2", "släckt");
		}

	}

}
