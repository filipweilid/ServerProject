package server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;


import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;


public class Mongodbtest {
	
	MongoClient mongoClient = new MongoClient("localhost", 27017);
	MongoDatabase database = mongoClient.getDatabase("test");
	MongoCollection<Document> logCollection = database.getCollection("log");
	MongoCollection<Document> lockCollection = database.getCollection("lockStatus");
	MongoCollection<Document> userCollection = database.getCollection("users");
	
	public void test(){
//		String test = "";
//		MessageDigest digest;
//		try {
//			digest = MessageDigest.getInstance("SHA-256");
//			byte[] hash = digest.digest(test.getBytes(StandardCharsets.UTF_8));
//			String encoded = Base64.getEncoder().encodeToString(hash);
//			System.out.println(encoded);
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		//User user1 = new User("Viktor Kullberg", "test1234", "king");
//		user1.setUsername("Viktor Kullberg);
		Gson gson = new Gson();
		String json = gson.toJson(user1);
	    System.out.println("json stringen = "+ json);
	    // Parse to bson document and insert
	   Document doc = Document.parse(json);
	    
//	   Document doc = Document.parse(json);
	   System.out.println(doc.toString());
	   //Document test = (Document) new Document().put("test", gson.toJson(user1));
	    
	  userCollection.insertOne(doc);
	   //Document document2 = userCollection.find().first();
	  // User user = (User) new Gson().fromJson(document2.toJson(), User.class);
	   //System.out.println(user1.testing());
//	   System.out.println(document2.toString()+ " = document2 to string");
//	   String jsontest = document2.toJson();
//	   System.out.print(jsontest);
//	   User test = gson.fromJson(jsontest, User.class);
//	   System.out.println(test.getUsername());
	   // gson.fromJson(document2.toJson(), User.class).toString();
	   // System.out.println(user2.getUsername());
	    
	    //Document document = new Document("hej", "username");
	}
	
	private static String generateKey() {
		UUID id = UUID.randomUUID();
		String key = id.toString();
		return key;
	}
	
	public static String hashPassword(String password) {
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
	
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
//	Mongodbtest test = new Mongodbtest();
//	test.test();
//	MongoDBController test = new MongoDBController();
//	SessionManager sessionManager = new SessionManager(test);
//	test.createUser("test", hashPassword("hejhej"), "admin");
//	test.createUser("filippleb", hashPassword("testtest"), "pleb");
//	System.out.println(test.verifyLogin("test", hashPassword("hejhej")));
//	System.out.println(test.verifyLogin("filippleb", hashPassword("testtest")));
//	String testare =  "placeholder";
//	String testare2 = "filipärpleb";
//	sessionManager.start(testare, "test");
//	sessionManager.start(testare2, "filippleb");
//		System.out.println(controller.getID("a"));
//		String key = generateKey();
//		new Session(controller, "a", key).start();
//		System.out.println(controller.checkKey(key, "58f60863e9203a13ec26f944"));
//		User user = new User("hej", "hej", "hej");
		String test = generateKey();
		//test.getBytes();
		System.out.println(test+ "\n" + test.length());
		
		
		
		
//		String test = "testtesttest";
//		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//		messageDigest.update(test.getBytes());
//		String encryptedString = new String(messageDigest.digest());
//		System.out.println(encryptedString);
	}
}
