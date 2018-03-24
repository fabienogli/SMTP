/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package smtp.server;

import database.Dns;
import database.NoNameDnsException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Year;
import java.util.Scanner;

public class Server {
    private SSLServerSocket sslServerSocket ;
    private Dns dns;

    private Server(){
        int portEcoute = 2026;
        try {
            SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(portEcoute);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dns = new Dns();
        dns.getDnsFromDb();
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
        registerDns();
        server.lancer();
    }

    private static void registerDns() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Voulez vous ajouter des noms de domaines ? (Y/N)");
        if (sc.next().equals("Y")) {

        }
    }

    public void addDomain(String name, String ip) {
        try {
            dns.associateNameIp(name, ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (NoNameDnsException e) {
            e.printStackTrace();
        }
    }

    public Dns getDns() {
        return dns;
    }

    public void setDns(Dns dns) {
        this.dns = dns;
    }
}
