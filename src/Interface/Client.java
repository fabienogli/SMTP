package Interface;

import common.Message;
import common.MessageBox;
import common.Utilisateur;
import pop3.ClientPop3;
import smtp.ClientSmtp;

import java.io.IOException;
import java.util.List;

public class Client {

    private ClientPop3 pop3;
    private ClientSmtp smtp;
    private Utilisateur utilisateur;

    public Client() throws IOException {
        this.pop3 = new ClientPop3();
        this.smtp = new ClientSmtp();
    }

    public Client(Utilisateur utilisateur) throws IOException {
        this();
        this.utilisateur = utilisateur;
        utilisateur.setName();
        this.pop3.setUtilisateur(utilisateur);
        this.pop3.authentificationApop();
        this.smtp.authentification();
        this.smtp.setUtilisateur(utilisateur);
    }

    //@TODO Implémenter les actions que le client peut executer ci dessous

    public void list() { //interet ?

    }

    public MessageBox getSentMessages() {
        MessageBox messageBox = new MessageBox();
        return messageBox;
    }

    public MessageBox getReceivedMessages() {
        MessageBox messageBox = new MessageBox();
        return messageBox;
    }

    public List<Utilisateur> getWrongUtilisateur() {
        //@Todo retourner un liste vide s'il y en a pas
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void sendMail(Message message) {
        this.smtp.sendMail(message);
    }

    public void quit() {
        this.smtp.quit();
        this.pop3.logout();
    }
}
