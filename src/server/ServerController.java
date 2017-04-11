package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class ServerController {
	// private MongoClient mongoClient = new MongoClient("35.157.249.193",
	// 27017);
	// private MongoDatabase database = mongoClient.getDatabase("test");
	// private MongoCollection<Document> logCollection =
	// database.getCollection("log");
	// private MongoCollection<Document> lockCollection =
	// database.getCollection("lockStatus");
	TestServer test;

	public ServerController() {
		test = new TestServer(25000, this);
	}

	// public void logDatabase(String text, String ip, String username) {
	// username = "Kalle";
	// TimeZone timeZone = TimeZone.getTimeZone("GMT+2");
	// Calendar c = Calendar.getInstance(timeZone);
	// SimpleDateFormat simpleDateFormat =
	// new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy", Locale.US);
	// simpleDateFormat.setTimeZone(timeZone);
	// Document document = new Document("username", username)
	// .append("date", simpleDateFormat.format(c.getTime()))
	// .append("ip", ip)
	// .append("message", text);
	//
	//
	// logCollection.insertOne(document);
	// }

	public void proccesData(String data, Socket socket) {
		String[] message = data.split(";");
		if (message[0].equals("log")) {
			
			//skriv till databas här
			
			sendResponse("Logged action for "+ message[1] + " by: " + socket.getInetAddress().toString(), socket);
		} else if (message[0].equals("lock")) {
			
			//skriv till databas här
			
			sendResponse("Lock status changed for: "+  message[1] + "to: "+ message[2], socket);
		}else if(message[0].equals("get")){
			
			//hämta från databas
			sendResponse("Log sent! "+  fetchLog(), socket);
		}else{
			sendResponse("Server couldnt process the data", socket);
		}
	}
	
//	public String returnMessage(){
//		
//		return
//	}

	public void sendResponse(String message, Socket socket) {
		try {
			OutputStream os = socket.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os);
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(message);
			System.out.println("Message sent to the client is :"+ "\n" + message);
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (Exception e) {
			}
		}
		
	}
	
	public String fetchLog(){
//		Document document = new Document("lock1", "på");
//		Document document2 = new Document("lock1", "av");
		ArrayList<Document> documents= new ArrayList<Document>();
		for(int i = 0; i < 100; i++){
			documents.add(new Document("lock1", i));
		}
//		documents.add(document);
//		documents.add(document2);
		String returnmessage = "";
		for(int i = 0; i < documents.size(); i++){
			returnmessage = returnmessage + documents.get(i).toString() + ":";
		}
		return returnmessage;
	}


	// public void logLockStatus(String lock, String status){
	// lockCollection.updateOne(
	// eq("lock", lock),
	// set("status", status));

	// Document document = new Document("lock", lock)
	// .append("status", status);
	//
	// lockCollection.insertOne(document);
	// }

	public static void main(String[] args) {
		new ServerController();
	}
}