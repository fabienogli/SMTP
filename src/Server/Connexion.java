/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;


public class Connexion implements Runnable {

    private Socket clientSocket;
    private StateEnum currentstate = StateEnum.ATTENTE_CONNEXION;
    private String USER;
    private MessageBox mailBox;
    private String timestamp;

    public StateEnum getCurrentstate() {
        return this.currentstate;
    }

    public void setCurrentstate(StateEnum currentstate) {
        this.currentstate = currentstate;
    }

    public Connexion() {

    }

    public Connexion(InetAddress clientAdress, Socket clientSocket, int clientPort) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        //Dans le run de serveur
        System.out.println(this.currentstate);
        boolean resultCommand = true;
        while (resultCommand) {
            if (this.currentstate.equals(StateEnum.ATTENTE_CONNEXION)) {
                String result = States.attenteConnexion(this);
                saveTimestamp(result);
                write(result);
                System.out.println(this.currentstate);
            } else {
                    resultCommand = this.traiterCommande();

            }
        }
    }

    private void saveTimestamp(String result) {
        String[] tmp = result.split("\n")[0].split(" ");
        timestamp = tmp[tmp.length -1];
        System.out.println("timestamp sauvegardé: "+ timestamp);
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
            case AUTHENTIFICATION:
                result = States.authentification(requete, this);
                break;
            case AUTHORIZATION:
                result = States.authorization(requete, this);
                break;
            case TRANSACTION:
                result = States.transaction(requete, this);
                break;
            default:
                result = "-ERR";
                break;
        }
        System.out.println(this.currentstate);
        write(result);
        return true;
    }


    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
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
}
