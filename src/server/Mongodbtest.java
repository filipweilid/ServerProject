package server;

import java.util.UUID;

public class Mongodbtest {
	
	private static String generateKey() {
		UUID id = UUID.randomUUID();
		String key = id.toString();
		return key;
	}
	
	public static void main(String[] args) {
		MongoDBController controller = new MongoDBController();
		System.out.println(controller.getID("a"));
		String key = generateKey();
		new Session(controller, "a", key).start();
		System.out.println(controller.checkKey(key, "58f60863e9203a13ec26f944"));
	}
}
