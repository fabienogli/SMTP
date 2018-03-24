package pop3.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.*;

public class ServerPop3 {
    final static int BUF_SIZE = 1024;
    public  int portEcoute;
    //private ServerSocket serverSocket;
    // Initialize the ServerPop3 Socket
    private SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
    private SSLServerSocket sslServerSocket ;


    public ServerPop3(){
        portEcoute = 2027;
        try {
            //serverSocket = new ServerSocket(portEcoute);
            sslServerSocket = (SSLServerSocket)sslServerSocketfactory.createServerSocket(portEcoute);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lancer(){

        byte [] buffer = new byte[BUF_SIZE];
        try {
            while (true)
            {
                SSLSocket clientSocket = (SSLSocket)sslServerSocket.accept();
                clientSocket.setUseClientMode(false);
                clientSocket.setEnabledProtocols(clientSocket.getSupportedProtocols());
                clientSocket.setEnabledCipherSuites(clientSocket.getSupportedCipherSuites());

                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                int portClient = receivePacket.getPort();
                InetAddress address = receivePacket.getAddress();
                Connexion serveurPOP3 = new Connexion(address,clientSocket, portClient);
                Thread thread = new Thread(serveurPOP3);
                thread.start();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
/*
    public static void main(String[] args) {
        ServerPop3 serverPop3 = new ServerPop3();
        serverPop3.lancer();
    }
    */
}
