/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import codes.SmptCodes;
import database.BdConnexion;

import java.util.Date;

public class Commande {

    private static String timestamp;
    
    public static String ehlo(String requete, Connexion connexion) {
        if (requete.contains(SmptCodes.EHLO.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmptCodes.OK.toString();
        }
        return SmptCodes.COMMAND_UNKNOWN.toString();
    }

    public static String quit(Connexion connexion) {
        return SmptCodes.LOGOUT.toString();
    }

    public static String ready(Connexion connexion) {
        connexion.write(SmptCodes.READY.toString());
        String email = connexion.read();
        String password = connexion.read();
        Utilisateur utilisateur = new Utilisateur(email, password);
        Utilisateur client = BdConnexion.getUtilisateur(utilisateur);
        if (client == null) {
            return SmptCodes.USER_UNKNOWN.toString();
        } else if (client.getMdp() != utilisateur.getMdp()) {
            return SmptCodes.WRONG_PASSWORD.toString();
        }
        connexion.setCurrentstate(StateEnum.CONNECTED);
        connexion.setClient(client);
        return SmptCodes.AUTHENTIFICATED.toString();
    }

    public static String connected(String requete, Connexion connexion) {
        if (requete.contains(SmptCodes.EHLO.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmptCodes.OK.toString();
        }
        return null;
    }

    public static String senderApproved(String requete, Connexion connexion) {
        if (requete.contains(SmptCodes.RCPT_TO.toString()) && getMailAddress(requete) != null) {
            Utilisateur destinataire = new Utilisateur(getMailAddress(requete));
            if (BdConnexion.getUtilisateur(destinataire) == null) {
                return SmptCodes.USER_UNKNOWN.toString();
            }
            Message message = new Message("id", destinataire, connexion.getClient(), new Date());
            connexion.setMailToSend(message);
            connexion.setCurrentstate(StateEnum.RECIPIENT_APPROVED);
            return SmptCodes.OK.toString();
        }
        if (requete.equals(SmptCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmptCodes.OK.toString();
        }
        return SmptCodes.COMMAND_UNKNOWN.toString();
    }

    public static String writingMail(String requete, Connexion connexion) {
        //TODO logique pour récupérer sujet et corps d'email
        String subject = "";
        String corps="";
        Message mail = connexion.getMailToSend();
        mail.setCorps(corps);
        mail.setCorps(subject);
        connexion.setMailToSend(mail);
        //TODO envoyer le mail
        connexion.setCurrentstate(StateEnum.READY_TO_DELIVER);
        return SmptCodes.OK.toString();
    }

    public static String wait(String requete, Connexion connexion) {
        String[] t_mail = requete.split(SmptCodes.MAIL_FROM.toString() + "<");
        if (t_mail.length < 1) {
            return SmptCodes.COMMAND_UNKNOWN.toString();
        }
        String email = t_mail[1].split(">")[1];
        if (email.equals(connexion.getClient().getEmail())) {
            return SmptCodes.OK.toString();
        }
        return SmptCodes.COMMAND_UNKNOWN.toString();
    }

    public static String getMailAddress(String rawMail) {
        return rawMail.split("<")[1].split(">")[0];
    }

    public static String recipientApproved(String requete, Connexion connexion) {
        if (requete.equals(SmptCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return SmptCodes.OK.toString();
        }
        if (requete.contains(SmptCodes.RCPT_TO.toString()) && getMailAddress(requete) != null) {
            Utilisateur destinataire = new Utilisateur(getMailAddress(requete));
            if (BdConnexion.getUtilisateur(destinataire) == null) {
                return SmptCodes.USER_UNKNOWN.toString();
            }
            connexion.addRecipentToMail(destinataire);
        }
        if (requete.contains(SmptCodes.DATA.toString())) {
            connexion.setCurrentstate(StateEnum.WRITING_MAIL);
            return SmptCodes.MESSAGE.toString();
        }
        return SmptCodes.COMMAND_UNKNOWN.toString();
    }

    public static String deliverMail(String requete, Connexion connexion) {
        return null;
    }
}