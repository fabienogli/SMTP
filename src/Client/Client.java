package Client;

import Server.Commande;
import Server.StateEnum;
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
        this(java.net.InetAddress.getByName("192.168.43.210"), 2026);
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
        System.out.println("Démarrage client");
        //this.clientSocket = new Socket(this.getAdresseIp(), this.getPort());
        this.clientSocket = (SSLSocket)sslsocketfactory.createSocket(this.getAdresseIp(), this.getPort());
        this.clientSocket.setEnabledCipherSuites(new String[] { "TLS_DH_anon_WITH_AES_128_CBC_SHA" });
        String reponseServer = read();
        String[] str = reponseServer.split("Ready ");
        if (str.length == 2) {
            timestamp = str[1];
//            this.stateEnum = StateEnum.AUTHORIZATION;
        }
        return reponseServer;
    }

    public boolean authentification() {
//        if (!this.stateEnum.equals(StateEnum.AUTHORIZATION)) {
//            return false;
//        }
//        if (this.getUtilisateur() == null) {
//            return false;
//        }
//        write("USER " + this.getUtilisateur().getNom());
//        String reponseServer = read();
//        if (reponseServer.contains("-ERR")) {
//            return false;
//        }
//        write("PASS " + this.getUtilisateur().getMdp());
//        reponseServer = read();
//        if (reponseServer.contains("+OK")) {
//            this.stateEnum = StateEnum.TRANSACTION;
//        }
        return true;
    }


    public void createMailFile(){
        Path file = Paths.get("src/Client/Mails/"+this.utilisateur.getNom()) ;
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
            OutputStream outputStream = this.clientSocket.getOutputStream();
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Le client envoie " + data);
    }

    public String read() {
        StringBuilder data = new StringBuilder();
        try {
            BufferedReader fromServer  = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            data.append(fromServer.readLine());
            //while (fromServer.ready() || (stopOnlyWhenDot && data.indexOf(".") == -1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Le client recoit " + data.toString());
        return data.toString();
    }

    public String readMultipleLines() {
        StringBuilder data = new StringBuilder();
        try {
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            String tmp = fromServer.readLine();
            do {
                data.append(tmp).append("\n");
                tmp = fromServer.readLine();
            } while (tmp.length() > 0 && tmp.charAt(0) != '.');
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Le client recoit " + data.toString());
        return data.toString();
    }
}