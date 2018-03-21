/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import java.io.*;
import java.net.InetAddress;
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
        //Dans le run de serveur
        boolean resultCommand = true;
        while (resultCommand) {
            if (this.currentstate.equals(StateEnum.CLOSED)) {
                String result = Commande.ready(this);
                write(result);
            } else {
                resultCommand = this.traiterCommande();
            }
        }
    }

    private boolean traiterCommande() {

        String result = "";
        String requete = read();
        if (requete.contains("QUIT")) {
            try {
                write(Commande.quit(this));
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
            PrintWriter outToClient = new PrintWriter(this.clientSocket.getOutputStream());
            outToClient.write(message);
            System.out.println("le serveur envoie" + message);
            outToClient.println();
            outToClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        String result = "";
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            result = fromClient.readLine();
            System.out.println("le serveur reçoit " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
}
