package server;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
/*
 * Class that handles the database
 */
public class MongoDBController {
	private MongoClient mongoClient = new MongoClient("localhost", 27017);
	private MongoDatabase database = mongoClient.getDatabase("test");
	private MongoCollection<Document> logCollection = database.getCollection("log");
	private MongoCollection<Document> lockCollection = database.getCollection("lockStatus");
	private MongoCollection<Document> userCollection = database.getCollection("users");
	
	
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
		//return lockCollection.find(eq("lock", "lock")).first().getString("status");
		Iterator<Document> iter = lockCollection.find().iterator();
		String returnmessage = "";
		while (iter.hasNext()) {
			Document document = iter.next();
			returnmessage = returnmessage + document.getString("lock")+ ";" + document.getString("status")+";";
		}
		return returnmessage;
	}
	
	public String getChildIP(String lock){
		Document document = (Document) lockCollection.find(and(eq("type", "child"), eq("lock", lock)));
		return document.getString("IP");
	}
	
	 public String getParent(){
		 Document document = (Document) lockCollection.find(eq("type", "Parent"));
		return document.getString("IP");
	 }
	 
	 public String addLock(String mac, String ip ,String type){
		 int length = (int) lockCollection.count();
		 Document document = new Document("lock", ("lock"+(length))).append("status", "open")
				 .append("type", type).append("ip", ip).append("macadress", mac);
		 lockCollection.insertOne(document);
		 return "OK";
	 }
	 
	 public String addMasterLock(String mac, String ip, String type){
		 int length = (int)lockCollection.count(eq("type", "parent"));
		 if(length<1){
		 Document document = new Document("lock", "master").append("status", "open").append("type", "parent")
				 .append("ip", ip).append("macadress", mac);
		 lockCollection.insertOne(document);
		 return "OK";
		 }
		 return "NOT OK";
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
	 * removes a user FIXA DENNA METODEN
	 */
	public String removeUser(String username) {
		if(userCollection.findOneAndDelete((eq("username", username)))!= null){
			return "OK";
		}
		return "NOTOK";
	}

	/*
	 * Retrieves a user
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
	
}
