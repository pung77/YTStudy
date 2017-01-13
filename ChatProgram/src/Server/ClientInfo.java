package Server;

import java.net.Socket;

public class ClientInfo {
	private String strClientID;
	private Socket socket;
	
	public void SetClientID(String strID) {
		strClientID = strID;
	}
	
	public String GetClientID() {
		return strClientID;
	}
	
	public void SetSocket(Socket socket){
		this.socket = socket;
	}
	
	public Socket GetSocket() {
		return this.socket;
	}
	
	public String GetIP() {
		return socket.getInetAddress().getHostAddress();
	}
}
