package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
        /*
        플로우? 흐름?? 에따라 조금씩 변경될듯.
        1. 하나의 아이디로 여러 채팅 방에 접속항때 Gui 아이디 입력받아 채팅방에서 쓰레드 생성..
        2. 새로운 채팅방에 접속항때마다 아이디를 입력받음. Gui 안에 loginUI 구성
        3. 뭐.. 확장가능하게.. 큰 패널안에 만들어서 처리해도 될듯... => 귀찮아....
         */

        // 오ㅒ!!! 안에서 호출하면안될까.. 고민.
        //Login loginUI = new Login(); // 로그인;
        Gui gui = new Gui();

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
			setLocation(200, 200);
			//setSize(getPreferredSize());
			setSize(280, 300);
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
        private JPanel mainPane;
		private JPanel chatPane;
		private JPanel buttonPane; 
		private JTextArea chatText;
		private JTextField chatLine;
		private JButton clearBtn;
		private JButton lockBtn; 
		private JButton exitBtn;
        private JTextField lockPasswordTextField;
        private JPanel loginJPanel;
        private JButton loginBtn;
        private JTextField loginTextField;

		private SendMessageHandler sendHandler;
		private ReceiveMessageHandler receiveHandler;

		private Thread receiveThread;
		private Thread sendThread;

        private String id;

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

        public void setId(String id){
            this.id = id;
            postLoginId(id);
        }

		public Gui() {
			this.sendHandler = new SendMessageHandler(writeStream, this);
			this.receiveHandler = new ReceiveMessageHandler(readStream, this);

			sendThread = new Thread(this.sendHandler);
			receiveThread = new Thread(this.receiveHandler);

            initChatRoom();
            startThread();
		}

        private void postLoginId(String id) {
            try {
                writeStream.writeObject(new Message(Message.type_LOGIN, id));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public JPanel loginUI() {
            JPanel loginJPanel = new JPanel();

            JLabel loginText = new JLabel();
            JPanel idPanel = new JPanel();
            loginTextField = new JTextField(12);
            loginText.setForeground(Color.RED);

            JLabel idLabel = new JLabel("ID : ");

            loginBtn = new JButton("LOGIN");
            loginBtn.addActionListener(this);

            idPanel.add(idLabel);
            idPanel.add(loginTextField);

            loginJPanel.add(idPanel);
            loginJPanel.add(loginBtn);
            loginJPanel.add(loginText);

            loginJPanel.setLayout(new FlowLayout());

            loginJPanel.setLocation(200, 200);
            loginJPanel.setSize(280, 100);
            //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginText.setText("ID를 입력하시오");

            //setVisible(true);

            return loginJPanel;
        }

		public void initChatRoom() {
			chatPane = new JPanel(new BorderLayout());
            loginJPanel = loginUI();
						
			/* chatPane */
			chatText = new JTextArea(10, 20);
			chatText.setLineWrap(true); // textbox 테두리
			chatText.setEditable(false); // textbox 수정여부
			chatText.setForeground(Color.blue); // 글씨색

			DefaultCaret caret = (DefaultCaret) chatText.getCaret();
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE); // 스크롤바가 항상  textarea 하단에 위치
						
			JScrollPane chatTextPane = new JScrollPane(chatText,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			chatLine = new JTextField();
			chatLine.setEnabled(true);
			chatLine.addKeyListener(this.sendHandler); // 키이벤트

			chatPane.add(chatLine, BorderLayout.SOUTH);
			chatPane.add(chatTextPane, BorderLayout.CENTER);
			chatPane.setPreferredSize(new Dimension(400, 400));

			/* lock password */
            lockPasswordTextField = new JTextField(20);
			
			/* buttonPane */
            buttonPane = new JPanel(new GridLayout(5, 0, 0, 50)); // 세로 5 x 가로 0 , 간격 50
			buttonPane.setPreferredSize(new Dimension(80, 400));

			clearBtn = new JButton("clear");
			lockBtn  = new JButton("lock");
			exitBtn  = new JButton("exit");

			clearBtn.addActionListener(this);		
			lockBtn.addActionListener(this);					
			exitBtn.addActionListener(this);

            buttonPane.add(lockPasswordTextField);
            lockPasswordTextField.setVisible(false);
			buttonPane.add(clearBtn);
			buttonPane.add(lockBtn);
			buttonPane.add(exitBtn);
			
			/* */

			/* mainPane */
			mainPane = new JPanel(new FlowLayout());
//			mainPane.add(chatPane);
//			mainPane.add(buttonPane);
            mainPane.add(loginJPanel);
			/* */
			
			mainFrame = new JFrame("YettieSoft ChatProgram");
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
			else if (e.getSource().equals(lockBtn)) {
				if( lockBtn.getText().equals("lock")) {
                    /*
                     사이즈부터 조정하자...... 그지같다..
                     우선.. visible로하면안되고.. remove로 처리해서 해야될듯.. 위치가 그지같이 뜸.....
                     swing버립시다.... 기능동작 커밋.
                    */
                    buttonPane.setPreferredSize(new Dimension(200, 400));
                    mainFrame.pack();

					chatPane.setVisible(false);
					clearBtn.setVisible(false);
					lockBtn.setText("unlock");
                    lockPasswordTextField.setVisible(true);
				}
				else {
                    /* lockPasswordTextField.getText() 뽐아낸 값으로 비밀번호 확인후 트루이면 락품 */
                    if(lockCheck(lockPasswordTextField.getText())){
                        // 사이즈 원복
                        buttonPane.setPreferredSize(new Dimension(80, 400));
                        mainFrame.pack();

                        lockPasswordTextField.setVisible(false);
                        chatPane.setVisible(true);
                        clearBtn.setVisible(true);
                        lockBtn.setText("lock");
                    }
                    else {
                        /* error 메세지... 어엄... */
                    }
				}
			}
			else if (e.getSource().equals(exitBtn)) {
				System.exit(0);
			}
            else if(e.getSource().equals(loginBtn)) {
                postLoginId(loginTextField.getText());
                mainPane.remove(loginJPanel);

                mainFrame.setPreferredSize(new Dimension(550, 480));
                mainFrame.pack();
                mainPane.add(chatPane);
			    mainPane.add(buttonPane);
            }
		}

        private Boolean lockCheck(String password) {
            // 무조건 성공!
            return true;
        }
	}

	public static void main(String[] args) {
		Client ct = new Client();
		//ct.setting("172.16.10.205", 3000);
		ct.setting("127.0.0.1", 3000);
		ct.start();
	}

}
