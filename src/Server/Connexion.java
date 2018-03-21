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
    private String USER;
    private MessageBox mailBox;
    private String timestamp;
    private Utilisateur client;

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
                String result = States.closed(this);
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
                result = States.wait(requete, this);
                break;
            case CONNECTED:
                result = States.connected(requete, this);
                break;
            case SENDER_APPROVED:
                result = States.senderApproved(requete, this);
                break;
            case RECIPIENT_APPROVED:
                result = States.senderApproved(requete, this);
                break;
            case WRITING_MAIL:
                result = States.writingMail(requete, this);
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

    public String getTimestamp() {
        return timestamp;
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
}
