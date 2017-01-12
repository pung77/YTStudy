package firstClient;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;


public class sendMessageHandler implements Runnable {
	private ObjectOutputStream writeStream;
	
	public sendMessageHandler(ObjectOutputStream writeStream) {
		super();
		this.writeStream = writeStream;
	}

	@Override
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
