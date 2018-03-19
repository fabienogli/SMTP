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

    
    public static String ehlo(String requete, Connexion connexion) {
        return requete;
    }

    public static String quit(Connexion connexion) {
    }
}