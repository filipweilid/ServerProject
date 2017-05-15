package server;

public class User {
	private String username;
	private String password;
	private String sessionkey;
	private String role;
	
	public User(String username, String password, String role){
		this.username = username;
		this.password = password;
		this.role = role;
		this.sessionkey = "default";
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
	
	public void setSessionkey(String sessionkey){
		this.sessionkey = sessionkey;
	}
}
