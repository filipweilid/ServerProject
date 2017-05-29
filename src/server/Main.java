package server;
/*
 * Starts the server
 */
public class Main {
	public static void main(String[] args) {
		ServerController controller = new ServerController();
		ServerConnectivity connect = new ServerConnectivity(25000, controller);
	}
}
