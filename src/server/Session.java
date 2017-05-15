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
		System.out.println(key);
		timer.schedule(task, 1000*60*10); //3min
	}
	
	public String getUser(){
		return user;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public void terminate(){
		this.key = "default";
	}
}