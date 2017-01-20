package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import common.Message;

public class Server {

    private static HashMap<String, ClientInfo> mapClient = new HashMap<String, ClientInfo>();  //first : ip, second : clientInfo
    private final int port = 3000;
    private ServerSocket serverSocket = null;

    public class ServerReceiverThread implements Runnable {
        private Socket socket;


        private ObjectInputStream oIn;
        private ObjectOutputStream oOut;
        private Object messageObject;

        @Override
        public void run() {
            System.out.println("thread run ( IP: " + socket.getInetAddress() + ", Port: " + socket.getPort() + ")");
            try {
                oOut = new ObjectOutputStream(socket.getOutputStream());
                oIn = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                // error,
                System.out.println("error: " + e);
                e.printStackTrace();
            }

            try {
                while (true) {
                    messageObject = oIn.readObject();
                    Message msg = (Message) messageObject;
                    if (!ProcessingByMessageType(msg)) {
                        System.out.println("The ProcessingByMessageType func is failed.");
                    }
                }
            } catch (Exception e) {
                System.out.println("readObject error: " + e);
                e.printStackTrace();
            }
        }

        public void SendToAll(Message msg) {
            String strKey;
            ClientInfo clInfo = null;
            Socket socket = null;

            Iterator<String> ite = mapClient.keySet().iterator();
            while (ite.hasNext()) {
                strKey = ite.next();
                clInfo = mapClient.get(strKey);
                if (clInfo == null)
                    continue;

                socket = clInfo.GetSocket();
                if (socket == null)
                    continue;

                try {
                    oOut.writeObject(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public Boolean ProcessingByMessageType(Message msg) {
            if (msg == null)
                return false;

            int nMessageType = msg.getMessageType();
            if (nMessageType < 1 || nMessageType > 3)
                return false;

            String strIpAddr = socket.getInetAddress().toString();
            String strMessge = msg.getMessage();

            if (nMessageType == Message.type_LOGIN) {
                ClientInfo clInfo = GetClientInfoByIP(strIpAddr);
                if (clInfo == null) {
                    System.out.println("Registration is failed. This ip is not registered. ip : " + strIpAddr);
                    return false;
                }

                clInfo.SetClientID(strMessge);

                System.out.println("Registration of id successful. ip : " + strIpAddr + ", id : " + strMessge);

            } else if (nMessageType == Message.type_MESSAGE) {
                ClientInfo clInfo = GetClientInfoByIP(strIpAddr);
                if (clInfo == null)
                    return false;

                SendToAll(new Message(msg.getMessageType(), msg.getMessage(), clInfo.GetClientID()));
            } else {

                if (mapClient.containsKey(strIpAddr)) {
                    mapClient.remove(strIpAddr);
                    System.out.println("Removeing id is successful. ip : " + strIpAddr + ", id : " + strMessge);

                } else
                    System.out.println("Removing id is failed. This ip is not registered. ip : " + strIpAddr);
            }
            return true;
        }

        public ServerReceiverThread(Socket socket) {
            this.socket = socket;
        }
    }

    public void start(Boolean bStart) {
        // false 경우가 있을까??
        Socket socket = null;
        if (bStart) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("start server");
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }

            try {
                while (true) {
                    socket = serverSocket.accept();
                    System.out.println("IP : " + socket.getInetAddress() + ", Port : " + socket.getPort());

                    ClientInfo client = new ClientInfo();
                    client.SetSocket(socket);
                    mapClient.put(socket.getInetAddress().toString(), client);

                    Thread st = new Thread(new ServerReceiverThread(socket));
                    st.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("Server is already shut down.");
        }
    }

    private ClientInfo GetClientInfoByIP(String strIp) {
        if (mapClient.containsKey(strIp)) {
            ClientInfo clInfo = mapClient.get(strIp);
            if (clInfo == null)
                return null;

            return clInfo;
        }

        return null;
    }

    public void finalize() {
        System.out.println("Bye Server.");
    }

    public static void main(String[] args) {
        Server myServer = new Server();
        myServer.start(true);
    }
}

