package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import java.security.*;

public class ServerController {
	// private MongoClient mongoClient = new MongoClient("localhost", 27017);
	// private MongoDatabase database = mongoClient.getDatabase("test");
	// private MongoCollection<Document> logCollection =
	// database.getCollection("log");
	// private MongoCollection<Document> lockCollection =
	// database.getCollection("lockStatus");
	// private MongoCollection<Document> userCollection =
	// database.getCollection("users");
	// private ServerConnectivity test;
	private MongoDBController mongodb = new MongoDBController();
	private ArduinoController arduinocontroller = new ArduinoController(mongodb);
	private String responseMessage;
	private String[] message;
	private ArrayList<Session> list = new ArrayList<Session>();

	public ServerController() {
		new ServerConnectivity(25000, this);
	}

	// public void proccesData(String data, Socket socket) {
	// message = data.split(";");
	//
	// if (message[0].equals("log")) {
	// // skriv till databas här
	// mongodb.logDatabase(message[1], socket.getInetAddress().toString(),
	// message[2]);
	// // addLog(message[1], socket.getInetAddress().toString(),
	// // message[2]);
	// sendResponse("Logged action for " + message[1] + " by: " +
	// socket.getInetAddress().toString(), socket);
	// } else if (message[0].equals("lock")) {
	//
	// responseMessage =
	// arduinocontroller.sendRequest(mongodb.getChildIP(message[1]),
	// message[2]);
	// // skriv till databas här
	// mongodb.logLockStatus(message[1], message[2]);
	// sendResponse(responseMessage, socket);
	//
	// } else if (message[0].equals("scan")) {
	// responseMessage = arduinocontroller.sendRequest("255.255.255.255",
	// message[1]);
	// mongodb.addLock(responseMessage);
	// sendResponse(responseMessage, socket);
	// }
	//
	// else if (message[0].equals("get")) {
	//
	// // hämta från databas och skickar
	// sendResponse(mongodb.fetchLog(), socket);
	// }
	//
	// else if (message[0].equals("login")) {
	// // hämta från databas här
	// sendResponse(mongodb.verifyLogin(message[1], message[2]), socket);
	// }
	//
	// else if (message[0].equals("status")) {
	// sendResponse(mongodb.getLockStatus(), socket);
	// }
	//
	// else if (message[0].equals("create")) {
	// sendResponse(mongodb.createUser(message[1], message[2], message[3]),
	// socket);
	// }
	//
	// else if (message[0].equals("delete")) {
	// sendResponse(mongodb.removeUser(message[1]), socket);
	// }
	//
	// else if (message[0].equals("users")) {
	// sendResponse(mongodb.getUsers(), socket);
	//
	// } else {
	// sendResponse("Server couldnt process the data", socket);
	// }
	// }

	public void processData(String data, Socket socket) {
		// this.socket = socket;
		System.out.println("Message is: " + data);
		message = data.split(";");
		String commando = message[0];
		// 3 fall utan session key, 2 kommer från arduino och vid login
		if (commando.equals("login")) {
			String verify = mongodb.verifyLogin(message[1], message[2]);
			if (verify != "NOTOK") {
				String key = generateKey(); // genererar en session key
				Session ses = new Session(mongodb, message[1], key);// skapar
																	// timer för
																	// keyn
				ses.start();
				list.add(ses);
				sendResponse(verify + ";" + key + ";" + mongodb.getID(message[1]), socket); // skickar
				// tillbaka key
				// + id?
			} else {
				sendResponse(verify, socket);
			}
		} else if (commando.equals("hej")) {
			mongodb.addMasterLock(message[1], socket.getInetAddress().toString().substring(1), "parent");
			sendResponse("Ok from Server!,masterlock added!", socket);
		} else if (commando.equals("key")) { // någon vred med nyckel
//			logAction("låsnamn", message[2]);
			// mongodb.logLockStatus("låsnamn", message[2]);
			// mongodb.logDatabase("låsnamn", message[2], "nyckel");
		} else {
			if (mongodb.checkKey(message[0], message[1]).equals("OK")) {
				executeCommando(message[2], socket);
			} else {
				sendResponse("key not valid!", socket);
			}
		}
	}

	public void executeCommando(String commando, Socket socket) {
		switch (commando) {
		case "log":
//			mongodb.logDatabase(message[1], socket.getInetAddress().toString(), message[2]);
			sendResponse("Logged action for " + message[1] + " by: " + socket.getInetAddress().toString(), socket);
			break;
		case "lock":
			// responseMessage =
			// arduinocontroller.sendRequest(mongodb.getChildIP(message[1]),
			// message[2]);
			responseMessage = arduinocontroller.sendRequest(mongodb.findIP(message[4]), message[3]);
			if (responseMessage.equals("locked") || responseMessage.equals("unlocked")) {
				logAction(message[4], responseMessage, mongodb.getUsername(message[1]), socket);
				sendResponse(responseMessage, socket);
			} else {
				// mongodb.logLockStatus(message[1], message[2]);
				sendResponse(responseMessage, socket);
				System.out.println("responseMessage= " + responseMessage);
			}
			break;
		case "scan":
			responseMessage = arduinocontroller.sendRequest(mongodb.getParent(), "3");
			String[] macip = responseMessage.split(";");
			if(macip.length > 1) {
				mongodb.addLock(macip[0], macip[1], "child");
			}
			System.out.println("message recieve:" + responseMessage);
			sendResponse("lock added", socket);
			break;
		case "get":
			sendResponse(mongodb.fetchLog(), socket);
			break;
		case "logoff":
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getUser() == message[1]) {
					list.get(i).terminate(); // tar bort sessionkey
					Session session = list.remove(i); // tar bort
														// sessionobjektet
					sendResponse(session.getUser(), socket);
				}
			}
			sendResponse("nyckel borta", socket);
			break;
		case "status":
			sendResponse(mongodb.getLockStatus(), socket);
			break;
		case "create":
			sendResponse(mongodb.createUser(message[3], message[4], message[5]), socket);
			break;
		case "delete":
			sendResponse(mongodb.removeUser(message[3]), socket);
			break;
		case "user":
			sendResponse(mongodb.getUsers(), socket);
			break;
		case "hej":
			// mongodb.addLock(message[1], socket.getInetAddress().toString(),
			// "parent");
			// sendResponse("Ok from Server!,masterlock added!", socket);
			break;
		case "key":
			sendResponse("la till logg", socket);
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

	private String generateKey() {
		UUID id = UUID.randomUUID();
		String key = id.toString();
		return key;
	}
	
	private void hash(String password){
	}

	/*
	 * Creates and sends a response
	 */
	public void sendResponse(String message, Socket socket) {
		try {
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(message);
			System.out.println("Message sent to the client is :" + "\n" + message);
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
	}

	public static void main(String[] args) {
		new ServerController();
	}
}