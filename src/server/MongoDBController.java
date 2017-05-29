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

/**
 * Class that handles the database, including several methods
 * for retrieving or storing information in said database
 * 
 * @author Filip Weilid, Viktor Kullberg
 */
public class MongoDBController {
	private MongoClient mongoClient = new MongoClient("localhost", 27017);
	private MongoDatabase database = mongoClient.getDatabase("test");
	private MongoCollection<Document> logCollection = database.getCollection("log");
	private MongoCollection<Document> lockCollection = database.getCollection("lockStatus");
	private MongoCollection<Document> userCollection = database	.getCollection("users");

	/**
	 * Logs data to the database
	 * @param action The action we took
	 * @param ip The IP-address the user had
	 * @param username Username of the user
	 * @param lock Name of the lock that the user interacted with
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

	/**
	 * Fetches and returns the log as a string
	 * @return Returns a string with all the logs
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

	/**
	 * Fetches log for a specific user
	 * @return Returns a string of the logs from a certain user
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

	/**
	 * Checks with database if username and password is correct
	 * @param user The username we are trying to verify
	 * @param password The password we are trying to verify
	 * @return Returns a string verifying the login
	 */
	public String verifyLogin(String user, String password) {
		if (userCollection.find(and(eq("username", user), eq("password", password))).first() != null) {
			System.out.println("Success");
			return "OK;" + userCollection.find(eq("username", user)).first().getString("role");
		} else {
			return "NOTOK";
		}
	}
	
	/**
	 * Adds a sessionkey to a specific usernameobject in the database
	 * @param key The sessionkey we want to add to the database
	 * @param id The userID we want the sessionkey to be associated with
	 */
	public void addKey(String key, String id) {
		ObjectId object = new ObjectId(id);
		userCollection.updateOne(eq("_id", object), set("sessionkey", key));
	}
	
	/**
	 * Removes the sessionkey from the specific userobject in the database
	 * @param id The userID of the user whose sessionkey we want to delete
	 */
	public void removeKey(String id) {
		ObjectId object = new ObjectId(id);
		userCollection.updateOne(eq("_id", object), set("sessionkey", "default"));
	}
	
	/**
	 * Checks if the sessionkey for a specifik user is valid
	 * @param key The sessionkey we want to check
	 * @param id The userID that has the sessionkey
	 * @return Returns a string verifying whether or not the key is valid
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
	
	/**
	 * Retrieves and returns the userobject id for a specific user as a String
	 * @param user The username of the user whose userID we want to know
	 * @return Returns the userID of the user
	 */
	public String getID(String user) {
		Document document = userCollection.find(and(eq("username", user))).first();
		return document.getObjectId("_id").toHexString();
	}
	
	/**
	 * Retrieves and returns the username for soecifikt userobject
	 * @param id The userID of the user whose username we want to know
	 * @return Returns a string with the username
	 */
	public String getUsername(String id) {
		Document document = userCollection.find(eq("_id", new ObjectId(id))).first();
		return document.get("username").toString();
	}

	/**
	 * Changes the status of lock
	 * @param lock Name of the lock we want to change status in the log of
	 * @param status The status we want to change to
	 */
	public void logLockStatus(String lock, String status) {
		lockCollection.updateOne(eq("lock", lock), set("status", status));
	}
	
	/**
	 * Retrieves the ip for a lock and returns it as a String
	 * @param name The name of lock whose IP-address we want to know
	 * @return Returns the IP-address
	 */
	public String findIP(String name){
		if(lockCollection.find(eq("lock", name)).first() != null) {
			Document document = (Document) lockCollection.find(eq("lock", name)).first();
			return document.get("ip").toString();
		}
		return "NOTOK";
	}

	/**
	 * Retrieves the lockstatus of all locks
	 * @return Returns the status of all the locks
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
	
	/**
	 * Retrieves the ip of a childlock and returns it
	 * @param Name of childlock whose IP-address we want to know
	 * @return Returns the IP-address
	 */
	public String getChildIP(String lock) {
		Document document = (Document) lockCollection.find(and(eq("type", "child"), eq("lock", lock))).first();
		return document.getString("ip");
	}
	
	/**
	 * Retrieves the ip of the parent lock and returns it
	 * @return Returns the IP-address of the parentlock
	 */
	public String getParent() {
		Document document = (Document) lockCollection.find(eq("type", "parent")).first();
		return document.getString("ip");
	}
	
	/**
	 * Retrives and returns the macadress of a specified lock
	 * @param lockname The name of the lock whose MAC-address we want to know
	 * @return Returns the MAC-address
	 */
	public String getMac(String lockname){
		if(lockCollection.find(eq("lock", lockname)).first() != null) {
			Document document = lockCollection.find(eq("lock", lockname)).first();
			return document.getString("macadress");
		}
		return "NOTOK";
	}

	/**
	 * Retrives and returns the name of the lock with the specified IP-address
	 * @param ip The IP-address of the lock
	 * @return Returns the name of the lock
	 */
	public String getLockName(String ip) {
		Document document = (Document) lockCollection.find(eq("ip", ip)).first();
		return document.getString("lock");
	}
	
	/**
	 * Adds a lock to the database
	 * @param mac MAC-address of the lock we want to add
	 * @param ip IP-address of the lock we want to add
	 * @param type Type of the lock we want to add
	 * @return Returns OK when lock was added
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
	
	/**
	 * Changes the active field of a lock, true/false
	 * @param mac MAC-address of the lock we want to change active status
	 * @param ActiveStatus Boolean whether or not the lock is currently active and can be interacted with
	 */
	public void changeActiveStatus(String mac, Boolean ActiveStatus) {
		lockCollection.findOneAndUpdate(eq("macadress", mac), set("active", ActiveStatus));
	}
	
	/**
	 * Edits the name of a specific lock
	 * @param oldLock Current name of the lock
	 * @param newLock The name we want to change to
	 * @return Returns a string saying whether or not the change was accepted
	 */
	public String editLock(String oldLock, String newLock) {
		if(newLock.length() > 15){ //cant have to long name
			return "NOTOK";
		}
		lockCollection.findOneAndUpdate(eq("lock", oldLock), set("lock", newLock));
		return "OK";
	}
	
	/**
	 * Edits the password of the specified user
	 * @param username Username of the user whose password we want to change
	 * @param oldPassword The current password of that user
	 * @param newPassword The password we want to change to
	 * @return Returns a string saying if the change was accepted or not
	 */
	public String changePassword(String username, String oldPassword, String newPassword) {
		if (userCollection.find(eq("username", username)).first() != null) {
			Document document = userCollection.find(eq("username", username)).first();
			if(document.getString("password").equals(oldPassword)) {
				userCollection.findOneAndUpdate(eq("username", username), set("password", newPassword));
				return "OK";
			} else {
				return "NOTOK";
			}
		}
		return "NOTOK";
	}
	
	/**
	 * Adds a masterlock, only one master lock can be added
	 * @param mac MAC-address of the masterlock
	 * @param ip IP-address of the masterlock
	 * @param type Type which is master
	 * @return Returns a string saying if we could add the lock or not
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

	/**
	 * Creates a new user
	 * @param username The username of the user we want to create
	 * @param password The password of the user
	 * @param role The role of the user
	 * @return Returns a string whether or not we could create the user
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
	
	/**
	 * Edits a user
	 * @param oldUsername The current username of the user we want to edit
	 * @param newUsername The username we want to change to
	 * @param password The password we want to change to
	 * @param role The role we want to change to
	 * @return Returns a string saying if the change was accepted or not
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

	/**
	 * Removes a user
	 * @param username The username of the user we want to delete
	 * @return Returns a string saying if we could delete the user or not
	 */
	public String removeUser(String username) {
		if (userCollection.findOneAndDelete((eq("username", username))) != null) {
			return "OK";
		}
		return "NOTOK";
	}

	/**
	 * Retrieves all users
	 * @return Returns a string of all the users in the database
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
