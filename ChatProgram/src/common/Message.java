package common;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int type_LOGIN = 1;
	public static final int type_MESSAGE = 2;
	public static final int type_LOGOUT = 3;
	
	private int messageType; 
	private String sender; 
	private String sendMessage;
		
	public Message(int type, String message)
	{
		this(type, message, null);
	}
	
	public Message(int type, String message, String sender)
	{
		this.messageType = type;
		this.sendMessage = message; 
		this.sender = sender;
	}
	
	public int getMessageType()
	{
		return messageType;
	}
	
	public String getMessage()
	{
		return sendMessage;
	}
	
	public String getSender()
	{
		return sender;
	}
	
}
