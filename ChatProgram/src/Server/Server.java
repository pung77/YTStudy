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

            // 嶺뚮∥�뾼�땻占썹춯�쉻�삕 �뜝�럥�빢�뜝�럥六�
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

                // client�뜝�럥�뱺 嶺뚮∥�뾼占쎈뻣嶺뚯쉻�삕占쎈ご�뜝占� �솻洹ｏ옙亦낉옙�뜝�럥堉�
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

                // todo : �뜝�럥�돵�뜝�럥堉� �뜝�럥�닡�뜝�럩逾졾뜝�럥�꺏�뤆�룊�삕 �뜝�럥援뜹뜝�럥�꽑�뜝�럩�꼨�뜝�럥堉꾬옙�뫅�삕 sendToAll
            } else if (nMessageType == Message.type_MESSAGE) {
                ClientInfo clInfo = GetClientInfoByIP(strIpAddr);
                System.out.println("msg type: " + msg.getMessageType() + ", msg: " + msg.getMessage() + ", id�젙蹂�: " + clInfo.GetClientID());
                m_oOut.writeObject(new Message(msg.getMessageType(), msg.getMessage(), clInfo.GetClientID()));
//                if (clInfo == null)
//                    return false;
//
//                // 嶺뚮ㅄ維�獄�占� �뜝�럡源삣뜝�럩逾у뜝�럩逾졾뜝�럥�꽘�뜝�럥諭쒎뜝�럥�뱺�뇦猿볦삕 �뜝�럩�쓧�뜝�럥苑�
//                SendToAll(new Message(msg.getMessageType(), msg.getMessage(), clInfo.GetClientID()));
            } else {
                // mapClient�뜝�럥�뱺 �뜝�럥�돵�뜝�럥堉� ip�뤆�룊�삕 �뜝�럩肉녑뜝�럥堉꾤춯濡녹삕 �뜝�럩�젷濾곌쒀�삕
                if (mapClient.containsKey(strIpAddr)) {
                    mapClient.remove(strIpAddr);
                    System.out.println("Removeing id is successful. ip : " + strIpAddr + ", id : " + strMessge);

                    // todo : �뜝�럥�돵�뜝�럥堉� �뜝�럥�닡�뜝�럩逾졾뜝�럥�꺏�뤆�룊�삕 �뜝�럡�룎�뤆�룆��占쎈펲占썩뫅�삕 sendToAll
                } else
                    System.out.println("Removing id is failed. This ip is not registered. ip : " + strIpAddr);
            }
            return true;
        }

        public ServerReceiverThread(Socket socket) {
            this.m_socket = socket;
        }
    }

    // �뜝�럡�맋�뵓怨ㅼ삕 �뜝�럥六삣뜝�럩�굚 �뜝�럥留쇿뜝�럥�빢
    public void ServerStart(Boolean bStart) {
        Socket socket = null;
        if (bStart) {
            try {
                serverSocket = new ServerSocket(nServerPort);
                System.out.println("�뜝�럡�맋�뵓怨뚯뫅�뜝占� �뜝�럥六삣뜝�럩�굚�뜝�럥�뵹�뜝�럥占썲뜝�럥裕멨뜝�럥鍮띶뜝�럥堉�.");
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

    // �뜝�럥爰뽫춯濡レ굣占쎌겱�뤆�룇�듌�뜝占� �뤆�룇裕놅옙���삕占쎈턄�뜝�럩逾� �뜝�럥由썲뜝�럥堉�.
    public void finalize() {
        System.out.println("�뤆�룇鍮섊뙼�뮋�삕占쎈꺄 嶺뚮씭�쐠�뜝�룞彛뺝뜝占� �뜝�럩占썲뜝�럥�꽘... Bye Server.");
    }

    public static void main(String[] args) {
        // �뜝�럡�맋�뵓怨ㅼ삕 �뜝�럥六삣뜝�럩�굚 �뜝�럥留쇿뜝�럥�빢
        Server myServer = new Server();
        myServer.ServerStart(true);
    }
}

