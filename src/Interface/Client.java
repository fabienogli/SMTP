package Interface;

import Interface.main.ModelMail;
import common.Message;
import common.MessageBox;
import common.Utilisateur;
import database.BdConnexion;
import pop3.ClientPop3;
import smtp.ClientSmtp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client {

    private ClientPop3 pop3;
    private ClientSmtp smtp;
    private Utilisateur utilisateur;
    private List<Message> mails;
    private List<ModelMail> modelMails;
    private HashMap <ModelMail, Message> linkMails;

    public Client() throws IOException {
        this.pop3 = new ClientPop3();
        this.smtp = new ClientSmtp();
        this.mails = new ArrayList<Message>();
        this.modelMails = new ArrayList<ModelMail>();
        this.linkMails = new HashMap<>();
    }

    public Client(Utilisateur utilisateur) throws IOException {
        this();

        this.utilisateur = utilisateur;
        utilisateur.setName();

        this.pop3.setUtilisateur(utilisateur);
        this.smtp.setUtilisateur(utilisateur);

        this.start();
    }

    public void start() {
        try {
            this.pop3.start();
            this.smtp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //@TODO Implémenter les actions que le client peut executer ci dessous

    public void list() { //interet ?

    }

    public MessageBox getSentMessages() {
        MessageBox messageBox = new MessageBox();
        return messageBox;
    }

    public void getReceivedMessages() {
        String[] list = this.pop3.list().split("\n");
        for(int i = 1; i < list.length; i++) {
            Message message = BdConnexion.parseMail(this.pop3.retr(Character.getNumericValue(list[i].charAt(0))));
            ModelMail modelMail = new ModelMail(message);
            this.mails.add(message);
            this.modelMails.add(modelMail);
            this.linkMails.put(modelMail, message);
        }
    }

    public List<Utilisateur> getWrongUtilisateur() {
        //@Todo retourner un liste vide s'il y en a pas
        return null;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public String sendMail(Message message) {
        return this.smtp.sendMail(message);
    }

    public void quit() {
        this.smtp.quit();
        this.pop3.logout();
    }

    public List<Message> getMails() {
        return mails;
    }

    public void setMails(List<Message> mails) {
        this.mails = mails;
    }

    public List<ModelMail> getModelMails() {
        return this.modelMails;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.smtp.setUtilisateur(utilisateur);
        this.pop3.setUtilisateur(utilisateur);
        utilisateur.setName();
    }

    public void authentification() throws WrongLoginException {
        boolean authApop = this.pop3.authentificationApop();
        this.smtp.authentification();
        if (authApop && this.smtp.isAuthentified()) {
            throw new WrongLoginException("Authentification échoué");
        }
    }

    public boolean isAuthentified() {
        return this.smtp.isAuthentified();
    }
}
