package firstClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;


public class Client {		
	private Socket clientSocket = null;	
	private ObjectInputStream readStream = null;
	private ObjectOutputStream writeStream = null;
	
	private Thread receiveThread = null;
	private Thread sendThread = null;
	
	// 소켓 생성 및 스레드 생성
	void bind(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			readStream = new ObjectInputStream(clientSocket.getInputStream());
			
			login();	// 로그인 메시지 전송
			
			sendThread = new Thread(new sendMessageHandler(writeStream));
			receiveThread = new Thread(new receiveMessageHandler(readStream));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 채팅 시작
	void start() {
		// 각각의 받기/보내기 핸들러 start
		if (clientSocket != null) {
			sendThread.start();		// 보내기 스레드
			receiveThread.start();	// 받기 스레드
		}
	}
	
	void login() throws IOException {		
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
		ct.bind("localhost", 2222);
		ct.start();
	}

}
