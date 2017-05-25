package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.security.*;

/*
 * Class that handles the logic in the server and determines what to do with incomming data
 * from the ServerConnectivity class. 
 * 
 */
public class ServerController {
	private MongoDBController mongodb = new MongoDBController();
	private ArduinoController arduinocontroller = new ArduinoController(mongodb);
	private SessionManager sessionManager = new SessionManager(mongodb);
	public ServerController() {
		new ServerConnectivity(25000, this);
	}

	public String processData(String data, Socket socket) {
		System.out.println("Message is: " + data);
		String[] message = data.split(";");
		String commando = message[0];
		// 3 fall utan session key, 2 kommer från arduino och vid login
		if (commando.equals("login")) {
			String verify = mongodb.verifyLogin(message[1], hashPassword(message[2]));
			if (!verify.equals("NOTOK")) {
				String key = generateKey(); // genererar en session key
				//SessionManager ses = new SessionManager(mongodb, message[1], key);// skapar
				sessionManager.start(key, mongodb.getID(message[1]));												// timer för
				sendResponse(verify + ";" + key + ";" + mongodb.getID(message[1]), socket); // skickar
				//return message[1];
				return mongodb.getID(message[1]);
				// tillbaka key
				// + id
			} else {
				sendResponse(verify, socket);
			}
		} else if (commando.equals("hej")) {
			mongodb.addMasterLock(message[1], socket.getInetAddress().toString().substring(1), "parent");
			mongodb.changeActiveStatus(message[1], true);
			sendResponse("Ok from Server!,masterlock added!", socket);
		} else if (commando.equals("key")) { // någon vred med nyckel
			logAction(mongodb.getLockName(socket.getInetAddress().toString().substring(1)), message[1], "key", socket);
			sendResponse("OK", socket);
		} else {
			if (message.length > 1 && mongodb.checkKey(message[0], message[1]).equals("OK")) {
				executeCommando(message[2], socket, message);
			} else {
				sendResponse("key not valid!", socket);
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	public void executeCommando(String commando, Socket socket, String[] message) {
		switch (commando) {
		case "log":
			// mongodb.logDatabase(message[1],
			// socket.getInetAddress().toString(), message[2]);
			sendResponse("Logged action for " + message[1] + " by: " + socket.getInetAddress().toString(), socket);
			break;
		case "lock":
			System.out.println(message[4]);
			// responseMessage =
			// arduinocontroller.sendRequest(mongodb.getChildIP(message[1]),
			// message[2]);
			String responseMessage = "";
			if(!mongodb.findIP(message[4]).equals("NOTOK")) {
				responseMessage = arduinocontroller.sendRequest(mongodb.findIP(message[4]), message[3]);
				if (responseMessage.equals("locked") || responseMessage.equals("unlocked")) {
					logAction(message[4], responseMessage, mongodb.getUsername(message[1]), socket);
				} else if(responseMessage.equals("The door is already unlocked")) {
					logAction(message[4], "unlocked", mongodb.getUsername(message[1]), socket);
				} else if(responseMessage.equals("The door is already locked")) {
					logAction(message[4], "locked", mongodb.getUsername(message[1]), socket);
				} else if(responseMessage.equals("error")) {
					//timeout
					mongodb.changeActiveStatus(mongodb.getMac(message[4]), false); //låset har timeat ut;
					// mongodb.logLockStatus(message[1], message[2]);
					System.out.println("responseMessage= " + responseMessage);
				}
			} else {
				responseMessage = "NOTOK";
			}
			sendResponse(responseMessage, socket);
			break;
		case "scan":
			responseMessage = arduinocontroller.sendRequest(mongodb.getParent(), "3");
			String[] macip = responseMessage.split(";");
			for(int i = 0; i < macip.length; i+=2) {
				if (macip.length > 1) {
					mongodb.addLock(macip[0+i], macip[1+i], "child");
					mongodb.changeActiveStatus(macip[0+i], true);
				}
			}
			if(macip.length > 1) {
				sendResponse("lock added", socket);
			} else {
				sendResponse(responseMessage, socket);
			}
		
			System.out.println("message recieve:" + responseMessage);
			break;
		case "get":
			sendResponse(mongodb.fetchLog(), socket);
			break;
		case "status":
			sendResponse(mongodb.getLockStatus(), socket);
			break;
		case "create":
			sendResponse(mongodb.createUser(message[3], hashPassword(message[4]), message[5]), socket);
			break;
		case "delete":
			sendResponse(mongodb.removeUser(message[3]), socket);
			break;
		case "user":
			sendResponse(mongodb.getUsers(), socket);
			break;
		case "editUser":
			 sendResponse(mongodb.editUser(message[3], message[4], hashPassword(message[5]), message[6]), socket);
			 break;
		case "editLock":
			 sendResponse(mongodb.editLock(message[3], message[4]), socket);
			 break;
		case "key":
			sendResponse("la till logg", socket);
			break;
		case "changePassword":
			sendResponse(mongodb.changePassword(message[3], hashPassword(message[4]), hashPassword(message[5])), socket);
			break;
		case "ping":
			
			
			break;
		default:
			sendResponse("Server couldnt process the data", socket);
			break;
		}
	}

	public void logAction(String lock, String status, String username, Socket socket) {
		mongodb.logDatabase(status, socket.getInetAddress().toString().substring(1), username, lock);
		mongodb.logLockStatus(lock, status);
	}
	
	public void endConnection(String user){
		sessionManager.terminate(user);
	}

	private String generateKey() {
		UUID id = UUID.randomUUID();
		String key = id.toString();
		return key;
	}

	public String hashPassword(String password) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			String encoded = Base64.getEncoder().encodeToString(hash);
			return encoded;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "error";
	}

	/*
	 * Creates and sends a response
	 */
	public void sendResponse(String message, Socket socket) {
		try {
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(message + "\n");
			System.out.println("Message sent to the client is :" + "\n" + message);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ServerController();
	}
}