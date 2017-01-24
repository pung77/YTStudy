package Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

import common.Message;


public class SendMessageHandler implements Runnable, KeyListener {
	private static ObjectOutputStream writeStream;
	private Gui gui;
	
	private String msg;
	private boolean connected = false;
	
	public SendMessageHandler(ObjectOutputStream writeStream, Gui gui) {
		this.writeStream = writeStream;
		this.gui = gui;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(250);
				if (this.connected) {
					try {
						msg = gui.getChatLine();
						if (msg != null && msg.length() > 0) {
							writeStream.writeObject(new Message(Message.type_MESSAGE, msg));
							writeStream.flush();
							
							gui.setChatLine("");
							this.connected = false;
						}
					} catch (IOException e) {
						System.err.println(e);
					}
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static ObjectOutputStream getWriteStream() {
		return writeStream;
	}

	public void setWriteStream(ObjectOutputStream writeStream) {
		SendMessageHandler.writeStream = writeStream;
	}


	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.connected = true;
		}
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
