/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.*;

public class Server {
    final static int BUF_SIZE = 1024;
    public  int portEcoute;
    //private ServerSocket serverSocket;
    // Initialize the Server Socket
    private SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
    private SSLServerSocket sslServerSocket ;


    public Server(){
        portEcoute = 2026;
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
               // Socket clientSocket = serverSocket.accept();

                SSLSocket clientSocket = (SSLSocket)sslServerSocket.accept();
                clientSocket.setUseClientMode(false);
                clientSocket.setEnabledProtocols(clientSocket.getSupportedProtocols());
                clientSocket.setEnabledCipherSuites(clientSocket.getSupportedCipherSuites());
                ///
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                int portClient = receivePacket.getPort();
                InetAddress address = receivePacket.getAddress();
                Connexion serveurPOP3 = new Connexion(address,clientSocket, portClient);
                Thread thread = new Thread(serveurPOP3);
                System.out.println("Lancement du serveur");
                thread.start();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Server server = new Server();
        server.lancer();
    }
}
