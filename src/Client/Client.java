package Client;

import ServerSmtp.HeadersEnum;
import ServerSmtp.Message;
import ServerSmtp.StreamHandling;
import ServerSmtp.Utilisateur;
import codes.SmtpCodes;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;

public class Client {

    //private Socket clientSocket;
    private int port;
    private InetAddress adresseIp;
    private Utilisateur utilisateur;
    private SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private SSLSocket clientSocket ;
    private boolean isAuthentified;

    public Client(InetAddress adresseIp, int port) throws IOException {
        this.adresseIp = adresseIp;
        this.port = port;
        this.isAuthentified = false;

    }

    public Client() throws IOException {
        this(java.net.InetAddress.getByName("localhost"), 2026);
        this.isAuthentified = false;
    }

    public Client(Utilisateur utilisateur) throws IOException {
        this();
        this.utilisateur = utilisateur;
    }

    public Client(InetAddress adresseIp, int port, Utilisateur utilisateur) throws IOException {
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

    public void authentification() {
        write(this.getUtilisateur().getEmail());
        write(this.getUtilisateur().getMdp());
        String response = read();
        if (response.equals(SmtpCodes.AUTHENTIFICATED.toString())) {
            this.isAuthentified = true;
            write(SmtpCodes.EHLO.toString());
            String ok =  read();
            if (!ok.equals(SmtpCodes.OK.toString())) {
                System.out.println(ok);
                this.isAuthentified = false;
            }
        }
    }

    private void write(String message) {
        try {
            StreamHandling.write(message, this.clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read() {
        String data = "";
        try {
            data = StreamHandling.read(this.clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String readMultipleLines() {
        String data="";
        try {
            data = StreamHandling.readMultipleLines(this.clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void quit() {
        write(SmtpCodes.QUIT.toString());
        read();
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMail(Message message) {
        write(SmtpCodes.MAIL_FROM.toString() + "<" + message.getAuteur().getEmail() + ">");
        String response = read();
        if (!response.equals(SmtpCodes.OK.toString())) {
            System.out.println(response);
        }
        for (Utilisateur destinataire: message.getDestinataires()) {
            write(SmtpCodes.RCPT_TO.toString() + "<" + destinataire.getEmail() + ">");
            response = read();
            if (!response.equals(SmtpCodes.OK.toString())) {
                System.out.println(response);
            }
        }
        write(SmtpCodes.DATA.toString());
        response = read();
        if (!response.equals(SmtpCodes.MESSAGE.toString())) {
            System.out.println(response);
        }
        write(parseMailForSmtp(message));
        response = read();
        if (!response.equals(SmtpCodes.OK.toString())) {
            System.out.println(response);
        }
        System.out.println("Message écrit avec succes");
    }

    private String parseMailForSmtp(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(HeadersEnum.DATE.toString())
                .append(message.getDate())
                .append("\n")
                .append(HeadersEnum.FROM.toString())
                .append(getUtilisateur().getNom())
                .append(" <")
                .append(getUtilisateur().getEmail())
                .append(">\n")
                .append(HeadersEnum.SUJET.toString())
                .append(message.getSujet())
                .append("\n")
                .append(HeadersEnum.TO.toString());
        for (int i = 0; i < message.getDestinataires().size(); i++) {
            stringBuilder
                    .append("<")
                    .append(message.getDestinataires().get(i).getEmail())
                    .append(">");
            if (i != message.getDestinataires().size() - 1) {
                stringBuilder.append(";");
            }
        }
        stringBuilder
                .append("\n\n")
                .append(message.getCorps())
                .append(HeadersEnum.CRLF.toString());
        return stringBuilder.toString();
    }

    public boolean isAuthentified() {
        return isAuthentified;
    }
}