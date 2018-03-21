///*
// * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
// */
//
//package Server;
//
//import java.io.IOException;
//
//public class States {
//
//    public static String connected(String requete, Connexion connexion) {
//        String[] arg = requete.split(" ");
//        String returnCode = "";
//
//        switch (arg[0]) {
//            //commandes prises en charge par cet etat
//            case "EHLO":
//                returnCode = Commande.ehlo(requete, connexion);
//                break;
//            default:
//                returnCode = "-ERR commande non autorisee";
//                break;
//        }
//        return returnCode;
//    }
//
//    public static String wait(String requete, Connexion connexion) {
//        String[] arg = requete.split(" ");
//        String returnCode = "";
//
//        switch (arg[0]) {
//            //commandes prises en charge par cet etat
//            case "LIST":
//
//                break;
//
//            default:
//                returnCode = "-ERR commande non autorisee";
//                break;
//        }
//
//        return returnCode;
//    }
//
//    public static String senderApproved(String requete, Connexion connexion) {
//        String[] arg = requete.split(" ");
//        String returnCode = "";
//
//        switch (arg[0]) {
//            case "PASS":
//
//            default:
//                returnCode = "-ERR commande non autorisee ";
//                break;
//        }
//        return returnCode;
//    }
//
//    public static String closed(Connexion connexion) {
//        connexion.setCurrentstate(StateEnum.CLOSED);
//        return Commande.ready(connexion);
//    }
//
//    public static String recipientApproved(String requete,Connexion connexion) {
//        connexion.setCurrentstate(StateEnum.CLOSED);
//        return Commande.ready();
//    }
//
//    public static String writingMail(String requete, Connexion connexion) {
//        connexion.setCurrentstate(StateEnum.CLOSED);
//        return Commande.ready();
//    }
//
//    public static String readyToDeliver(String requete,Connexion connexion) {
//        connexion.setCurrentstate(StateEnum.CLOSED);
//        return Commande.ready();
//    }
//}
