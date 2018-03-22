/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package ServerSmtp;

import codes.SmtpCodes;

import java.io.*;
import java.net.Socket;


public class Connexion implements Runnable {

    private Socket clientSocket;
    private StateEnum currentstate = StateEnum.CLOSED;
    private MessageBox mailBox;
    private Utilisateur client;
    private Message mailToSend;

    public StateEnum getCurrentstate() {
        return this.currentstate;
    }

    public void setCurrentstate(StateEnum currentstate) {
        this.currentstate = currentstate;
    }

    public Connexion() {
        client = new Utilisateur();
    }

    public Connexion(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        boolean resultCommand = true;
        while (resultCommand) {
            if (this.currentstate.equals(StateEnum.CLOSED)) {
                String result = Commande.closed(this);
                try {
                    StreamHandling.write(result, this.clientSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                resultCommand = this.traiterCommande();
            }
        }
    }

    private boolean traiterCommande() {

        String result = "";
        String requete;

        if (this.getCurrentstate() == StateEnum.WRITING_MAIL) {
            requete = readMultipleLines();
        } else {
            requete = read();
        }
        if (requete.length() == 0 || requete == null) {
            this.setCurrentstate(StateEnum.WAIT);
            write(SmtpCodes.COMMAND_UNKNOWN.toString());
            return true;
        }

        if (requete.equals(SmtpCodes.RESET.toString()) && !this.currentstate.equals(StateEnum.CLOSED) && !this.currentstate.equals(StateEnum.CONNECTED)) {
            this.currentstate = StateEnum.WAIT;
            write(SmtpCodes.OK.toString());
            return true;
        }

        if (requete.equals(SmtpCodes.QUIT.toString())) {
            try {
                StreamHandling.write(Commande.quit(this), this.clientSocket.getOutputStream());
                this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        switch (getCurrentstate()) {
            case WAIT:
                result = Commande.wait(requete, this);
                break;
            case CONNECTED:
                result = Commande.connected(requete, this);
                break;
            case SENDER_APPROVED:
                result = Commande.senderApproved(requete, this);
                break;
            case RECIPIENT_APPROVED:
                result = Commande.recipientApproved(requete, this);
                break;
            case WRITING_MAIL:
                result = Commande.writingMail(requete, this);
                break;
            case READY_TO_DELIVER:
                result = Commande.deliverMail(requete, this);
                break;
            case READY:
                result = Commande.ready(requete, this);
                break;
            default:
                result = "502 Command not executed ";
                break;
        }
        System.out.println(this.currentstate);
        write(result);
        return true;
    }

    public MessageBox getMailBox() {
        return mailBox;
    }

    public void setMailBox(MessageBox mailBox) {
        this.mailBox = mailBox;
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

    public Utilisateur getClient() {
        return client;
    }

    public void setClient(Utilisateur client) {
        this.client = client;
    }

    public Message getMailToSend() {
        return this.mailToSend;
    }

    public void setMailToSend(Message mailToSend) {
        this.mailToSend = mailToSend;
    }

    public void addRecipentToMail(Utilisateur utilisateur) {
        this.mailToSend.addDestinataire(utilisateur);
    }

    public void close() {
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
