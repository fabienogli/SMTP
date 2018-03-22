/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package ServerSmtp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Message {

    private String id;

    private List<Utilisateur> destinataires;
    private Utilisateur auteur;
    private Date date;
    private String sujet;
    private String corps;
    private Map<String, String> optionalHeaders;
    private boolean deleteMark = false;

    public Message(String id) {
        this.id = id;
        this.destinataires = new ArrayList<Utilisateur>();
        this.date = new Date();
        this.sujet = "";
        this.corps = "";
    }

    public Message(String id, Utilisateur destinataire, Utilisateur auteur, Date date) {
        this(id);
        this.destinataires.add(destinataire);
        this.auteur = auteur;
        this.date = date;
    }

    public Message(String id, Utilisateur destinataire, Utilisateur auteur, Date date, String sujet, String corps) {
        this(id, destinataire, auteur, date);
        this.sujet = sujet;
        this.corps = corps;
    }

    public Message() {
        this(generateDateStamp(), new Utilisateur("unknown"), new Utilisateur("unknown"), new Date(), "unknown", "unknown");
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
                .append("From: ")
                .append("<")
                .append(this.getAuteur().getEmail())
                .append(">\r\nTo: ");
        for (int i = 0; i < this.getDestinataires().size(); i++) {
            generateMessage
                    .append("<")
                    .append(this.destinataires.get(i).getEmail())
                    .append(">");
            if (i != this.destinataires.size() - 1) {
                generateMessage.append(";");
            }
        }
        generateMessage
                .append("\r\nSubject: ")
                .append(this.getSujet())
                .append("\r\nDate: ")
                .append(this.getDate().toString())
                .append("\r\nMessage-ID: <")
                .append(this.getId())
                .append("@local.machine.example>\r\n\n")
                .append(this.getCorps())
                .append("\r\n.\n");

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
        return "<" + stamp + "@smtp.fabienMarkFlorian.fr" + ">";

    }
}
