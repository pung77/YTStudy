package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gui {
	private static JFrame mainFrame;
	private static JPanel chatPane;
	private static JTextArea chatText;
	private static JTextField chatLine;
	
	public static String getChatText() {
		return chatText.getText();
	}

	public static String getChatLine() {
		return chatLine.getText();
	}
	
	public static void setChatText(String text) {
		Gui.chatText.setText(text);
	}

	public static void setChatLine(String text) {
		Gui.chatLine.setText(text);
	}
	
	public Gui() {		
		chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true); // textbox 테두리
		chatText.setEditable(false); // textbox 수정여부
		chatText.setForeground(Color.blue); // 글씨색
		
		JScrollPane chatTextPane = new JScrollPane(chatText, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		chatLine = new JTextField();
		chatLine.setEnabled(true);
		chatLine.addKeyListener(new KeyEventListener());

		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(400, 400));

		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(chatPane, BorderLayout.CENTER);

		mainFrame = new JFrame("Multi Chatting");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
	/*interface KeyEventListener {
		public void keyPressed(KeyEvent e);
		public void keyTyped(KeyEvent e);
		public void keyReleased(KeyEvent e);
	}*/

/*	static class KeyEventListener implements KeyListener {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
//				String msg = chatLine.getText();
//				writeStream.writeObject(new Message(Message.type_MESSAGE, msg));
//				writeStream.flush();
				chatLine.setText(chatLine.getText());
				chatLine.setText("");
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
	}*/
}
