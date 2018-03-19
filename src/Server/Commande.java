/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

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

    public static String quit(Connexion connexion) throws IOException {
        String result = "+OK Serveur POP3 de Mark-Florian-Fabien signing off";
        //deleteMarkedMails(connexion);
        connexion.setCurrentstate(StateEnum.ATTENTE_CONNEXION);
        //gerer le cas ou des messages ont ete marque a efface
        return result;

    }

    public static String user(String user, Connexion connexion) {
        String result = "-ERR Boite aux lettres invalide";
        //verifier si la boite aux lettres existe
        if (isUserValid(user)) {
            connexion.setUSER(user);
            connexion.setCurrentstate(StateEnum.AUTHENTIFICATION);
            result = "+OK Boite aux lettres valide";
        }

        //si oui

        return result;
    }

    public static String pass(String password, Connexion connexion) {
        String result = "-ERR Mot de passe invalide";
        //verifier le mot de passe pour l'identifiant donné
        if (isPassValid(password, connexion.getUSER())) {
            connexion.setCurrentstate(StateEnum.TRANSACTION);
            connexion.setMailBox(addMail(connexion.getUSER()));
            result = "+OK Authentification reussie";
        }

        return result;
    }

    public static String apop(String requete, Connexion connexion) {
        String result = "-ERR APOP invalide";

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
            result = "+OK authentification APOP réussie";
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
            sb.append("-ERR message marque supprime");
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

    public static String delete(Connexion connexion, int numMsg) {
        StringBuilder sb = new StringBuilder();
        if (((numMsg - 1) < 0) || ((numMsg - 1) > connexion.getMailBox().getNumberMessages())) {
            sb.append("-ERR numero de message invalide");
        } else if (connexion.getMailBox().getMessage(numMsg - 1).isDeleteMark()) {
            sb.append("-ERR message ").append(numMsg).append(" deja marque supprime");
        } else {
            sb.append("+OK ").append("message ").append(numMsg).append("supprime");
            connexion.getMailBox().getMessage(numMsg - 1).setDeleteMark(true);
        }

        return sb.toString();
    }

    public static String retrieve(int num, Connexion connexion) {
        StringBuilder mailSb = new StringBuilder();
        if (num <= 0 || num > connexion.getMailBox().getNumberMessages()) {
            System.out.println(connexion.getMailBox().getNumberMessages());
            System.out.print("RETR echec mauvais numero de message");
            return "-ERR no such messages,only " + connexion.getMailBox().getNumberMessages() + " messages in maildrop";
        } else {
            Message mail = connexion.getMailBox().getMessage(num - 1);
            if (mail.isDeleteMark()) {
                System.out.print("RETR echec mail supprime");
                return "-ERR message " + num + " is deleted";
            }
            System.out.print("RETR succes");
            mailSb.append("+OK " + mail.size() + " octets").append("\n").append(mail.toString());
        }
        System.out.println(mailSb.toString());
        return mailSb.toString();
    }

    public static String ready() {
        //envoi message ready
        return "Serveur POP3 de Mark-Fabien-Florian Ready " + generateDateStamp() + "\n";
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
//                        System.out.println("bonne encryption " + encryptApop(connexion.getTimestamp() + tabChaine[x + 1]));
//                        System.out.println("bonne encryption " + tabChaine[x + 1]).equals(APOP));
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

    public static boolean isPassValid(String PASS, String USER) {
        if (USER == null || PASS == null) {
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
                        if (x == i_USER && USER.equals(tabChaine[x]) && tabChaine[x + 1].equals(PASS)) {
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


    protected static MessageBox addMail(String user) {
        MessageBox mailBox = new MessageBox();
        List<Message> mails = new ArrayList<Message>();
        StringBuilder rawMessages = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(cheminDatabase + user + "_messages");
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
            mails.add(parseMail(rawMessage));
        }
        mailBox.setMessages(mails);
        return mailBox;
    }

    private static Message parseMail(String rawMail) {
        Message mail = new Message();
        //Parse header
        StringBuilder content = new StringBuilder();
        String[] rawMailPerLine = rawMail.split("\n");
        boolean headersDone = false;
        for (String line : rawMailPerLine) {

            if (line.length() == 0) {
                headersDone = true;
                System.out.println("HEADERSDONE");
                continue;
            }
            if (!headersDone) {
                parseHeader(mail, line);
            } else {
                content.append(line);
                content.append("\n");
            }

        }
        mail.setCorps(content.toString());
        return mail;
    }

    private static void parseHeader(Message mail, String line)
    //throws BadMailFormat
    {
        boolean delimiteurFound = false;
        String key = "";
        String value = "";
        char item;
        String test = "";
        for (int i = 0; i < line.length(); i++) {
            item = line.charAt(i);
            test += item;
            if (!delimiteurFound && item == ':')
                delimiteurFound = true;
            else {
                if (delimiteurFound)
                    value += item;
                else
                    key += item;
            }
        }


        if (!delimiteurFound) {
            return;
        }
        key = key.trim();
        value = value.trim();
        System.out.println(key);
        System.out.println(value);
        switch (key.toUpperCase()) {

            case "TO":
                String valuesTo[] = value.split(" ");
                mail.setDestinataire(new Utilisateur(valuesTo[0], valuesTo[1]));
                break;
            case "FROM":
                String valuesFrom[] = value.split(" ");
                mail.setAuteur(new Utilisateur(valuesFrom[0], valuesFrom[1]));
                break;
            case "SUBJECT":
                mail.setSujet(value);
                break;
            case "DATE":
                DateFormat format = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss Z", Locale.US);
                /*Date date = new Date();
                System.out.println(format.format(date));*/
                try {
                    mail.setDate(format.parse(value));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case "MESSAGE-ID":
                mail.setId(value);
                break;
            default:
                mail.addOptionalHeader(key, value);
                break;
        }

    }

    private static String generateDateStamp() {
        StringBuilder dateStamp = new StringBuilder();
        String stamp = "";
        Date date = new Date();
        String uniqueID = UUID.randomUUID().toString();
        dateStamp.append(uniqueID).append(date);
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(dateStamp.toString().getBytes(), 0, dateStamp.toString().length());
            stamp = new BigInteger(1, m.digest()).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "<" + stamp + "@pop.markFabienFlo.fr" + ">";

    }

    private static void deleteMarkedMails(Connexion connexion) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(cheminDatabase + connexion.getUSER()+ "_messages"));
        try {
            for (Message m : connexion.getMailBox().getMessages()
                    ) {
                if (!m.isDeleteMark()) {
                    writer.write(m.toString());
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}