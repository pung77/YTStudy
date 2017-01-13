package Client;

import java.io.ObjectInputStream;

import common.Message;


public class ReceiveMessageHandler implements  Runnable{
	private ObjectInputStream reader;		
	
	public ReceiveMessageHandler(ObjectInputStream reader)
	{
		this.reader = reader;
	}
	
	public void run()
	{
		while(true)
		{
			try {
				Message receiveMessage =(Message)reader.readObject();
				
				switch(receiveMessage.getMessageType())
				{
					case Message.type_MESSAGE:
						System.out.printf("%s : %s\n", receiveMessage.getSender(), receiveMessage.getMessage());
						break;
					default:					
				}
			
			} catch(Exception ex) {
    		}
			
			
		}
		
	}

}
