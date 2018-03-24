/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package smtp.server;

import database.Dns;
import database.NoNameDnsException;
import pop3.server.ServerPop3;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Year;
import java.util.Scanner;

public class ServerSMTP implements Runnable{
    private SSLServerSocket sslServerSocket ;
    private Dns dns;

    public ServerSMTP(){
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

    public void lancer(){
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
    /*
    public static void main(String[] args) {
        ServerSMTP serverSMTP = new ServerSMTP();
        //registerDns(serverSMTP.getDns());
        serverSMTP.lancer();
    }*/



    public static void registerDns(Dns dns) {
        Scanner sc = new Scanner(System.in);
        String again = "Voulez vous ajouter des ip à un nom de domaine ? (Y/N)";
        System.out.println(again);
        boolean dansLeScript = sc.next().equals("Y");
        while (dansLeScript) {
            System.out.println(dns);
            System.out.println("Entrez le numéro de channel à affecter");
            int i_name = Integer.parseInt(sc.next());
            System.out.println("Entrez l'ip que vous voulez associé");
            String ip = sc.nextLine();
            try {
                dns.associateNameIp(dns.getDomain().get(i_name), ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (NoNameDnsException e) {
                e.printStackTrace();
            }
            System.out.println(again);
            dansLeScript = sc.next().equals("Y");
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

    @Override
    public void run() {
        this.lancer();
    }
}
