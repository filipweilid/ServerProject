package server;


public class User{
	
	private String username;
	private String password;
	private Session session;
	private String role;
	
	public String getUsername(){
		return username;
	}
	
	public void setUsername(String username){
		this.username = username;
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
