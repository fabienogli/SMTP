package database;

import ServerSmtp.Message;
import ServerSmtp.MessageBox;
import ServerSmtp.Utilisateur;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BdConnexion {

    private static String cheminDatabase = "src/database/";

    public static List<Utilisateur> getUtilisateur() {
        List<Utilisateur> utilisateurs = new ArrayList<Utilisateur>();
        try {
            FileReader fileReader = new FileReader(cheminDatabase + "users.csv");
            BufferedReader db = new BufferedReader(fileReader);
            String chaine;
            int i = 1;
            while ((chaine = db.readLine()) != null) {
                if (i > 1) {
                    Utilisateur utilisateur = new Utilisateur();
                    String[] tabChaine = chaine.split(",");
                    for (int x = 0; x < tabChaine.length; x++) {
                        if (x == 0) {
                            utilisateur.setNom(tabChaine[x]);
                        } else if (x == 1) {
                            utilisateur.setMdp(tabChaine[x]);
                        } else {
                            utilisateur.setEmail(tabChaine[x]);
                        }
                    }
                    utilisateurs.add(utilisateur);
                }
                i++;
            }
            db.close();

        } catch (FileNotFoundException e) {
            System.out.println("Le fichier est introuvable !");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    public static Utilisateur getUtilisateur(Utilisateur user) {
        Utilisateur utilisateur = new Utilisateur();
        try {
            FileReader fileReader = new FileReader(cheminDatabase + "users.csv");
            BufferedReader db = new BufferedReader(fileReader);
            String chaine;
            int i = 1;
            while ((chaine = db.readLine()) != null) {
                if (i > 1) {
                    String[] tabChaine = chaine.split(",");
                    for (int x = 0; x < tabChaine.length; x++) {
                        if (x == 0) {
                            utilisateur.setNom(tabChaine[x]);
                        } else if (x == 1) {
                            utilisateur.setMdp(tabChaine[x]);
                        } else {
                            utilisateur.setEmail(tabChaine[x]);
                            if (utilisateur.getEmail().equals(user.getEmail())) {
                                return utilisateur;
                            }
                        }
                    }
                }
                i++;
            }
            db.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MessageBox getMessages(Utilisateur utilisateur) {
        MessageBox mailBox = new MessageBox();
        List<Message> mails = new ArrayList<Message>();
        StringBuilder rawMessages = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(cheminDatabase + utilisateur.getNom() + "_messages");
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
        mailBox.setUtilisateur(utilisateur);
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

    private static void parseHeader(Message mail, String line) {
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
        switch (key.toUpperCase()) {

            case "TO":
                String valuesTo[] = value.split(" ");
                mail.addDestinataire(new Utilisateur(valuesTo[0], valuesTo[1]));
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

    public static void writeToFile(Utilisateur utilisateur, String content) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        PrintWriter out = null;

        try {
            fw = new FileWriter(cheminDatabase + utilisateur.getNom() + "_messages", true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
