/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Message {

    private String id;

    private List<Utilisateur> destinataires;
    private Utilisateur auteur;
    private Date date;
    private String sujet;
    private String corps;
    private Map<String,String> optionalHeaders;
    private boolean deleteMark = false;

    public Message(String id) {
        this.id = id;
        this.destinataires = new ArrayList<Utilisateur>();
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
        this("unknown", new Utilisateur("unknown"), new Utilisateur("unknown"), new Date(), "unknown", "unknown");
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
            generateMessage.append("From: <")
                    .append(this.getAuteur().toString())
                    .append(">\r\nTo: <")
                    .append(this.getDestinataires().toString())
                    .append(">\r\nSubject: ")
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
