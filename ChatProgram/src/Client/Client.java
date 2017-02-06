package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

import common.Message;

public class Client {
	// 귓속말, 잠금, 채팅창 클리어 기능 추가
	private Socket clientSocket;
	private ObjectInputStream readStream;
	private ObjectOutputStream writeStream;

	// 소켓 생성 및 스레드 생성
	public void setting(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			writeStream = new ObjectOutputStream(clientSocket.getOutputStream());
			readStream = new ObjectInputStream(clientSocket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Login loginUI = new Login(); // 로그인
		Gui gui = new Gui();
		gui.startThread();

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

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String id = tf.getText();
			try {
				writeStream.writeObject(new Message(Message.type_LOGIN, id));
				dispose();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public class Gui implements ActionListener {
		private JFrame mainFrame;
		private JPanel chatPane;
		private JTextArea chatText;
		private JTextField chatLine;
		private JButton clearBtn;

		private SendMessageHandler sendHandler;
		private ReceiveMessageHandler receiveHandler;

		private Thread receiveThread;
		private Thread sendThread;

		public JTextArea getChatText() {
			return chatText;
		}

		public JTextField getChatLine() {
			return chatLine;
		}

		public void setChatText(String text) {
			chatText.setText(text);
		}

		public void setChatLine(String text) {
			chatLine.setText(text);
		}
		
		public JButton getClearBtn() {
			return clearBtn;
		}

		public Gui() {
			this.sendHandler = new SendMessageHandler(writeStream, this);
			this.receiveHandler = new ReceiveMessageHandler(readStream, this);

			sendThread = new Thread(this.sendHandler);
			receiveThread = new Thread(this.receiveHandler);

			initChatRoom();
		}

		public void initChatRoom() {
			chatPane = new JPanel(new BorderLayout());
			chatText = new JTextArea(10, 20);
			chatText.setLineWrap(true); // textbox 테두리
			chatText.setEditable(false); // textbox 수정여부
			chatText.setForeground(Color.blue); // 글씨색

			DefaultCaret caret = (DefaultCaret) chatText.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // 스크롤바가 항상
																// textarea 하단에
																// 위치

			JScrollPane chatTextPane = new JScrollPane(chatText,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			chatLine = new JTextField();
			chatLine.setEnabled(true);
			chatLine.addKeyListener(this.sendHandler); // 키이벤트

			clearBtn = new JButton("clear");
			clearBtn.setSize(70, 25);
			// clearBtn.setLocation(325, 375);
			clearBtn.addActionListener(this);

			chatPane.add(chatLine, BorderLayout.SOUTH);
			chatPane.add(clearBtn);
			chatPane.add(chatTextPane, BorderLayout.CENTER);
			chatPane.setPreferredSize(new Dimension(400, 400));

			JPanel mainPane = new JPanel(new BorderLayout());
			mainPane.add(chatPane, BorderLayout.CENTER);

			mainFrame = new JFrame("채팅 프로그램");
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setContentPane(mainPane);
			mainFrame.setSize(mainFrame.getPreferredSize());
			mainFrame.setLocation(200, 200);
			mainFrame.pack();
			mainFrame.setVisible(true);
		}

		public void startThread() {
			receiveThread.start();
			sendThread.start();

			try {
				receiveThread.join();
				sendThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		
		public void actionPerformed(ActionEvent e) {
			// 액션 리스너 재정의
			if (e.getSource().equals(clearBtn)) {
				chatText.setText("");
				receiveHandler.setContents("");
			}
		}
	}

	public static void main(String[] args) {
		Client ct = new Client();
		ct.setting("localhost", 3000);
		ct.start();
	}

}
