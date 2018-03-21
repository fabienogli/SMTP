/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

public class Utilisateur {

    private String nom;
    private String email;
    private String mdp;

    public Utilisateur() {
        this.nom = "unknown";
        this.email = "unknown";
        this.mdp = "unknown";
    }

    public Utilisateur(String email) {
        this.email = email;
    }

    public Utilisateur(String email, String mdp) {
        this.email = email;
        this.mdp = mdp;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getNom())
                .append(" ")
                .append(this.getEmail());
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this.getEmail().equals(((Utilisateur) obj).getEmail()) && this.getMdp().equals(((Utilisateur) obj).getMdp());
    }
}
