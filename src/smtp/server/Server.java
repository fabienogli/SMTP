/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package smtp.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class Server {
    private SSLServerSocket sslServerSocket ;

    private Server(){
        int portEcoute = 2026;
        try {
            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(portEcoute);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lancer(){
        try {
            while (true)
            {
                SSLSocket clientSocket = (SSLSocket)sslServerSocket.accept();
                clientSocket.setUseClientMode(false);
                clientSocket.setEnabledProtocols(clientSocket.getSupportedProtocols());
                clientSocket.setEnabledCipherSuites(clientSocket.getSupportedCipherSuites());
                Connexion serveurSTMP = new Connexion(clientSocket);
                Thread thread = new Thread(serveurSTMP);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.lancer();
    }
}
