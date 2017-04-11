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
	TestServer test;

	public ServerController() {
		test = new TestServer(25000, this);
	}

	public void logDatabase(String text, String ip, String username) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
		Calendar c = Calendar.getInstance(timeZone);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		simpleDateFormat.setTimeZone(timeZone);
		Document document = new Document("username", username).append("date", simpleDateFormat.format(c.getTime()))
				.append("ip", ip).append("message", text);

		logCollection.insertOne(document);
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
			sendResponse("L" + fetchLog(), socket);
		} else if (message[0].equals("user")) {

			// hämta från databas här
			sendResponse("Login is :" + verifyLogin(message[1], message[2]), socket);
		}

		else {
			sendResponse("Server couldnt process the data", socket);
		}
	}

	public String verifyLogin(String user, String password) {
		if(userCollection.find(and(eq("Username", user), eq("Username", password))).first() != null) {
			System.out.println("Success");
			return "OK";
		}else{
			return "NOTOK";
		}
	}

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

	// public void addLog(String text, String ip, String username){
	// documents.add(new Document(text, username + " :" + ip));
	// }

	public String fetchLog() {
		Iterator iter = logCollection.find().iterator();
		String returnmessage = "";
		// for(int i = 0; i < documents.size(); i++){
		// returnmessage = returnmessage + documents.get(i).toJson() + ";";
		// }
		while (iter.hasNext()) {
			returnmessage = returnmessage + iter.next().toString() + ";";
		}
		return returnmessage;
	}

	public void logLockStatus(String lock, String status) {
		lockCollection.updateOne(eq("lock", lock), set("status", status));
		Document document = new Document("lock", lock).append("status", status);
		lockCollection.insertOne(document);
	}

	public static void main(String[] args) {
		new ServerController();
	}
}