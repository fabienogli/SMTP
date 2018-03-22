/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import codes.SmtpCodes;
import database.BdConnexion;

import java.util.Date;

public class Commande {

    private static String timestamp;

    public static String ehlo(String requete, Connexion connexion) {
        if (requete.contains(SmtpCodes.EHLO.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmtpCodes.OK.toString();
        }
        return SmtpCodes.COMMAND_UNKNOWN.toString();
    }

    public static String quit(Connexion connexion) {
        return SmtpCodes.LOGOUT.toString();
    }

    public static String ready(String requete, Connexion connexion) {
        String email = requete;
        quit(email, connexion);
        String password = connexion.read();
        Utilisateur utilisateur = new Utilisateur(email, password);
        Utilisateur client = BdConnexion.getUtilisateur(utilisateur);
        System.out.println(client);
        System.out.println(utilisateur);
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
            Utilisateur destinataire = new Utilisateur(getMailAddress(requete));
            if (BdConnexion.getUtilisateur(destinataire) == null) {
                return SmtpCodes.USER_UNKNOWN.toString();
            }
            Message message = new Message("id", destinataire, connexion.getClient(), new Date());
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

    public static String writingMail(String requete, Connexion connexion) {
        quit(requete, connexion);
        //TODO logique pour récupérer sujet et corps d'email
        String subject = "";
        String corps = "";
        Message mail = connexion.getMailToSend();
        mail.setCorps(corps);
        mail.setCorps(subject);
        connexion.setMailToSend(mail);
        //TODO envoyer le mail
        connexion.setCurrentstate(StateEnum.READY_TO_DELIVER);
        return SmtpCodes.OK.toString();
    }

    public static String wait(String requete, Connexion connexion) {
        quit(requete, connexion);
        String[] t_mail = requete.split(SmtpCodes.MAIL_FROM.toString() + "<");
        if (t_mail.length < 1) {
            return SmtpCodes.COMMAND_UNKNOWN.toString();
        }
        String email = t_mail[1].split(">")[1];
        if (email.equals(connexion.getClient().getEmail())) {
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
            Utilisateur destinataire = new Utilisateur(getMailAddress(requete));
            if (BdConnexion.getUtilisateur(destinataire) == null) {
                return SmtpCodes.USER_UNKNOWN.toString();
            }
            connexion.addRecipentToMail(destinataire);
        }
        if (requete.contains(SmtpCodes.DATA.toString())) {
            connexion.setCurrentstate(StateEnum.WRITING_MAIL);
            return SmtpCodes.MESSAGE.toString();
        }
        return SmtpCodes.COMMAND_UNKNOWN.toString();
    }

    public static String deliverMail(String requete, Connexion connexion) {
        return null;
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