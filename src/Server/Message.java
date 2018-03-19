/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import java.util.Date;
import java.util.Map;

public class Message {

    private String id;

    private Utilisateur destinataire;
    private Utilisateur auteur;
    private Date date;
    private String sujet;
    private String corps;
    private Map<String,String> optionalHeaders;
    private boolean deleteMark = false;

    public Message(String id) {
        this.id = id;
    }

    public Message(String id, Utilisateur destinataire, Utilisateur auteur, Date date, String sujet, String corps) {
        this.id = id;
        this.destinataire = destinataire;
        this.auteur = auteur;
        this.date = date;
        this.sujet = sujet;
        this.corps = corps;
    }

    public Message() {
        Utilisateur johnDoe = new Utilisateur("unknown", "unknown");
        this.id = "unknown";
        this.destinataire = johnDoe;
        this.auteur = johnDoe;
        this.date = new Date();
        this.sujet = "unknown";
        this.corps = "unknown";
    }

    public Utilisateur getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Utilisateur destinataire) {
        this.destinataire = destinataire;
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
            generateMessage.append("From: ")
                    .append(this.getAuteur().toString())
                    .append("\r\nTo: ")
                    .append(this.getDestinataire().toString())
                    .append("\r\nSubject: ")
                    .append(this.getSujet())
                    .append("\r\nDate : ")
                    .append(this.getDate().toString())
                    .append("\r\nMessage-ID <")
                    .append(this.getId())
                    .append("@local.machine.example>\r\n")
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
}
