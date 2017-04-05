package server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.bson.Document;
import com.mongodb.*;
import com.mongodb.client.*;

public class ServerController {
	private MongoClient mongoClient = new MongoClient("35.157.249.193", 27017);
	private MongoDatabase database = mongoClient.getDatabase("test");
	private MongoCollection<Document> collection = database.getCollection("log");
	
	public ServerController() {
		TestServer test = new TestServer(25000, this);
	}
	
	public void logDatabase(String text, String ip, String username) {
		username = "Kalle";
		TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
		Calendar c = Calendar.getInstance(timeZone);
		SimpleDateFormat simpleDateFormat = 
			       new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
			simpleDateFormat.setTimeZone(timeZone);
		Document document = new Document("username", username)
	               .append("date", simpleDateFormat.format(c.getTime()))
	               .append("ip", ip)
	               .append("message", text);
	                                       

	collection.insertOne(document);
	}
	
	public static void main(String[] args) {
		new ServerController();
	}
}
