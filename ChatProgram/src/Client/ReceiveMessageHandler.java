package Client;

import java.awt.Color;
import java.awt.event.ActionListener;
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
					case Message.type_MESSAGE:
						setContents(getContents() + (receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "\n"));
						gui.setChatText(getContents());
						break;
					case Message.type_WHISPER:
						gui.getChatText().setForeground(Color.red); // 빨간색으로 변경
						setContents(getContents() + (receiveMessage.getSender() + ": " + receiveMessage.getMessage() + "\n"));
						gui.setChatText(getContents());
						break;
					default:
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
