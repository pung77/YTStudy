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

            // 筌롫뗄苑�筌욑옙 占쎈땾占쎈뻿
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

                // client占쎈퓠 筌롫뗄�뻻筌욑옙�몴占� 癰귣�沅�占쎈뼄
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

        public Boolean ProcessingByMessageType(Message msg) throws IOException {
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

                // todo : 占쎈퉸占쎈뼣 占쎈툡占쎌뵠占쎈탵揶쏉옙 占쎈굶占쎈선占쎌넅占쎈뼄�⑨옙 sendToAll
            } else if (nMessageType == Message.type_MESSAGE) {
                ClientInfo clInfo = GetClientInfoByIP(strIpAddr);
                System.out.println("msg type: " + msg.getMessageType() + ", msg: " + msg.getMessage() + ", id정보: " + clInfo.GetClientID());
                m_oOut.writeObject(new Message(msg.getMessageType(), msg.getMessage(), clInfo.GetClientID()));
//                if (clInfo == null)
//                    return false;
//
//                // 筌뤴뫀諭� 占쎄깻占쎌뵬占쎌뵠占쎈섧占쎈뱜占쎈퓠野껓옙 占쎌읈占쎈꽊
//                SendToAll(new Message(msg.getMessageType(), msg.getMessage(), clInfo.GetClientID()));
            } else {
                // mapClient占쎈퓠 占쎈퉸占쎈뼣 ip揶쏉옙 占쎌뿳占쎈뼄筌롳옙 占쎌젫椰꾬옙
                if (mapClient.containsKey(strIpAddr)) {
                    mapClient.remove(strIpAddr);
                    System.out.println("Removeing id is successful. ip : " + strIpAddr + ", id : " + strMessge);

                    // todo : 占쎈퉸占쎈뼣 占쎈툡占쎌뵠占쎈탵揶쏉옙 占쎄돌揶쏅뗀�뼄�⑨옙 sendToAll
                } else
                    System.out.println("Removing id is failed. This ip is not registered. ip : " + strIpAddr);
            }
            return true;
        }

        public ServerReceiverThread(Socket socket) {
            this.m_socket = socket;
        }
    }

    // 占쎄퐣甕곤옙 占쎈뻻占쎌삂 占쎈맙占쎈땾
    public void ServerStart(Boolean bStart) {
        Socket socket = null;
        if (bStart) {
            try {
                serverSocket = new ServerSocket(nServerPort);
                System.out.println("占쎄퐣甕곌쑨占� 占쎈뻻占쎌삂占쎈┷占쎈�占쎈뮸占쎈빍占쎈뼄.");
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

    // 占쎈꺖筌롫챷�쁽揶쏆늿占� 揶쏆뮆�쀯옙�뵠占쎌뵬 占쎈립占쎈뼄.
    public void finalize() {
        System.out.println("揶쏆빘猿쒙옙�벥 筌띾뜆占쏙쭕占� 占쎌�占쎈섧... Bye Server.");
    }

    public static void main(String[] args) {
        // 占쎄퐣甕곤옙 占쎈뻻占쎌삂 占쎈맙占쎈땾
        Server myServer = new Server();
        myServer.ServerStart(true);
    }
}

