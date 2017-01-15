package Client;

import java.io.ObjectOutputStream;


public class SendMessageHandler implements Runnable {
	private static ObjectOutputStream writeStream;
	
	public SendMessageHandler(ObjectOutputStream writeStream) {
		super();
		this.setWriteStream(writeStream);
	}
	
	public void run() {
//		Scanner sc = new Scanner(System.in);
		
		while(true) {
//			try {
//				String msg = sc.nextLine();
//				String msg = Gui.getChatLine(); 
//
//				writeStream.writeObject(new Message(Message.type_MESSAGE, msg));
//				writeStream.flush();
//			} catch (IOException e) {
//				System.err.println(e);
//			}
		}
	}

	public static ObjectOutputStream getWriteStream() {
		return writeStream;
	}

	public void setWriteStream(ObjectOutputStream writeStream) {
		SendMessageHandler.writeStream = writeStream;
	}
}
