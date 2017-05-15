package server;

import java.io.Serializable;

public class User{

	private String username;
	private String role;
	private String password;
	private String sessionKey = null;
	
	public User(String username, String password, String role){
		this.username = username;
		this.role = role;
		this.password = password;
	}
	
	public String getUsername(){
		return username;
	}
	public String testing(){
		return username + role + password;
	}
}
