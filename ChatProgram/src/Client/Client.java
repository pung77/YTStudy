package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import common.Message;


public class Client {		
	private Socket clientSocket;	
	private ObjectInputStream readStream;
	private ObjectOutputStream writeStream;
	
//	private Thread receiveThread;
//	private Thread sendThread;
	
	private Gui gui;
	
	// 소켓 생성 및 스레드 생성
	public void setting(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			readStream = new ObjectInputStream(clientSocket.getInputStream());
			
			gui = new Gui(readStream, writeStream);
//			gui.setStream(readStream, writeStream);
			gui.createThread();
			
//			sendThread = new Thread(new SendMessageHandler(writeStream));
//			receiveThread = new Thread(new ReceiveMessageHandler(readStream));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		login();	// 로그인
		
		gui.startThread();
//		receiveThread.start();
//		sendThread.start();
		
//		try {
////			receiveThread.join();
////			sendThread.join(); 
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}		
	
	private void login() {
		System.out.print("ID입력: ");
		String id = new Scanner(System.in).nextLine();
		
		try {
			writeStream.writeObject(new Message(Message.type_LOGIN, id));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		Client ct = new Client();
		ct.setting("localhost", 3000);
		ct.start();
	}

}
