package common;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int type_LOGIN = 1;
	public static final int type_MESSAGE = 2;
	public static final int type_LOGOUT = 3;
	public static final int type_WHISPER = 4;
	
	private int messageType; 
	private String sender; 
	private String receiver;
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
	
	public Message(int type, String message, String sender, String receiver)
	{
		this.messageType = type;
		this.sendMessage = message; 
		this.sender = sender;
		this.receiver = receiver;
	}
	
	public int getMessageType()
	{
		return messageType;
	}
	
	public void setMessage(String str)
	{
		sendMessage = str;
	}
	
	public String getMessage()
	{
		return sendMessage;
	}
	
	public void setSender(String str)
	{
		sender = str;
	}
	
	public String getSender()
	{
		return sender;
	}
	
	public void setReceiver(String str)
	{
		receiver = str;
	}
	
	public String getReceiver()
	{
		return receiver;
	}
	
	public Boolean IsExistReceiver()
	{
		if(receiver == "")
			return false;
		
		return true;
	}
	
}
