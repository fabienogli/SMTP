package pop3.server;

import codes.Pop3Codes;
import common.Message;
import common.MessageBox;
import database.BdConnexion;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Commande {

    protected static final int i_USER = 0;
    private static String cheminDatabase = "src/database/";
    private static String timestamp;

    public static String quit(Connexion connexion) {
        String result = Pop3Codes.QUIT.toString();
        connexion.setCurrentstate(StateEnum.ATTENTE_CONNEXION);
        //gerer le cas ou des messages ont ete marque a efface
        return result;

    }

    public static String user(String user, Connexion connexion) {
        String result = Pop3Codes.MAILBOX_INVALID.toString();
        //verifier si la boite aux lettres existe
        if (isUserValid(user)) {
            connexion.setUSER(user);
            connexion.setCurrentstate(StateEnum.AUTHENTIFICATION);
            result = Pop3Codes.MAILBOX_VALID.toString();
        }

        //si oui

        return result;
    }

    public static String pass(String password, Connexion connexion) {
        String result = Pop3Codes.PASSWORD_INVALID.toString();
        //verifier le mot de passe pour l'identifiant donné
        if (BdConnexion.isPassValid(password, connexion.getUSER())) {
            connexion.setCurrentstate(StateEnum.TRANSACTION);
            connexion.setMailBox(addMail(connexion.getUSER()));
            result = Pop3Codes.PASSWORD_VALID.toString();
        }

        return result;
    }

    public static String apop(String requete, Connexion connexion) {
        String result = Pop3Codes.APOP_INVALID.toString();

        String[] tab = requete.split(" ");
        if (tab.length != 3) {
            return result;
        }
        String USER = tab[1];
        String APOP = tab[2];

        if (Commande.isApopValid(USER, APOP, connexion)) {
            connexion.setUSER(USER);
            connexion.setCurrentstate(StateEnum.TRANSACTION);
            connexion.setMailBox(addMail(connexion.getUSER()));
            result = Pop3Codes.APOP_VALID.toString();
        }

        return result;
    }

    public static String list(Connexion connexion) {
        StringBuilder result = new StringBuilder();
        if (connexion.getMailBox().getNumberMessages() == 0) {
            result.append("+OK ").append(" no message");
        } else if (connexion.getMailBox().getNumberMessages() == 1) {
            result.append("+OK ").append(" 1 message ");
            result.append("(").append(connexion.getMailBox().getBytes()).append(")");
            result.append(connexion.getMailBox().buildList());
        } else {
            result.append("+OK ").append(connexion.getMailBox().getNumberMessages()).append(" messages");
            result.append("(").append(connexion.getMailBox().getBytes()).append(")");
            result.append(connexion.getMailBox().buildList());
        }
        result.append("\n.");

        return result.toString();
    }

    public static String list(Connexion connexion, int numMsg) {
        StringBuilder sb = new StringBuilder();
        if (((numMsg - 1) < 0) || ((numMsg - 1) > connexion.getMailBox().getNumberMessages())) {
            sb.append("-ERR numero de message invalide");
        } else if (connexion.getMailBox().getMessage(numMsg - 1).isDeleteMark()) {
            sb.append("-ERR message marque supprimé");
        } else {
            sb.append("+OK ").append(numMsg).append(" ").append(connexion.getMailBox().getMessage(numMsg - 1));
        }

        return sb.toString();
    }

    public static String stat(Connexion connexion) {
/*" +OK " suivi par un simple espace, le nombre de message dans le dépôt de courrier,
 un simple espace et la taille du dépôt de courrier en octets*/
        StringBuilder statSb = new StringBuilder();
        statSb.append("+OK ").append(connexion.getMailBox().getNumberMessages()).append(" ").append(connexion.getMailBox().getBytes());

        return statSb.toString();
    }

    public static String delete(Connexion connexion,int numMsg) {
        StringBuilder sb = new StringBuilder();
        if (((numMsg - 1) < 0) || ((numMsg - 1) > connexion.getMailBox().getNumberMessages())) {
            sb.append("-ERR numero de message invalide");
        } else if (connexion.getMailBox().getMessage(numMsg - 1).isDeleteMark()) {
            sb.append("-ERR message ").append(numMsg).append(" deja marque supprime");
        } else {
            sb.append("+OK ").append("message ").append(numMsg).append("supprime");
        }

        return sb.toString();
    }

    public static String retrieve(int num, Connexion connexion) {
        StringBuilder mailSb = new StringBuilder();
        if (num <= 0 || num > connexion.getMailBox().getNumberMessages()) {
            System.out.println(connexion.getMailBox().getNumberMessages());
            System.out.print("RETR echec mauvais numero de message");
            return "-ERR no such messages,only " + connexion.getMailBox().getNumberMessages() + " messages in maildrop"+"\n.";
        } else {
            Message mail = connexion.getMailBox().getMessage(num - 1);
            if (mail.isDeleteMark()) {
                System.out.print("RETR echec mail supprime");
                return "-ERR message " + num + " is deleted"+"\n.";
            }
            System.out.print("RETR succes");
            System.out.println(mail.toString());

            mailSb.
                    append("+OK ")
                    .append(mail.size())
                    .append(" octets")
                    .append("\n")
                    .append(mail.toString());
        }
        System.out.println(mailSb.toString());
        return mailSb.toString();
    }

    public static String ready() {
        //envoi message ready
        return "Serveur POP3 de Mark-Fabien-Florian Ready "+ generateDateStamp() + "\n";
    }

    public static String encryptApop(String toEncrypt) {
        StringBuilder encryptMd5 = new StringBuilder();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encrApop = md5.digest(toEncrypt.getBytes());
            for (int i = 0; i < encrApop.length; ++i) {
                encryptMd5.append(Integer.toHexString((encrApop[i] & 0xFF) | 0x100).substring(1, 3));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encryptMd5.toString();
    }

    public static boolean isApopValid(String USER, String APOP, Connexion connexion) {

        try {
            FileReader fileReader = new FileReader(cheminDatabase + "users.csv");
            BufferedReader db = new BufferedReader(fileReader);
            String chaine;
            int i = 1;
            while ((chaine = db.readLine()) != null) {
                System.out.println("dans le while");
                if (i > 1) {
                    String[] tabChaine = chaine.split(",");
                    for (int x = 0; x < tabChaine.length - 1; x++) {
                        if (x == i_USER && USER.equals(tabChaine[x])) {

                            System.out.println("APOP normal " + connexion.getTimestamp().concat(tabChaine[x + 1]));
                            System.out.println("entree ? " + connexion.getTimestamp().contains("\n"));
                        }
                        if (x == i_USER && USER.equals(tabChaine[x]) && encryptApop(connexion.getTimestamp() + tabChaine[x + 1]).equals(APOP)) {
                            return true;
                        } else if (x == i_USER && USER.equals(tabChaine[x])) { //Le user est bien là mais le mot de passe ne correspond pas
                            return false;
                        }
                    }
                }
                i++;
            }
            db.close();
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier est introuvable !");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUserValid(String USER) {
        if (USER == null) {
            return false;
        }
        try {
            FileReader fileReader = new FileReader(cheminDatabase + "users.csv");
            BufferedReader db = new BufferedReader(fileReader);
            String chaine;
            int i = 1;
            while ((chaine = db.readLine()) != null) {
                if (i > 1) {
                    String[] tabChaine = chaine.split(",");
                    for (int x = 0; x < tabChaine.length; x++) {
                        if (x == i_USER && USER.equals(tabChaine[x])) {
                            return true;
                        }
                    }
                }
                i++;
            }
            db.close();
        } catch (FileNotFoundException e) {
            System.out.println("Le fichier est introuvable !");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    protected static MessageBox addMail(String user) {
        MessageBox mailBox = new MessageBox();
        List<Message> mails = new ArrayList<Message>();
        StringBuilder rawMessages = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(cheminDatabase + "received/" + user + "_messages");
            BufferedReader db = new BufferedReader(fileReader);
            String line;
            while ((line = db.readLine()) != null) {
                rawMessages.append(line);
                rawMessages.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] rawMessagesInArray = rawMessages.toString().split("\n.\n\n");
        for (String rawMessage : rawMessagesInArray) {
            mails.add(BdConnexion.parseMail(rawMessage));
        }
        mailBox.setMessages(mails);
        return mailBox;
    }

    private static String generateDateStamp(){
        StringBuilder dateStamp = new StringBuilder();
        String stamp ="" ;
        Date date = new Date();
        String uniqueID = UUID.randomUUID().toString();
        dateStamp.append(uniqueID).append(date);
        MessageDigest m;
        try {
            m= MessageDigest.getInstance("MD5");
            m.update(dateStamp.toString().getBytes(),0,dateStamp.toString().length());
            stamp= new BigInteger(1,m.digest()).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "<"+stamp+"@pop.markFabienFlo.fr"+">";

    }

}