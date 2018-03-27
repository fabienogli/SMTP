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
    private List<Message> receivedMails;
    private List<ModelMail> receivedModelMails;
    private HashMap <ModelMail, Message> receivedLinkMails;
    private List<Message> sentMails;
    private List<ModelMail> sentModelMails;
    private HashMap <ModelMail, Message> sentLinkMails;

    public Client() throws IOException {
        this.pop3 = new ClientPop3();
        this.smtp = new ClientSmtp();
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

    public void getSentMessages() {
        this.sentMails= new ArrayList<Message>();
        this.sentModelMails = new ArrayList<ModelMail>();
        this.sentLinkMails = new HashMap<>();
        this.sentMails = BdConnexion.getSentMessages(this.getUtilisateur());
        for (Message sent : sentMails) {
            ModelMail sentModel = new ModelMail(sent);
            this.sentModelMails.add(sentModel);
            this.sentLinkMails.put(sentModel, sent);
        }
    }

    public void getReceivedMessages() {
        this.receivedMails = new ArrayList<Message>();
        this.receivedModelMails = new ArrayList<ModelMail>();
        this.receivedLinkMails = new HashMap<>();

        String[] list = this.pop3.list().split("\n");
        for(int i = 1; i <= list.length-2; i++) {
            System.out.println(i);
            Message message = BdConnexion.parseMail(this.pop3.retr(i));
            System.out.println("fin retrair");
            if (message == null) {
                continue;
            }
            ModelMail modelMail = new ModelMail(message);
            this.receivedMails.add(message);
            this.receivedModelMails.add(modelMail);
            this.receivedLinkMails.put(modelMail, message);
        }
        System.out.println("fin receivedMessages");
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

    public List<Message> getReceivedMails() {
        return receivedMails;
    }

    public void setReceivedMails(List<Message> receivedMails) {
        this.receivedMails = receivedMails;
    }

    public List<ModelMail> getReceivedModelMails() {
        return this.receivedModelMails;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.smtp.setUtilisateur(utilisateur);
        this.pop3.setUtilisateur(utilisateur);
        utilisateur.setName();
    }

    public boolean authentification()  {
        boolean authApop = this.pop3.authentificationApop();
        if (authApop) {
            this.smtp.authentification();
        } /*else {
            throw new WrongLoginException("Authentification échoué");
        }
        if (!this.smtp.isAuthentified()) {
            throw new WrongLoginException("Authentification échoué");
        }*/
        return authApop;
    }

    public boolean isAuthentified() {
        return this.smtp.isAuthentified();
    }

    public HashMap<ModelMail, Message> getReceivedLinkMails() {
        return receivedLinkMails;
    }

    public void setReceivedLinkMails(HashMap<ModelMail, Message> receivedLinkMails) {
        this.receivedLinkMails = receivedLinkMails;
    }

    public List<Message> getSentMails() {
        return sentMails;
    }

    public List<ModelMail> getSentModelMails() {
        return sentModelMails;
    }

    public HashMap<ModelMail, Message> getSentLinkMails() {
        return sentLinkMails;
    }
}
