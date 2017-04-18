package server;

public class Message {
	private String[] message;
	
	public Message(String message){
		this.message = message.split(";");
	}
	
	public String getFirst(){
		return message[0];
	}
	
	public String getSecond(){
		return message[1];
	}
	
	public String getThird(){
		return message[2];
	}
	
	public String getFourth(){
		return message[3];
	}
}
