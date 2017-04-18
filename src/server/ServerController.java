package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.DatabaseMetaData;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class ServerController {
	private MongoClient mongoClient = new MongoClient("83.249.20.194", 27017);
	private MongoDatabase database = mongoClient.getDatabase("test");
	private MongoCollection<Document> logCollection = database.getCollection("log");
	private MongoCollection<Document> lockCollection = database.getCollection("lockStatus");
	private MongoCollection<Document> userCollection = database.getCollection("users");
	ServerConnectivity test;

	public ServerController() {
		test = new ServerConnectivity(25000, this);
	}

	public void proccesData(String data, Socket socket) {
		String[] message = data.split(";");
		if (message[0].equals("log")) {

			// skriv till databas här
			logDatabase(message[1], socket.getInetAddress().toString(), message[2]);
			// addLog(message[1], socket.getInetAddress().toString(),
			// message[2]);
			sendResponse("Logged action for " + message[1] + " by: " + socket.getInetAddress().toString(), socket);
		} else if (message[0].equals("lock")) {

			// skriv till databas här
			logLockStatus(message[1], message[2]);
			sendResponse("Lock status changed for: " + message[1] + "to: " + message[2], socket);
		} else if (message[0].equals("get")) {

			// hämta från databas
			sendResponse(fetchLog(), socket);
		} 

		else if (message[0].equals("login")) {

			// hämta från databas här
			sendResponse(verifyLogin(message[1], message[2]), socket);
		}

		else if (message[0].equals("status")) {
			sendResponse(getLockStatus(), socket);
		}

		else if (message[0].equals("create")) {
			sendResponse(createUser(message[1], message[2], message[3]), socket);
		}

		else if (message[0].equals("users")) {

			sendResponse(getUsers(), socket);
		} else {
			sendResponse("Server couldnt process the data", socket);
		}
	}

	/*
	 * Sends a response
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}

	}

	/*
	 * loggar data till servern
	 */
	public void logDatabase(String text, String ip, String username) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
		Calendar c = Calendar.getInstance(timeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		simpleDateFormat.setTimeZone(timeZone);
		Document document = new Document("username", username).append("date", simpleDateFormat.format(c.getTime()))
				.append("ip", ip).append("message", text);
		logCollection.insertOne(document);
	}

	/*
	 * Fetches and returns the log as a string
	 */
	public String fetchLog() {
		Iterator<Document> iter = logCollection.find().iterator();
		String returnmessage = "";
		while (iter.hasNext()) {
			Document document = iter.next();
			String info = document.get("date") + ";" + document.getString("message") + ";"
					+ document.getString("username") + ";" + document.get("ip");
			returnmessage = returnmessage + info + ";";

		}
		return returnmessage;
	}

	/*
	 * Fetches log for a specific user
	 */
	public String fetchLogForUser(String username) {
		Iterator<Document> iter = logCollection.find(eq("username", username)).iterator();
		String returnmessage = "";
		// for(int i = 0; i < documents.size(); i++){
		// returnmessage = returnmessage + documents.get(i).toJson() + ";";
		// }
		while (iter.hasNext()) {
			Document document = iter.next();
			String info = document.get("date") + ";" + document.getString("message") + ";"
					+ document.getString("username") + ";" + document.get("ip");
			returnmessage = returnmessage + info + ";";
		}
		return returnmessage;
	}

	/*
	 * checks with database if username and password is correct
	 */
	public String verifyLogin(String user, String password) {
		if (userCollection.find(and(eq("username", user), eq("password", password))).first() != null) {
			System.out.println("Success");
			return "OK;" + userCollection.find(eq("username", user)).first().getString("role");
		} else {
			return "NOTOK";
		}
	}

	/*
	 * Changes the status of lock
	 */
	public void logLockStatus(String lock, String status) {
		lockCollection.updateOne(eq("lock", lock), set("status", status));
	}

	/*
	 * Retrieves the lockstatus of a certain lock
	 */
	public String getLockStatus() {
		return lockCollection.find(eq("lock", "lock")).first().getString("status");
	}

	// ***____________________ADMIN--METODER_______________***//

	/*
	 * creates new user
	 */
	public String createUser(String username, String password, String role) {
		if (userCollection.find(eq("username", username)).first() == null) {
			Document document = new Document("username", username).append("password", password).append("role", role);
			userCollection.insertOne(document);
			return "OK";
		}
		return "NOTOK";
	}

	/*
	 * removes a user
	 */
	public void removeUser(String username) {
		userCollection.findOneAndDelete((eq("username", username)));
	}

	/*
	 * 
	 */
	public String getUsers() {
		Iterator<Document> iter = userCollection.find().iterator();
		String returnmessage = "";
		while (iter.hasNext()) {
			Document document = iter.next();
			returnmessage = returnmessage + document.getString("username") + ";" + document.getString("role") + ";";
		}
		return returnmessage;
	}

	// ***_______________________________________________***//

	public static void main(String[] args) {
		new ServerController();
	}
}