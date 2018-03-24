/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package smtp.server;

import common.Utilisateur;
import common.HeadersEnum;
import common.Message;
import codes.SmtpCodes;
import database.BdConnexion;

public class Commande {

    public static String quit(Connexion connexion) {
        return SmtpCodes.LOGOUT.toString();
    }

    public static String ready(String email, Connexion connexion) {
        quit(email, connexion);
        String password = connexion.read();
        Utilisateur utilisateur = new Utilisateur(email, password);
        Utilisateur client = BdConnexion.getUtilisateur(utilisateur);
        if (client == null) {
            return SmtpCodes.USER_UNKNOWN.toString();
        } else if (!client.equals(utilisateur)) {
            return SmtpCodes.WRONG_PASSWORD.toString();
        }
        connexion.setCurrentstate(StateEnum.CONNECTED);
        connexion.setClient(client);
        return SmtpCodes.AUTHENTIFICATED.toString();
    }

    public static String connected(String requete, Connexion connexion) {
        quit(requete, connexion);
        if (requete.contains(SmtpCodes.EHLO.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmtpCodes.OK.toString();
        }
        return null;
    }

    public static String senderApproved(String requete, Connexion connexion) {
        quit(requete, connexion);
        if (requete.contains(SmtpCodes.RCPT_TO.toString()) && getMailAddress(requete) != null) {
            Utilisateur destinataire = BdConnexion.getUtilisateur(new Utilisateur(getMailAddress(requete)));
            if (destinataire == null) {
                return SmtpCodes.USER_UNKNOWN.toString();
            }
            Message message = new Message(connexion.getClient());
            message.addDestinataire(destinataire);
            connexion.setMailToSend(message);
            connexion.setCurrentstate(StateEnum.RECIPIENT_APPROVED);
            return SmtpCodes.OK.toString();
        }
        if (requete.equals(SmtpCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmtpCodes.OK.toString();
        }
        return SmtpCodes.COMMAND_UNKNOWN.toString();
    }

    public static String writingMail(String rawMail, Connexion connexion) {
        quit(rawMail, connexion);
        if (rawMail.equals(SmtpCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmtpCodes.OK.toString();
        }
        Message message = parseRawMail(rawMail, connexion.getMailToSend());

        BdConnexion.registerMail(message);
        connexion.setCurrentstate(StateEnum.READY_TO_DELIVER);
        return SmtpCodes.OK.toString();
    }

    private static Message parseRawMail(String rawMail, Message message) {
        String[] tab = rawMail.split("\n\n");
        String headers = tab[0];
        StringBuilder _corps = new StringBuilder();
        for (int i = 1 ; i < tab.length; i++) {
            _corps.append(tab[i]);
            if (i < tab.length - 1) {
                _corps.append("\n\n");
            }
        }
        String sujet = headers.split(HeadersEnum.SUJET.toString())[1].split(HeadersEnum.TO.toString())[0];
        message.setCorps(_corps.toString());
        message.setSujet(sujet);
        return message;
    }

    public static String wait(String requete, Connexion connexion) {
        quit(requete, connexion);
        String[] t_mail = requete.split(SmtpCodes.MAIL_FROM.toString() + "<");
        if (t_mail.length < 1) {
            return SmtpCodes.COMMAND_UNKNOWN.toString();
        }
        String email = t_mail[1].split(">")[0];
        if (email.equals(connexion.getClient().getEmail())) {
            connexion.setCurrentstate(StateEnum.SENDER_APPROVED);
            return SmtpCodes.OK.toString();
        }
        return SmtpCodes.COMMAND_UNKNOWN.toString();
    }

    public static String getMailAddress(String rawMail) {
        return rawMail.split("<")[1].split(">")[0];
    }

    public static String recipientApproved(String requete, Connexion connexion) {
        quit(requete, connexion);
        if (requete.equals(SmtpCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmtpCodes.OK.toString();
        }
        if (requete.contains(SmtpCodes.RCPT_TO.toString()) && getMailAddress(requete) != null) {
            Utilisateur destinataire = BdConnexion.getUtilisateur(new Utilisateur(getMailAddress(requete)));
            if (destinataire == null) {
                return SmtpCodes.USER_UNKNOWN.toString();
            }
            connexion.addRecipentToMail(destinataire);
            return SmtpCodes.OK.toString();
        }
        if (requete.contains(SmtpCodes.DATA.toString())) {
            connexion.setCurrentstate(StateEnum.WRITING_MAIL);
            return SmtpCodes.MESSAGE.toString();
        }
        System.out.println("requete =" + requete);
        System.out.println(connexion.getMailToSend());
        System.out.println("retourne commande inconnue");
        return SmtpCodes.COMMAND_UNKNOWN.toString();
    }

    public static String deliverMail(String requete, Connexion connexion) {
        if (requete.equals(SmtpCodes.MAIL_FROM.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            wait(requete, connexion);
        }
        if (requete.equals(SmtpCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmtpCodes.OK.toString();
        }
        return SmtpCodes.COMMAND_UNKNOWN.toString();
    }

    public static void quit(String requete, Connexion connexion) {
        if (requete.equals(SmtpCodes.QUIT.toString())) {
            connexion.close();
        }
    }

    public static String closed(Connexion connexion) {
        connexion.setCurrentstate(StateEnum.READY);
        return SmtpCodes.READY.toString();
    }
}