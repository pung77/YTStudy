package Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import common.Message;

public class KeyEventListener implements KeyListener {
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			String msg = Gui.getChatLine();
			try {
				SendMessageHandler.getWriteStream().writeObject(new Message(Message.type_MESSAGE, msg));
//				writeStream.flush();
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
