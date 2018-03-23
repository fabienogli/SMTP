package pop3.server;

import common.MessageBox;
import common.StreamHandling;

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
            StreamHandling.write(message, clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read() {
        String result = "";
        try {
            result = StreamHandling.read(this.clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
