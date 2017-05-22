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
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

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

	public void addKey(String key, String username) {
		ObjectId object = new ObjectId(username);
		userCollection.updateOne(eq("_id", object), set("sessionkey", key));
	}

	public void removeKey(String username) {
		ObjectId object = new ObjectId(username);
		userCollection.updateOne(eq("_id", object), set("sessionkey", "default"));
	}

	public String checkKey(String key, String id) {
		if(key.equals("default") || (id.length()!=24)){ //hack
			return "NOTOK";
		}
		ObjectId object = new ObjectId(id);
		if (userCollection.find(and(eq("sessionkey", key), eq("_id", object))).first() != null) {
			return "OK";
		} else {
			return "NOTOK";
		}
	}

	public String getID(String user) {
		Document document = userCollection.find(and(eq("username", user))).first();
		return document.getObjectId("_id").toHexString();
	}
	
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
	
	public String findIP(String name){
		if(lockCollection.find(eq("lock", name)).first() != null) {
			Document document = (Document) lockCollection.find(eq("lock", name)).first();
			return document.get("ip").toString();
		}
		return "NOTOK";
	}

	/*
	 * Retrieves the lockstatus of a certain lock
	 */
	public String getLockStatus() {
		// return lockCollection.find(eq("lock",
		// "lock")).first().getString("status");
		Iterator<Document> iter = lockCollection.find().iterator();
		String returnmessage = "";
		while (iter.hasNext()) {
			Document document = iter.next();
			returnmessage = returnmessage + document.getString("lock") + ";" + document.getString("status") + ";" +
			document.getBoolean("active") + ";";
		}
		return returnmessage;
	}

	public String getChildIP(String lock) {
		Document document = (Document) lockCollection.find(and(eq("type", "child"), eq("lock", lock))).first();
		return document.getString("ip");
	}

	public String getParent() {
		Document document = (Document) lockCollection.find(eq("type", "parent")).first();
		return document.getString("ip");
	}
	
	public String getMac(String lockname){
		if(lockCollection.find(eq("lock", lockname)).first() != null) {
			Document document = lockCollection.find(eq("lock", lockname)).first();
			return document.getString("macadress");
		}
		return "NOTOK";
	}

	public String getLockName(String ip) {
		Document document = (Document) lockCollection.find(eq("ip", ip)).first();
		return document.getString("lock");
	}
	
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
	
	public void changeActiveStatus(String mac, Boolean ActiveStatus) {
		lockCollection.findOneAndUpdate(eq("macadress", mac), set("active", ActiveStatus));
	}
	
	public String editLock(String oldLock, String newLock) {
		if(newLock.length() > 15){ //för långa namn buggar appen
			return "NOTOK";
		}
		lockCollection.findOneAndUpdate(eq("lock", oldLock), set("lock", newLock));
		return "OK";
	}
	
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
	
	public String editUser(String oldUsername, String newUsername, String password, String role) {
		if(newUsername.length() > 10){ //för långa namn buggar appen
			return "NOTOK";
		}
		if (userCollection.find(eq("username", oldUsername)).first() != null) {
			String key = userCollection.find(eq("username", oldUsername)).first().getString("sessionkey"); //hämtar gamla sessionkeyn så 
			Document document = new Document("username", newUsername).append("password", password).append("role", role)
					.append("sessionkey", key);
			userCollection.findOneAndReplace(eq("username", oldUsername), document);
			return "OK";
		}
		return "NOTOK";
	}

	public String addMasterLock(String mac, String ip, String type) {
		int length = (int) lockCollection.count(eq("type", "parent"));
		System.out.println(length);
		if (length < 1) {
			Document document = new Document("lock", "parent").append("status", "unlocked").append("type", "parent")
					.append("ip", ip).append("macadress", mac).append("active", true);;
			lockCollection.insertOne(document);
			return "OK";
		} else if(lockCollection.find(eq("macadress", mac)) != null) {
			lockCollection.findOneAndUpdate(eq("macadress", mac), set("ip", ip));
			return "OK";
		}
		return "NOTOK";
	}

	// ***____________________ADMIN--METODER_______________***//

	/*
	 * creates new user
	 */
	public String createUser(String username, String password, String role) {
		if(username.length()> 10){
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
	 * removes a user FIXA DENNA METODEN
	 */
	public String removeUser(String username) {
		if (userCollection.findOneAndDelete((eq("username", username))) != null) {
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
	
	
//	public void insertUser(User user){
//		
//		userCollection.insert();
//	}

}
