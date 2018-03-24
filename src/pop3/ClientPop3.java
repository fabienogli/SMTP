package pop3;

import codes.Pop3Codes;
import common.StreamHandling;
import common.Utilisateur;
import pop3.server.Commande;
import pop3.server.StateEnum;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientPop3 {

    //private Socket clientSocket;
    private int port;
    private InetAddress adresseIp;
    private Utilisateur utilisateur;

    private SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private SSLSocket clientSocket;

    private StateEnum stateEnum = null;
    private String timestamp;

    public ClientPop3(InetAddress adresseIp, int port) throws IOException {
        this.adresseIp = adresseIp;
        this.port = port;

    }

    public ClientPop3() throws IOException {
        this(InetAddress.getByName("localhost"), 2027);
    }

    public ClientPop3(Utilisateur utilisateur) throws IOException {
        this();
        this.utilisateur = utilisateur;
    }

    public ClientPop3(InetAddress adresseIp, Utilisateur utilisateur, int port) throws IOException {
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
        System.out.println("Démarrage client");
        this.clientSocket = (SSLSocket) sslsocketfactory.createSocket(this.getAdresseIp(), this.getPort());
        this.clientSocket.setEnabledCipherSuites(new String[]{"TLS_DH_anon_WITH_AES_128_CBC_SHA"});
        String reponseServer = read();
        String[] str = reponseServer.split("Ready ");
        if (str.length == 2) {
            timestamp = str[1];
            this.stateEnum = StateEnum.AUTHORIZATION;
        }
        return reponseServer;
    }

    public boolean authentification() {
        if (!this.stateEnum.equals(StateEnum.AUTHORIZATION)) {
            return false;
        }
        if (this.getUtilisateur() == null) {
            return false;
        }
        write(Pop3Codes.USER.toString() + this.getUtilisateur().getNom());
        String reponseServer = read();
        if (reponseServer.contains("-ERR")) {
            return false;
        }
        write(Pop3Codes.PASS.toString() + this.getUtilisateur().getMdp());
        reponseServer = read();
        if (reponseServer.contains("+OK")) {
            this.stateEnum = StateEnum.TRANSACTION;
        }
        return true;
    }

    public boolean authentificationApop() {
        if (!this.stateEnum.equals(StateEnum.AUTHORIZATION)) {
            return false;
        }
        if (this.getUtilisateur() == null) {
            return false;
        }
        write(Pop3Codes.APOP.toString() + this.getUtilisateur().getNom() + " " + Commande.encryptApop(timestamp + this.getUtilisateur().getMdp()));
        String reponseServer = read();
        if (reponseServer.contains("+OK")) {
            this.stateEnum = StateEnum.TRANSACTION;
            return true;
        }
        return false;
    }

    public void createMailFile() {
        Path file = Paths.get("src/ClientPop3/Mails/" + this.utilisateur.getNom());
        try {
            // Create the empty file with default permissions, etc.
            Files.createFile(file);
        } catch (FileAlreadyExistsException x) {
            System.err.format("file named %s" +
                    " already exists%n", file);
        } catch (IOException x) {
            // Some other sort of failure, such as permissions.
            System.err.format("createFile error: %s%n", x);
        }
    }


    public void write(String data) {
        data += "\r\n";
        try {
            StreamHandling.write(data, this.clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        String response = "";
        try {
            response = StreamHandling.read(this.clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String readMultipleLines() {
        String response = "";
        try {
            response = StreamHandling.readMultipleLines(this.clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public String retr(int numMessage) {
        String reponseServer = "";
        write(Pop3Codes.RETR.toString() + numMessage);
        reponseServer = readMultipleLines();
        String[] arrayResponse = reponseServer.split("\n");
        if (! arrayResponse[0].contains(Pop3Codes.SUCCESS.toString())) {
            //@TODO controle de l'erreur
            System.out.println(arrayResponse[0]);
            return "Erreur";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < arrayResponse.length; i++) {
            stringBuilder.append(arrayResponse[i]);
            if (i != arrayResponse.length - 1) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public String list() {
        String reponseServer = "";
        write(Pop3Codes.LIST.toString());
        reponseServer = readMultipleLines();
        return reponseServer;
    }

    public String stat() {
        String reponseServer = "";
        write(Pop3Codes.STAT.toString());
        reponseServer = read();
        return reponseServer;
    }

    public StateEnum getStatus() {
        return this.stateEnum;
    }

    public void logout() {
        this.stateEnum = StateEnum.AUTHORIZATION;
        write(Pop3Codes.LOGOUT.toString());

    }
}