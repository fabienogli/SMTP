/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import codes.STMPCodes;
import database.BdConnexion;

import java.util.Date;

public class Commande {

    private static String timestamp;
    
    public static String ehlo(String requete, Connexion connexion) {
        if (requete.contains(STMPCodes.HELLO.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return STMPCodes.OK.toString();
        }
        return STMPCodes.COMMAND_UNKNOWN.toString();
    }

    public static String quit(Connexion connexion) {
        return STMPCodes.LOGOUT.toString();
    }

    public static String ready(Connexion connexion) {
        connexion.write(STMPCodes.READY.toString());
        String email = connexion.read();
        String password = connexion.read();
        Utilisateur utilisateur = new Utilisateur(email, password);
        Utilisateur client = BdConnexion.getUtilisateur(utilisateur);
        if (client == null) {
            return STMPCodes.USER_UNKNOWN.toString();
        } else if (client.getMdp() != utilisateur.getMdp()) {
            return STMPCodes.WRONG_PASSWORD.toString();
        }
        connexion.setCurrentstate(StateEnum.CONNECTED);
        connexion.setClient(client);
        return STMPCodes.AUTHENTIFICATED.toString();
    }

    public static String connected(String requete, Connexion connexion) {
        if (requete.contains(STMPCodes.HELLO.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return STMPCodes.OK.toString();
        }
        return null;
    }

    public static String senderApproved(String requete, Connexion connexion) {
        if (requete.contains(STMPCodes.RCPT_TO.toString()) && getMailAddress(requete) != null) {
            Utilisateur destinataire = new Utilisateur(getMailAddress(requete));
            if (BdConnexion.getUtilisateur(destinataire) == null) {
                return STMPCodes.USER_UNKNOWN.toString();
            }
            Message message = new Message("id", destinataire, connexion.getClient(), new Date());
            connexion.setMailToSend(message);
            connexion.setCurrentstate(StateEnum.RECIPIENT_APPROVED);
            return STMPCodes.OK.toString();
        }
        if (requete.equals(STMPCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return STMPCodes.OK.toString();
        }
        return STMPCodes.COMMAND_UNKNOWN.toString();
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
        return STMPCodes.OK.toString();
    }

    public static String wait(String requete, Connexion connexion) {
        String[] t_mail = requete.split(STMPCodes.MAIL_FROM.toString() + "<");
        if (t_mail.length < 1) {
            return STMPCodes.COMMAND_UNKNOWN.toString();
        }
        String email = t_mail[1].split(">")[1];
        if (email.equals(connexion.getClient().getEmail())) {
            return STMPCodes.OK.toString();
        }
        return STMPCodes.COMMAND_UNKNOWN.toString();
    }

    public static String getMailAddress(String rawMail) {
        return rawMail.split("<")[1].split(">")[0];
    }

    public static String recipientApproved(String requete, Connexion connexion) {
        if (requete.equals(STMPCodes.RESET.toString())) {
            connexion.setCurrentstate(StateEnum.WAIT);
            return STMPCodes.OK.toString();
        }
        if (requete.contains(STMPCodes.RCPT_TO.toString()) && getMailAddress(requete) != null) {
            Utilisateur destinataire = new Utilisateur(getMailAddress(requete));
            if (BdConnexion.getUtilisateur(destinataire) == null) {
                return STMPCodes.USER_UNKNOWN.toString();
            }
            connexion.addRecipentToMail(destinataire);
        }
        if (requete.contains(STMPCodes.DATA.toString())) {
            connexion.setCurrentstate(StateEnum.WRITING_MAIL);
            return STMPCodes.MESSAGE.toString();
        }
        return STMPCodes.COMMAND_UNKNOWN.toString();
    }
}