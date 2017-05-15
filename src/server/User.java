package server;

public class User {
	private String username;
	private String password;
	private Session session;
	private String role;
	
	public User(String username, String password, String role){
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getRole(){
		return role;
	}
	
	public String checkPassword(String pasword){
		return "";
	}
	
	public void setSession(Session session){
		this.session = session;
	}
	
	public String getKey(){
		return "test";
	}
}
