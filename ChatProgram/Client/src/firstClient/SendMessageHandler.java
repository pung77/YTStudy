package firstClient;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;


public class SendMessageHandler implements Runnable {
	private ObjectOutputStream writeStream;
	
	public SendMessageHandler(ObjectOutputStream writeStream) {
		this.writeStream = writeStream;
	}

	public void run() {
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			try {
				String msg = sc.nextLine();

				writeStream.writeObject(new Message(Message.type_MESSAGE, msg));
				writeStream.flush();
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
