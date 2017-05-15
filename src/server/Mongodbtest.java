package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
		User user1 = new User("hej", "hej", "hej");
		Gson gson = new Gson();
	    String json = gson.toJson(user1);
	    System.out.println("json stringen = "+ json);
	    // Parse to bson document and insert
	   // Document doc = Document.parse(json);
	    
	   Document doc = Document.parse(json);
	   System.out.println(doc.toString());
	   //Document test = (Document) new Document().put("test", gson.toJson(user1));
	    
	   userCollection.insertOne(doc);
	   Document document2 = userCollection.find().first();
	   User user = (User) new Gson().fromJson(document2.toJson(), User.class);
	   System.out.println(document2.toString()+ " = document2 to string");
	   String jsontest = document2.toJson();
	   System.out.print(jsontest);
	   User test = gson.fromJson(jsontest, User.class);
	   System.out.println(test.getUsername());
	   // gson.fromJson(document2.toJson(), User.class).toString();
	   // System.out.println(user2.getUsername());
	    
	    //Document document = new Document("hej", "username");
	}
	
	private static String generateKey() {
		UUID id = UUID.randomUUID();
		String key = id.toString();
		return key;
	}
	
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
	Mongodbtest test = new Mongodbtest();
//		System.out.println(controller.getID("a"));
//		String key = generateKey();
//		new Session(controller, "a", key).start();
//		System.out.println(controller.checkKey(key, "58f60863e9203a13ec26f944"));
//		User user = new User("hej", "hej", "hej");
		test.test();
		
		
		
//		String test = "testtesttest";
//		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//		messageDigest.update(test.getBytes());
//		String encryptedString = new String(messageDigest.digest());
//		System.out.println(encryptedString);
	}
}
