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
import org.bson.types.ObjectId;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/*
 * Class that handles the database, including several methods
 * for retrieving or storing information in said database
 */
public class MongoDBController {
	private MongoClient mongoClient = new MongoClient("localhost", 27017);
	private MongoDatabase database = mongoClient.getDatabase("test");
	private MongoCollection<Document> logCollection = database.getCollection("log");
	private MongoCollection<Document> lockCollection = database.getCollection("lockStatus");
	private MongoCollection<Document> userCollection = database.getCollection("users");

	/*
	 * Logs data to the database
	 */
	public void logDatabase(String action, String ip, String username, String lock) {
		TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
		Calendar c = Calendar.getInstance(timeZone); 
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		simpleDateFormat.setTimeZone(timeZone);
		Document document = new Document("lock", lock).append("username", username).append("date", simpleDateFormat.format(c.getTime()))
				.append("ip", ip).append("action", action);
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
			String info = document.get("date") + ";" + document.getString("action") + ";"
					+ document.getString("username") + ";" + document.get("ip") + ";" + document.get("lock");
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
	 * Checks with database if username and password is correct
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
	 * Adds a sessionkey to a specific usernameobject in the dataname
	 */
	public void addKey(String key, String id) {
		ObjectId object = new ObjectId(id);
		userCollection.updateOne(eq("_id", object), set("sessionkey", key));
	}
	
	/*
	 * Removes the sessionkey from the specific userobject in the database
	 */
	public void removeKey(String id) {
		ObjectId object = new ObjectId(id);
		userCollection.updateOne(eq("_id", object), set("sessionkey", "default"));
	}
	
	/*
	 * Checks if the sessionkey for a specifik user is valid
	 */
	public String checkKey(String key, String id) {
		if(key.equals("default") || (id.length()!=24)){ //default= no key, id length must be 24
			return "NOTOK";
		}
		ObjectId object = new ObjectId(id);
		if (userCollection.find(and(eq("sessionkey", key), eq("_id", object))).first() != null) {
			return "OK";
		} else {
			return "NOTOK";
		}
	}
	
	/*
	 * Retrieves and returns the userobject id for a specific user as a String
	 */
	public String getID(String user) {
		Document document = userCollection.find(and(eq("username", user))).first();
		return document.getObjectId("_id").toHexString();
	}
	
	/*
	 * Retrieves and returns the username for soecifikt userobject
	 */
	public String getUsername(String id) {
		Document document = userCollection.find(eq("_id", new ObjectId(id))).first();
		return document.get("username").toString();
	}

	/*
	 * Changes the status of lock
	 */
	public void logLockStatus(String lock, String status) {
		lockCollection.updateOne(eq("lock", lock), set("status", status));
	}
	
	/*
	 * Retrieves the ip for a lock and returns it as a String
	 */
	public String findIP(String name){
		Document document = (Document) lockCollection.find(eq("lock", name)).first();
		return document.get("ip").toString();
	}

	/*
	 * Retrieves the lockstatus of all locks
	 */
	public String getLockStatus() {
		Iterator<Document> iter = lockCollection.find().iterator();
		String returnmessage = "";
		while (iter.hasNext()) {
			Document document = iter.next();
			returnmessage = returnmessage + document.getString("lock") + ";" + document.getString("status") + ";" +
			document.getBoolean("active") + ";";
		}
		return returnmessage;
	}
	
	/*
	 * Retrieves the ip of a childlock and returns it
	 */
	public String getChildIP(String lock) {
		Document document = (Document) lockCollection.find(and(eq("type", "child"), eq("lock", lock))).first();
		return document.getString("ip");
	}
	
	/*
	 * Retrieves the ip of the parent lock and returns it
	 */
	public String getParent() {
		Document document = (Document) lockCollection.find(eq("type", "parent")).first();
		return document.getString("ip");
	}
	
	/*
	 * Retrives and returns the macadress of a specified lock
	 */
	public String getMac(String lockname){
		Document document = lockCollection.find(eq("lock", lockname)).first();
		return document.getString("macadress");
	}
	
	/*
	 * Adds a specific lock to the database
	 */
	public String addLock(String mac, String ip, String type) {
		int length = (int) lockCollection.count();
		if(lockCollection.find(eq("macadress", mac)).first() == null) {
			Document document = new Document("lock", ("lock" + (length))).append("status", "locked").append("type", type)
					.append("ip", ip).append("macadress", mac).append("active", true);
			lockCollection.insertOne(document);
		} else {
			lockCollection.findOneAndUpdate(eq("macadress", mac), set("ip", ip));
		}
		return "OK";
	}
	
	/*
	 * Changes the active field of a lock, true/false
	 */
	public void changeActiveStatus(String mac, Boolean ActiveStatus) {
		lockCollection.findOneAndUpdate(eq("macadress", mac), set("active", ActiveStatus));
	}
	
	/*
	 * Edits the name of a specific lock
	 */
	public String editLock(String oldLock, String newLock) {
		if(newLock.length() > 10){ //cant have to long names
			return "NOTOK";
		}
		lockCollection.findOneAndUpdate(eq("lock", oldLock), set("lock", newLock));
		return "OK";
	}
	
	/*
	 * Edits a user
	 */
	public String editUser(String oldUsername, String newUsername, String password, String role) {
		if(newUsername.length() > 10){ //cant have to long names
			return "NOTOK";
		}
		if (userCollection.find(eq("username", oldUsername)).first() != null) { //checks if name is valid
			String key = userCollection.find(eq("username", oldUsername)).first().getString("sessionkey"); //fetches the sessionkey
			Document document = new Document("username", newUsername).append("password", password).append("role", role)
					.append("sessionkey", key);
			userCollection.findOneAndReplace(eq("username", oldUsername), document);
			return "OK";
		}
		return "NOTOK";
	}
	
	/*
	 * Adds a masterlock, only one master lock can be added
	 */
	public String addMasterLock(String mac, String ip, String type) {
		int length = (int) lockCollection.count(eq("type", "parent"));
		System.out.println(length);
		if (length < 1) { //prevents several masterlocks
			Document document = new Document("lock", "parent").append("status", "unlocked").append("type", "parent")
					.append("ip", ip).append("macadress", mac).append("active", true);;
			lockCollection.insertOne(document);
			return "OK";
		} else if(lockCollection.find(eq("macadress", mac)) != null) { //updates the current masterlock
			lockCollection.findOneAndUpdate(eq("macadress", mac), set("ip", ip));
			return "OK";
		}
		return "NOTOK";
	}

	// ***____________________ADMIN--METODER_______________***//

	/*
	 * Creates a new user
	 */
	public String createUser(String username, String password, String role) {
		if(username.length()> 10){ //name to long
			return "NOTOK";
		}
		if (userCollection.find(eq("username", username)).first() == null) {
			Document document = new Document("username", username).append("password", password).append("role", role)
					.append("sessionkey", "default");
			userCollection.insertOne(document);
			return "OK";
		}
		return "NOTOK";
	}

	/*
	 * Removes a user
	 */
	public String removeUser(String username) {
		if (userCollection.findOneAndDelete((eq("username", username))) != null) {
			return "OK";
		}
		return "NOTOK";
	}

	/*
	 * Retrieves all users
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
