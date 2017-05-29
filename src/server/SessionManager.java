package server;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
/*
 * Hanndles the user sessions
 * 
 * @author Viktor Kullberg
 */
public class SessionManager {
	
	private MongoDBController controller;
	private Timer timer = new Timer();
	private ArrayList<SessionTask> sessionTasks = new ArrayList<SessionTask>(); 

	public SessionManager(MongoDBController controller) {
		this.controller = controller;
	}
	/*
	 * Starts a new session for a specific user
	 */
	public void start(String key, String id) {
		controller.addKey(key, id);
		SessionTask task = new SessionTask(id);
		sessionTasks.add(task);
		timer.schedule(task, 1000 * 60 * 10); // 10min
	}
	/*
	 * Removes the sessiontask
	 */
	private SessionTask removeUserSession(String user) {
		for(int i = 0; i < sessionTasks.size(); i++){
			if(sessionTasks.get(i).getUser().equals(user)){
				sessionTasks.get(i).cancel();
				return sessionTasks.remove(i);
			}
		}
		return null;
	}
	/*
	 * Removes the sessionkey and removes the sessiontask
	 */
	public void terminate(String id) {
		controller.removeKey(id);
		removeUserSession(id);
		//System.out.println(removeUserSession(id).getUser());
		System.out.println(sessionTasks.size());
	}

	/*
	 * Inner class that extends TimerTask to create a custom event to occur
	 */
	private class SessionTask extends TimerTask{
		private String id;
		public SessionTask(String id){
			this.id = id;
		}
		
		public String getUser(){
			return this.id;
		}
		
		public void run() {
			terminate(this.id);
		}
	}
}