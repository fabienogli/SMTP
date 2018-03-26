package database;

import common.HeadersEnum;
import common.Message;
import common.MessageBox;
import common.Utilisateur;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BdConnexion {

    private static String cheminDatabase = "src/database/";

    public static List<Utilisateur> getUtilisateurs() {
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

    private static List<Message> getMessages(Utilisateur utilisateur, String type) {
        List<Message> mails = new ArrayList<Message>();
        StringBuilder rawMessages = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(cheminDatabase + type + utilisateur.getNom() + "_messages");
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
            System.out.println(rawMessage.split(": ")[0]);
            if (!rawMessage.split(": ")[0].equals(HeadersEnum.FROM.getString())) {
                continue;
            }
            mails.add(parseMail(rawMessage));
        }
        return mails;
    }

    public static List<Message> getSentMessages(Utilisateur utilisateur) {
        return getMessages(utilisateur, "sent/");
    }

    public static List<Message> getReceiveedMessages(Utilisateur utilisateur) {
        return getMessages(utilisateur, "received/");
    }

    public static Message parseMail(String rawMail) {
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

    public static Utilisateur parseUser(String rawUser) {
        System.out.println("dans parseUser");
        System.out.println(rawUser);
        String[] tab = rawUser.split(" <");
        Utilisateur auteur = new Utilisateur(tab[1].split(">")[0]);
        auteur.setNom(tab[0]);
        return auteur;
    }

    private static void parseHeader(Message mail, String line) {
        System.out.println("dans parse header");
        System.out.println(line);
        String header = line.split(": ")[0];
        String value = line.split(": ")[1];
        if (header.equals(HeadersEnum.FROM.getString())) {
            mail.setAuteur(parseUser(value));
        } else if (header.equals(HeadersEnum.TO.getString())) {
            for (String user : value.split(";")) {
                mail.addDestinataire(parseUser(user));
            }
        } else if (header.equals(HeadersEnum.DATE.getString())) {
            System.out.println(value);
            mail.setDate(value);
        } else if (header.equals(HeadersEnum.SUJET.getString())) {
            mail.setSujet(value);
        } else if (header.equals(HeadersEnum.ID.getString())) {
            String id = "id_message";
            try {
                id = value.split("<")[1].split("@")[0];
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            mail.setId(id);
        }
        System.out.println("sortie de parse Header");
    }

    public static void registerMail(Message message) {
        writeToFile(message.getAuteur(), message.toString(), "sent");

        for (Utilisateur utilisateur : message.getDestinataires()) {
            // la deuxieme condition sera a modifer sur chacun de nos serveur
            if (utilisateur.domainName().equals("mail.com") || utilisateur.domainName().equals("smtp.fr")) {
                writeToFile(utilisateur, message.toString(), "received");
            }
        }
    }

    private static void writeToFile(Utilisateur utilisateur, String content, String typeBoite) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        PrintWriter out = null;

        try {
            fw = new FileWriter(cheminDatabase + typeBoite + "/" + utilisateur.getNom() + "_messages", true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.println();
            out.print(content);
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
                        if (x == 0 && USER.equals(tabChaine[x]) && tabChaine[x + 1].equals(PASS)) {
                            return true;
                        } else if (x == 0 && USER.equals(tabChaine[x])) { //Le user est bien là mais le mot de passe ne correspond pas
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


}
