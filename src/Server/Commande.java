/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import Client.CommandEnum;
import database.BdConnexion;

public class Commande {

    private static String timestamp;
    
    public static String ehlo(String requete, Connexion connexion) {
        return requete;
    }

    public static String quit(Connexion connexion) {
        return "";
    }

    public static String ready(Connexion connexion) {
        connexion.write(CommandEnum.READY.toString());
        String email = connexion.read();
        String password = connexion.read();
        Utilisateur utilisateur = new Utilisateur(email, password);
        Utilisateur client = BdConnexion.getUtilisateur(utilisateur);
        if (client == null) {
            return "unknown";
        }
        connexion.setCurrentstate(StateEnum.CONNECTED);
        connexion.setClient(client);
        return CommandEnum.AUTHENTIFICATED.toString();
    }
    public static String ready() {
        return "";
    }
}