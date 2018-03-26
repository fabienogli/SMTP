/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package common;

import smtp.server.ServerSMTP;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Message {

    private String id;

    private List<Utilisateur> destinataires;
    private Utilisateur auteur;
    private String date;
    private String sujet;
    private String corps;
    private Map<String, String> optionalHeaders;
    private boolean deleteMark = false;

    public Message(String id) {
        this.id = id;
        this.destinataires = new ArrayList<Utilisateur>();
        /*DateFormat format = new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss Z", Locale.US);
        try {
            Date today = Calendar.getInstance().getTime();
           // this.date = format.parse(format.format(new Date()));
            this.date = format.parse(today.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        this.date = getDate();
        this.sujet = "";
        this.corps = "";
    }

    public Message(String id, Utilisateur destinataire, Utilisateur auteur, String date) {
        this(id);
        this.destinataires.add(destinataire);
        this.auteur = auteur;
        this.date = date;
    }

    public Message(String id, Utilisateur destinataire, Utilisateur auteur, String date, String sujet, String corps) {
        this(id, destinataire, auteur, date);
        this.sujet = sujet;
        this.corps = corps;
    }

    public Message() {
        this(generateDateStamp());
    }

    public Message(Utilisateur auteur) {
        this(generateDateStamp());
        this.auteur = auteur;
    }

    public List<Utilisateur> getDestinataires() {
        return destinataires;
    }

    public void setDestinataires(List<Utilisateur> destinataires) {
        this.destinataires = destinataires;
    }

    public void addDestinataire(Utilisateur destinataire) {
        this.destinataires.add(destinataire);
    }

    public Utilisateur getAuteur() {
        return auteur;
    }

    public void setAuteur(Utilisateur auteur) {
        this.auteur = auteur;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getCorps() {
        return corps;
    }

    public void setCorps(String corps) {
        this.corps = corps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        StringBuilder generateMessage = new StringBuilder();

        generateMessage
                .append(HeadersEnum.FROM.toString())
                .append(this.getAuteur().getNom())
                .append(" <")
                .append(this.getAuteur().getEmail())
                .append(">\r\n")
                .append(HeadersEnum.TO.toString());
        for (int i = 0; i < this.getDestinataires().size(); i++) {
            generateMessage
                    .append(this.destinataires.get(i).getNom())
                    .append(" <")
                    .append(this.destinataires.get(i).getEmail())
                    .append(">");
            if (i != this.destinataires.size() - 1) {
                generateMessage.append(";");
            }
        }
        generateMessage
                .append("\r\n")
                .append(HeadersEnum.SUJET.toString())
                .append(this.getSujet())
                .append("\r\n")
                .append(HeadersEnum.DATE.toString())
                .append(this.getDate())
                .append("\r\n")
                .append(HeadersEnum.ID.toString())
                .append("<")
                .append(this.getId())
                .append("@")
                .append(ServerSMTP.name)
                .append(">\r\n\n")
                .append(this.getCorps())
                .append("\r\n.");

        return generateMessage.toString();
    }

    public boolean isDeleteMark() {
        return deleteMark;
    }

    public void setDeleteMark(boolean deleteMark) {
        this.deleteMark = deleteMark;
    }

    public int size() {
        return this.toString().getBytes().length;
    }

    public void addOptionalHeader(String key, String value) {
        this.optionalHeaders.put(key, value);
    }

    private static String generateDateStamp() {
        StringBuilder dateStamp = new StringBuilder();
        String stamp = "";
        Date date = new Date();
        String uniqueID = UUID.randomUUID().toString();
        dateStamp.append(uniqueID).append(date);
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(dateStamp.toString().getBytes(), 0, dateStamp.toString().length());
            stamp = new BigInteger(1, m.digest()).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return stamp;

    }
}
