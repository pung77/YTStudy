package Client;

import java.io.ObjectInputStream;

import Client.Client.Gui;
import common.Message;

public class ReceiveMessageHandler implements Runnable {
	private ObjectInputStream reader;
	private Gui gui;
	
	private static String contents = "";

	public ReceiveMessageHandler(ObjectInputStream reader, Gui gui) {
		this.reader = reader;
		this.gui = gui;
	}

	public void run() {
		while (true) {
			try {
				Message receiveMessage = (Message) reader.readObject();

				switch (receiveMessage.getMessageType()) {
				case Message.type_LOGIN:
//					gui.getChatText().setForeground(Color.yellow); // color change
					contents += "[ " + receiveMessage.getSender() + ": " + receiveMessage.getMessage() + " ]\n";
					gui.setChatText(contents);
					break;
				case Message.type_MESSAGE:
//					gui.getChatText().setForeground(Color.blue); // color change
					contents += receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "\n";
					gui.setChatText(contents);
					break;
				case Message.type_LOGOUT:
//					gui.getChatText().setForeground(Color.yellow); // color change
					contents += "[ " + receiveMessage.getSender() + ": " + receiveMessage.getMessage() + " ]\n";
					gui.setChatText(contents);
					break;
				case Message.type_WHISPER:
//					gui.getChatText().setForeground(Color.red); // color change
					contents += receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "\n";
					gui.setChatText(contents);
					break;
				default:
					break;
				}

			} catch (Exception ex) {
			}

		}
	}

	public static String getContents() {
		return contents;
	}

	public static void setContents(String contents) {
		ReceiveMessageHandler.contents = contents;
	}

}
