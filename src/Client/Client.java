package Client;

import Server.Commande;
import Server.StateEnum;
import Server.StreamHandling;
import Server.Utilisateur;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class Client {

    //private Socket clientSocket;
    private int port;
    private InetAddress adresseIp;
    private Utilisateur utilisateur;
    private SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private SSLSocket clientSocket ;

    private StateEnum stateEnum= null;
    private String timestamp;

    public Client(InetAddress adresseIp, int port) throws IOException {
        this.adresseIp = adresseIp;
        this.port = port;

    }

    public Client() throws IOException {
        this(java.net.InetAddress.getByName("localhost"), 2026);
    }

    public Client(Utilisateur utilisateur) throws IOException {
        this();
        this.utilisateur = utilisateur;
    }

    public Client(InetAddress adresseIp, Utilisateur utilisateur, int port) throws IOException {
        this(adresseIp, port);
        this.utilisateur = utilisateur;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public InetAddress getAdresseIp() {
        return adresseIp;
    }

    public void setAdresseIp(InetAddress adresseIp) {
        this.adresseIp = adresseIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String start() throws IOException {
        this.clientSocket = (SSLSocket)sslsocketfactory.createSocket(this.getAdresseIp(), this.getPort());
        this.clientSocket.setEnabledCipherSuites(new String[] { "TLS_DH_anon_WITH_AES_128_CBC_SHA" });
        return read();
    }

    public boolean authentification() {

        return true;
    }

    public void write(String message) {
        try {
            StreamHandling.write(message, this.clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        String data = "";
        try {
            data = StreamHandling.read(this.clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public String readMultipleLines() {
        String data="";
        try {
            data = StreamHandling.readMultipleLines(this.clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}