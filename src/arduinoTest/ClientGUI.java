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

import server.ServerController;

public class ClientGUI extends JPanel implements ActionListener {
	private JPanel panel = new JPanel(new GridLayout(2, 2));
	private JButton On = new JButton("Sätt på Lampa 1");
	private JButton On2 = new JButton("Sätt på Lampa 2");
	private JButton Off = new JButton("Släck Lampa 1");
	private JButton Off2 = new JButton("Släck Lampa 2");
	private SendMessage send = new SendMessage();
	private ServerController server = new ServerController();

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
			send.Message("H2");
			server.logDatabase("Tände lampa 1", send.getHost(), "Kalle");
			server.setLockStatus("Lampa 1", "Tänd");
		}
		if (e.getSource() == On2) {
			send.Message("H3");
			server.logDatabase("Tände lampa 2", send.getHost(), "Kalle");
			server.setLockStatus("Lampa 2", "Tänd");
		}
		if (e.getSource() == Off) {
			send.Message("L2");
			server.logDatabase("Släckte lampa 1", send.getHost(), "Kalle");
			server.setLockStatus("Lampa 1", "Släckt");
		}
		if (e.getSource() == Off2) {
			send.Message("L3");
			server.logDatabase("Släckte lampa 2", send.getHost(), "Kalle");
			server.setLockStatus("Lampa 2", "Släckt");
		}

	}

}
