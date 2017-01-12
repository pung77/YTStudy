package firstServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;

import Model.Message;

public class Server {

    private HashMap<String, ClientInfo> mapClient = new HashMap<String, ClientInfo>();  //first : ip, second : clientInfo
    private final int nServerPort = 3000;
    private ServerSocket serverSocket = null;

    public class ServerReceiverThread implements Runnable {
        private Socket m_socket;


        private ObjectInputStream m_oIn;
        private ObjectOutputStream m_oOut;
        private Object m_object;

        @Override
        public void run() {
            System.out.println("thread run ( IP: " + m_socket.getInetAddress() + ", Port: " + m_socket.getPort() + ")");
            try {
                m_oOut = new ObjectOutputStream(m_socket.getOutputStream());
                m_oIn = new ObjectInputStream(m_socket.getInputStream());
            } catch (IOException e) {
                // error,
                System.out.println("error: " + e);
                e.printStackTrace();
            }

            // 메세지 수신
            try {
                while (true) {
                    m_object = m_oIn.readObject();
                    Message msg = (Message) m_object;
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

                // client에 메시지를 보낸다
                socket = clInfo.GetSocket();
                if (socket == null)
                    continue;

                try {
                    m_oOut.writeObject(msg);
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

            String strIpAddr = m_socket.getInetAddress().toString();
            String strMessge = msg.getMessage();

            if (nMessageType == Message.type_LOGIN) {
                ClientInfo clInfo = GetClientInfoByIP(strIpAddr);
                if (clInfo == null) {
                    System.out.println("Registration is failed. This ip is not registered. ip : " + strIpAddr);
                    return false;
                }

                clInfo.SetClientID(strMessge);

                System.out.println("Registration of id successful. ip : " + strIpAddr + ", id : " + strMessge);

                // todo : 해당 아이디가 들어왔다고 sendToAll
            } else if (nMessageType == Message.type_MESSAGE) {
                ClientInfo clInfo = GetClientInfoByIP(strIpAddr);
                if (clInfo == null)
                    return false;

                // 모든 클라이언트에게 전송
                SendToAll(new Message(msg.getMessageType(), msg.getMessage(), clInfo.GetClientID()));
            } else {
                // mapClient에 해당 ip가 있다면 제거
                if (mapClient.containsKey(strIpAddr)) {
                    mapClient.remove(strIpAddr);
                    System.out.println("Removeing id is successful. ip : " + strIpAddr + ", id : " + strMessge);

                    // todo : 해당 아이디가 나갔다고 sendToAll
                } else
                    System.out.println("Removing id is failed. This ip is not registered. ip : " + strIpAddr);
            }
            return true;
        }

        public ServerReceiverThread(Socket socket) {
            this.m_socket = socket;
        }
    }

    // 서버 시작 함수
    public void ServerStart(Boolean bStart) {
        Socket socket = null;
        if (bStart) {
            try {
                serverSocket = new ServerSocket(nServerPort);
                System.out.println("서버가 시작되었습니다.");
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }

            try {
                while (true) {
                    socket = serverSocket.accept();
                    System.out.println("IP : " + socket.getInetAddress() + ", Port : " + socket.getPort());

                    ClientInfo clInfo = new ClientInfo();
                    clInfo.SetSocket(socket);
                    mapClient.put(socket.getInetAddress().toString(), clInfo);

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

    // 소멸자같은 개념이라 한다.
    public void finalize() {
        System.out.println("객체의 마지막 유언... Bye Server.");
    }

    public static void main(String[] args) {
        // 서버 시작 함수
        Server myServer = new Server();
        myServer.ServerStart(true);
    }
}

