/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import java.io.IOException;

public class States {

    public static String connected(String requete, Connexion connexion) {
        String[] arg = requete.split(" ");
        String returnCode = "";

        switch (arg[0]) {
            //commandes prises en charge par cet etat
            case "APOP":
                returnCode = Commande.apop(requete, connexion);
                break;
            case "USER":
                System.out.println("envoi user");
                if (arg.length == 2) {
                    returnCode = Commande.user(arg[1], connexion);
                } else returnCode = "-ERR mauvais nombre arguments";
                break;
            case "QUIT":
                try {
                    returnCode = Commande.quit(connexion);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //commandes non prises en charge cet etat
                break;
            default:
                returnCode = "-ERR commande non autorisee";
                break;
        }
        return returnCode;
    }

    public static String wait(String requete, Connexion connexion) {
        String[] arg = requete.split(" ");
        String returnCode = "";

        switch (arg[0]) {
            //commandes prises en charge par cet etat
            case "LIST":
                if (arg.length == 1) {
                    returnCode = Commande.list(connexion);
                } else if(arg.length==2){
                    //verifier que le message nest pas marqué a effacer
                    returnCode= Commande.list(connexion,Integer.parseInt(arg[1]));
                }
                else {
                    returnCode = "-ERR nombre arguments invalide";
                }
                break;
            case "STAT":
                if (arg.length == 1) {
                    returnCode = Commande.stat(connexion);
                } else returnCode = "-ERR nombre arguments invalide";
                break;
            case "RETR":
                //verifier que le message nest pas marqué a effacer
                if (arg.length == 2) {
                    returnCode = Commande.retrieve(Integer.parseInt(arg[1]), connexion);
                } else returnCode = "-ERR nombre arguments invalide";
                break;
            case "DELE":
                //verifier que le message nest pas marqué a effacer
                if (arg.length == 2) {
                    returnCode = Commande.delete(connexion,Integer.parseInt(arg[1]));
                } else returnCode = "-ERR nombre arguments invalide";
            case "NOOP":
                break;
            case "QUIT":
                if (arg.length == 1) {
                    try {
                        returnCode = Commande.quit(connexion);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else returnCode = "-ERR nombre arguments invalide";
                //commandes non prises en charge cet etat
                break;
            //commandes non prises en charge cet etat
            default:
                returnCode = "-ERR commande non autorisee";
                break;
        }

        return returnCode;
    }

    public static String senderApproved(String requete, Connexion connexion) {
        String[] arg = requete.split(" ");
        String returnCode = "";

        switch (arg[0]) {
            case "PASS":
                System.out.println("envoi mdp");
                if (arg.length == 2) {
                    returnCode += Commande.pass(arg[1], connexion);
                } else returnCode = "-ERR mauvais nombre arguments";
                break;
            default:
                returnCode = "-ERR commande non autorisee ";
                break;
        }
        return returnCode;
    }

    public static String closed(Connexion connexion) {
        connexion.setCurrentstate(StateEnum.CLOSED);
        return Commande.ready();
    }

    public static String recipientApproved(Connexion connexion) {
        connexion.setCurrentstate(StateEnum.CLOSED);
        return Commande.ready();
    }

    public static String writingMail(Connexion connexion) {
        connexion.setCurrentstate(StateEnum.CLOSED);
        return Commande.ready();
    }

    public static String readyToDeliver(Connexion connexion) {
        connexion.setCurrentstate(StateEnum.CLOSED);
        return Commande.ready();
    }
}
