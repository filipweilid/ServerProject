package server;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

public class MongoDBTest {

   public static void main( String args[] ) {
	
	   MongoClient mongoClient = new MongoClient( "35.157.249.193" , 27017 );
	   MongoDatabase database = mongoClient.getDatabase("test");
	   MongoCollection<Document> collection = database.getCollection("test");
	   
	   Document doc = new Document("name", "MongoDB")
               .append("type", "database")
               .append("count", 1)
               .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
               .append("info", new Document("x", 203).append("y", 102));
	   
	   collection.insertOne(doc);
   }
}