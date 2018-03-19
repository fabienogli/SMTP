/*
 * Copyright (c) 2018. Mark KPAMY -Fabien OGLI - Florian LOMBARDO
 */

package Server;

import java.util.ArrayList;
import java.util.List;

public class MessageBox {

    private String name;
    private String password;
    private String address;

    private List<Message> Messages;
    private boolean blocked = false;

    //Constructeur
    public MessageBox(String name, String password, String address, ArrayList<Message> Messages) {
        this.name = name;
        this.password = password;
        this.Messages = Messages;
        this.address = address;
    }

    public MessageBox(String name, String password, String address) {
        this.name = name;
        this.password = password;
        this.Messages = Messages;
        this.address = address;
        this.Messages = new ArrayList<>();
    }

    public MessageBox() {

    }

    //accesseurs
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public List<Message> getMessages() {
        return Messages;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    //methodes
    public int getNumberMessages() {
        return this.Messages.size();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
