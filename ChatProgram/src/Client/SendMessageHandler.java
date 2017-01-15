package Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

import common.Message;


public class SendMessageHandler extends Gui implements Runnable, KeyEventListener, KeyListener {
	private ObjectOutputStream writeStream;
	
	public SendMessageHandler(ObjectOutputStream writeStream) {
		super();
		this.writeStream = writeStream;
	}
	
	public void run() {
		Scanner sc = new Scanner(System.in);
		
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
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			String msg = Gui.getChatLine();
			try {
				writeStream.writeObject(new Message(Message.type_MESSAGE, msg));
				writeStream.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			Gui.setChatLine("");
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
