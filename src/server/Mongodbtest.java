package server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import com.mongodb.Block;

import com.mongodb.util.JSON;

public class Mongodbtest {
	
	private static String generateKey() {
		UUID id = UUID.randomUUID();
		String key = id.toString();
		return key;
	}
	
	
	
	
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		MongoDBController controller = new MongoDBController();
		System.out.println(controller.getID("a"));
		String key = generateKey();
		new Session(controller, "a", key).start();
		System.out.println(controller.checkKey(key, "58f60863e9203a13ec26f944"));
		User user = new User("hej", "hej", "hej");
		Gson gson = new Gson();
		String json = gson.toJson(user);
//		String test = "testtesttest";
//		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//		messageDigest.update(test.getBytes());
//		String encryptedString = new String(messageDigest.digest());
//		System.out.println(encryptedString);
	}
}
