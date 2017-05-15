package server;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Session {
	
	private MongoDBController controller;
	private String user;
	private String key;
	
	public Session(MongoDBController controller, String user, String key){
		this.controller = controller;
		this.user = user;
		this.key = key;
	}
	
	private Timer timer = new Timer();
	TimerTask task = new TimerTask(){
		public void run(){
			terminate();
			task.cancel();
			}
	};

	public void start(){
		///timer.scheduleAtFixedRate(task, 100000, 10000);
		controller.addKey(key,user);
		System.out.println(key);
		timer.schedule(task, 1000*30*1); //3min
	}
	
	public String getUser(){
		return user;
	}
	
	public void terminate(){
		controller.removeKey(user);
	}
}