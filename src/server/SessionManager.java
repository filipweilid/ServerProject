package server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
/*
 * Hanndles the user sessions
 */
public class SessionManager {
	
	private MongoDBController controller;
	private Timer timer = new Timer();
	private ArrayList<SessionTask> sessionTasks = new ArrayList<SessionTask>();

	public SessionManager(MongoDBController controller) {
		this.controller = controller;
	}

	public void start(String key, String user) {
		controller.addKey(key, user);
		System.out.println(key);
		SessionTask task = new SessionTask(user);
		sessionTasks.add(task);
		timer.schedule(task, 1000 * 30 * 1); // 3min
	}

	private SessionTask removeUserSession(String user) {
		for(int i = 0; i < sessionTasks.size(); i++){
			if(sessionTasks.get(i).getUser().equals(user)){
				sessionTasks.get(i).cancel();
				return sessionTasks.remove(i);
			}
		}
		return null;
	}

	public void terminate(String user) {
		controller.removeKey(user);
		System.out.println(removeUserSession(user).getUser());
	}


	private class SessionTask extends TimerTask{
		private String user;
		public SessionTask(String user){
			this.user = user;
		}
		
		public String getUser(){
			return this.user;
		}
		
		public void run() {
			terminate(this.user);
		}
	}
}