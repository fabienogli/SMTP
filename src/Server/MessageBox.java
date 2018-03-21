/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import java.util.ArrayList;
import java.util.List;

public class MessageBox {

    private List<Message> Messages;
    private boolean blocked = false;
    private Utilisateur utilisateur;

    //Constructeur
    public MessageBox(Utilisateur utilisateur, ArrayList<Message> Messages) {
        this.utilisateur = utilisateur;
        this.Messages = Messages;
    }

    public MessageBox(Utilisateur utilisateur) {
        this();
        this.utilisateur = utilisateur;
    }

    public MessageBox() {
        this.Messages = new ArrayList<>();
    }

    //accesseurs

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public List<Message> getMessages() {
        return Messages;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    //methodes
    public int getNumberMessages() {
        return this.Messages.size();
    }

    public String buildList() {
        String list = "";
        int i = 0;
        for (Message Message : Messages) {
            i++;
            if(!Message.isDeleteMark())
                list += "\n" + i + " " + Message.size();
        }
        return list;
    }

    public Message getMessage(int i){
        return this.Messages.get(i);
    }

    public int getStorageSize() {
        int length = 0;
        for (Message Message : Messages) {
            length += Message.size();
        }
        return length;
    }

    public void resetDelete() {
        this.Messages.forEach(Message -> Message.setDeleteMark(false));
    }

    public void deleteMessage(){
        this.Messages.removeIf((Message) -> Message.isDeleteMark());
    }

    public void setMessages(List<Message> Messages) {
        this.Messages = Messages;
    }

    public int getBytes(){
        int size = 0;
        for(Message message:Messages){
            size+= message.size();
        }
        return size;
    }
}
