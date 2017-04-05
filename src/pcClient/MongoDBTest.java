package pcClient;

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class MongoDBTest extends JPanel implements ActionListener {
	private JTextField tfUsername = new JTextField();
	private JTextField tfPassword = new JTextField();
	private JButton btnLogin = new JButton("Login");
	private JPanel grid = new JPanel(new GridLayout(2,1));
	private MongoClient mongoClient = new MongoClient("35.157.249.193", 27017);
	private MongoDatabase database = mongoClient.getDatabase("test");
	private MongoCollection<Document> collection = database.getCollection("users");
	
	public MongoDBTest() {
//		Document user = new Document("Username", "test").append("Password", "test");
//		collection.insertOne(user);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(500, 500));
		grid.add(tfUsername);
		grid.add(tfPassword);
		add(grid, BorderLayout.CENTER);
		add(btnLogin, BorderLayout.SOUTH);
		btnLogin.addActionListener(this);
	}
	
//	Block<Document> printBlock = new Block<Document>() {
//	       @Override
//	       public void apply(final Document document) {
//	           System.out.println("Success");
//	       }
//	};
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnLogin) {
			if(collection.find(and(eq("Username", tfUsername.getText()), eq("Username", tfPassword.getText()))).first() != null) {
				System.out.println("Success");
			}
			
//			MongoCursor<Document> cursor = collection.find(and(eq("Username", tfUsername.getText()), eq("Username", tfPassword.getText()))).iterator();
//			try {
//				while(cursor.hasNext()) {
//					System.out.println(cursor.next().toJson());
//				}
//			} finally {
//				cursor.close();
//			}
		}
	}
	
	public static void main(String args[]) {
		MongoDBTest test = new MongoDBTest();
		JFrame frame = new JFrame("MongoDB");
		frame.setLocation(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(test);
		frame.pack();
		frame.setVisible(true);
	}
}