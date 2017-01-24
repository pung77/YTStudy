package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientInfo {
	private String strClientID;
	private Socket socket;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
	
	public void SetClientID(String strID) {
		strClientID = strID;
	}
	
	public String GetClientID() {
		return strClientID;
	}
	
	public void SetSocket(Socket socket) throws Exception{
		this.socket = socket;

        this.in = new ObjectInputStream(this.socket.getInputStream());
        this.out = new ObjectOutputStream(this.socket.getOutputStream());
	}

    public ObjectInputStream getObjectInputStream() {
        return this.in;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return this.out;
    }

	public Socket GetSocket() {
		return this.socket;
	}
	
	public String GetIP() {
		return socket.getInetAddress().getHostAddress();
	}
}
