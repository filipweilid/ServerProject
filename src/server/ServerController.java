package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class ServerController {
//	private MongoClient mongoClient = new MongoClient("localhost", 27017);
//	private MongoDatabase database = mongoClient.getDatabase("test");
//	private MongoCollection<Document> logCollection = database.getCollection("log");
//	private MongoCollection<Document> lockCollection = database.getCollection("lockStatus");
//	private MongoCollection<Document> userCollection = database.getCollection("users");
	ServerConnectivity test;
	ArduinoController arduinocontroller = new ArduinoController();
	MongoDBController mongodb= new MongoDBController();

	public ServerController() {
		this.test = new ServerConnectivity(25000, this);
	}

	public void proccesData(String data, Socket socket) {
		String[] message = data.split(";");
		if (message[0].equals("log")) {
			// skriv till databas här
			
			mongodb.logDatabase(message[1], socket.getInetAddress().toString(), message[2]);
			// addLog(message[1], socket.getInetAddress().toString(),
			// message[2]);
			sendResponse("Logged action for " + message[1] + " by: " + socket.getInetAddress().toString(), socket);
		} else if (message[0].equals("lock")) {
			
			arduinocontroller.sendRequest(, );
			// skriv till databas här
			mongodb.logLockStatus(message[1], message[2]);
			sendResponse("Lock status changed for: " + message[1] + "to: " + message[2], socket);
		} else if (message[0].equals("get")) {

			// hämta från databas och skickar
			sendResponse(mongodb.fetchLog(), socket);
		} 

		else if (message[0].equals("login")) {
			// hämta från databas här
			sendResponse(mongodb.verifyLogin(message[1], message[2]), socket);
		}

		else if (message[0].equals("status")) {
			sendResponse(mongodb.getLockStatus(), socket);
		}
		
		else if (message[0].equals("create")) {
			sendResponse(mongodb.createUser(message[1], message[2], message[3]), socket);
		}
		
		else if (message[0].equals("delete")) {
			sendResponse(mongodb.removeUser(message[1]), socket);
		}
		
		else if (message[0].equals("users")) {
			sendResponse(mongodb.getUsers(), socket);
			
		} else {
			sendResponse("Server couldnt process the data", socket);
		}
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