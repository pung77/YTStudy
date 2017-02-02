package Client;

import java.awt.Color;
import java.io.ObjectInputStream;

import Client.Client.Gui;

import common.Message;

public class ReceiveMessageHandler implements Runnable {
	private ObjectInputStream reader;
	private Gui gui;
	
	private String contents = "";

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
					contents += "[" + receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "]\n";
					gui.setChatText(contents);
					break;
				case Message.type_MESSAGE:
					contents += receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "\n";
					gui.setChatText(contents);
					break;
				case Message.type_LOGOUT:
					contents += "[" + receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "]\n";
					gui.setChatText(contents);
					break;
				case Message.type_WHISPER:
					gui.getChatText().setForeground(Color.red); // 빨간색으로 변경
					contents += receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "\n";
					gui.setChatText(contents);
					break;
				default:

				}

			} catch (Exception ex) {
			}

		}

	}

}
