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

            // 硫붿꽭吏� �닔�떊
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

                // client�뿉 硫붿떆吏�瑜� 蹂대궦�떎
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

                // todo : �빐�떦 �븘�씠�뵒媛� �뱾�뼱�솕�떎怨� sendToAll
            } else if (nMessageType == Message.type_MESSAGE) {
                ClientInfo clInfo = GetClientInfoByIP(strIpAddr);
                if (clInfo == null)
                    return false;

                // 紐⑤뱺 �겢�씪�씠�뼵�듃�뿉寃� �쟾�넚
                SendToAll(new Message(msg.getMessageType(), msg.getMessage(), clInfo.GetClientID()));
            } else {
                // mapClient�뿉 �빐�떦 ip媛� �엳�떎硫� �젣嫄�
                if (mapClient.containsKey(strIpAddr)) {
                    mapClient.remove(strIpAddr);
                    System.out.println("Removeing id is successful. ip : " + strIpAddr + ", id : " + strMessge);

                    // todo : �빐�떦 �븘�씠�뵒媛� �굹媛붾떎怨� sendToAll
                } else
                    System.out.println("Removing id is failed. This ip is not registered. ip : " + strIpAddr);
            }
            return true;
        }

        public ServerReceiverThread(Socket socket) {
            this.m_socket = socket;
        }
    }

    // �꽌踰� �떆�옉 �븿�닔
    public void ServerStart(Boolean bStart) {
        Socket socket = null;
        if (bStart) {
            try {
                serverSocket = new ServerSocket(nServerPort);
                System.out.println("�꽌踰꾧� �떆�옉�릺�뿀�뒿�땲�떎.");
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

    // �냼硫몄옄媛숈� 媛쒕뀗�씠�씪 �븳�떎.
    public void finalize() {
        System.out.println("媛앹껜�쓽 留덉�留� �쑀�뼵... Bye Server.");
    }

    public static void main(String[] args) {
        // �꽌踰� �떆�옉 �븿�닔
        Server myServer = new Server();
        myServer.ServerStart(true);
    }
}

