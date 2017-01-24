package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import common.Message;

public class Client {
	private Socket clientSocket;
	private ObjectInputStream readStream;
	private ObjectOutputStream writeStream;

	private Thread receiveThread;
	private Thread sendThread;

	// 소켓 생성 및 스레드 생성
	public void bind(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			readStream = new ObjectInputStream(clientSocket.getInputStream());

			sendThread = new Thread(new SendMessageHandler(writeStream));
			receiveThread = new Thread(new ReceiveMessageHandler(readStream));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Login loginUI = new Login(); // 로그인

		receiveThread.start();
		sendThread.start();

		try {
			receiveThread.join();
			sendThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private class Login extends JFrame implements ActionListener {

		private JTextField tf;
		private JButton login;
		JLabel loginText = new JLabel();

		public Login() {
			JPanel idPanel = new JPanel();
			tf = new JTextField(12);
			loginText.setForeground(Color.RED);

			JLabel idLabel = new JLabel("ID : ");

			login = new JButton("LOGIN");
			login.addActionListener(this);

			idPanel.add(idLabel);
			idPanel.add(tf);

			this.add(idPanel);
			this.add(login);
			this.add(loginText);

			setLayout(new FlowLayout());

			setTitle("Login");
			setSize(280, 100);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			loginText.setText("ID를 입력하시오");

			setVisible(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String id = login.getText();
			try {
				writeStream.writeObject(new Message(Message.type_LOGIN, id));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Client ct = new Client();
		ct.bind("localhost", 3000);
		ct.start();
	}

}
