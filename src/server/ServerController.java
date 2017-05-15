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
	private Socket socket;

	public ServerController() {
		new ServerConnectivity(25000, this);
	}

	public void processData(String data, Socket socket) {
		this.socket = socket;
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
				sendResponse(verify + ";" + key + ";" + mongodb.getID(message[1])); // skickar
				// tillbaka key
				// + id?
			} else {
				sendResponse(verify);
			}
		} else if (commando.equals("hej")) {
			mongodb.addMasterLock(message[1], socket.getInetAddress().toString().substring(1), "parent");
			sendResponse("Ok from Server!,masterlock added!");
		} else if (commando.equals("key")) { // någon vred med nyckel
//			logAction("låsnamn", message[2]);
			// mongodb.logLockStatus("låsnamn", message[2]);
			// mongodb.logDatabase("låsnamn", message[2], "nyckel");
		} else {
			if (mongodb.checkKey(message[0], message[1]).equals("OK")) {
				executeCommando(message[2]);
			} else {
				sendResponse("key not valid!");
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void executeCommando(String commando) {
		switch (commando) {
		case "log":
//			mongodb.logDatabase(message[1], socket.getInetAddress().toString(), message[2]);
			sendResponse("Logged action for " + message[1] + " by: " + socket.getInetAddress().toString());
			break;
		case "lock":
			// responseMessage =
			// arduinocontroller.sendRequest(mongodb.getChildIP(message[1]),
			// message[2]);
			responseMessage = arduinocontroller.sendRequest(mongodb.findIP(message[4]), message[3]);
			if (responseMessage.equals("locked") || responseMessage.equals("unlocked")) {
				logAction(message[4], responseMessage, mongodb.getUsername(message[1]), socket);
				sendResponse(responseMessage);
			} else {
				// mongodb.logLockStatus(message[1], message[2]);
				sendResponse(responseMessage);
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
			sendResponse("lock added");
			break;
		case "get":
			sendResponse(mongodb.fetchLog());
			break;
		case "logout":
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getUser() == message[1]) {
					list.get(i).terminate(); // tar bort sessionkey
					Session session = list.remove(i); // tar bort
														// sessionobjektet
					sendResponse(session.getUser());
				}
			}
			sendResponse("nyckel borta");
			break;
		case "status":
			sendResponse(mongodb.getLockStatus());
			break;
		case "create":
			sendResponse(mongodb.createUser(message[3], message[4], message[5]));
			break;
		case "delete":
			sendResponse(mongodb.removeUser(message[3]));
			break;
		case "user":
			sendResponse(mongodb.getUsers());
			break;
		case "hej":
			// mongodb.addLock(message[1], socket.getInetAddress().toString(),
			// "parent");
			// sendResponse("Ok from Server!,masterlock added!", socket);
			break;
		case "key":
			sendResponse("la till logg");
			break;
		default:
			sendResponse("Server couldnt process the data");
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
	
	
	/*
	 * Creates and sends a response
	 */
	public void sendResponse(String message) {
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