package smtp;

import common.HeadersEnum;
import common.Message;
import common.StreamHandling;
import codes.SmtpCodes;
import common.Utilisateur;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClientSmtp {

    //private Socket clientSocket;
    private int port;
    private InetAddress adresseIp;
    private Utilisateur utilisateur;
    private SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private SSLSocket clientSocket;
    private boolean isAuthentified;

    public ClientSmtp(InetAddress adresseIp, int port) throws IOException {
        this.adresseIp = adresseIp;
        this.port = port;
        this.isAuthentified = false;

    }

    public ClientSmtp() throws IOException {
        this(java.net.InetAddress.getByName("localhost"), 2026);
        this.isAuthentified = false;
    }

    public ClientSmtp(Utilisateur utilisateur) throws IOException {
        this();
        this.utilisateur = utilisateur;
    }

    public ClientSmtp(InetAddress adresseIp, int port, Utilisateur utilisateur) throws IOException {
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
        this.clientSocket = (SSLSocket) sslsocketfactory.createSocket(this.getAdresseIp(), this.getPort());
        this.clientSocket.setEnabledCipherSuites(new String[]{"TLS_DH_anon_WITH_AES_128_CBC_SHA"});
        return read();
    }

    public void authentification() {
        this.isAuthentified = true;
        write(SmtpCodes.EHLO.toString());
        String ok = read();
        if (!ok.equals(SmtpCodes.OK.toString())) {
            System.out.println(ok);
            this.isAuthentified = false;
        } else {
            this.isAuthentified = true;
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
        String data = "";
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

    public String sendMail(Message message) {
        write(SmtpCodes.MAIL_FROM.toString() + "<" + message.getAuteur().getEmail() + ">");
        String response = read();
        if (!response.equals(SmtpCodes.OK.toString())) {
            System.out.println(response);
        }
        StringBuilder sb = new StringBuilder();
        boolean receiverErrors = false;

        for (Utilisateur destinataire : message.getDestinataires()) {
            write(SmtpCodes.RCPT_TO.toString() + "<" + destinataire.getEmail() + ">");
            response = read();
            if (!response.equals(SmtpCodes.OK.toString())) {
                receiverErrors = true;
                System.out.println(response);
                sb.append(destinataire.getEmail()).append(";");
            }
        }
        if (receiverErrors) return "1;" + sb.toString();
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

        //On reset le client
        write(SmtpCodes.RESET.toString());
        response = read();
        if (!response.equals(SmtpCodes.OK.toString())) {
            System.out.println(response);
        }
        return "2";
    }

    private String parseMailForSmtp(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        message.setDate(generateDate());
        stringBuilder
                .append(HeadersEnum.FROM.toString())
                .append(message.getAuteur().getNom())
                .append(" <")
                .append(message.getAuteur().getEmail())
                .append(">\n")
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
        stringBuilder.append("\n")
                .append(HeadersEnum.SUJET.toString())
                .append(message.getSujet())
                .append("\n")
                .append(HeadersEnum.DATE.toString())
                .append(message.getDate());

        stringBuilder
                .append("\n\n")
                .append(message.getCorps())
                .append(HeadersEnum.CRLF.toString());
        return stringBuilder.toString();
    }

    public String generateDate() {
        // (1) get today's date
        Date today = Calendar.getInstance().getTime();

        // (2) create a date "formatter" (the date format we want)
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss Z", Locale.US);

        // (3) create a new String using the date format we want
        return formatter.format(today);
    }

    public boolean isAuthentified() {
        return isAuthentified;
    }
}