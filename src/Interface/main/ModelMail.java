package Interface.main;

import common.Message;
import javafx.beans.property.SimpleStringProperty;

public class ModelMail {
    private final SimpleStringProperty email = new SimpleStringProperty("");
    private final SimpleStringProperty subject = new SimpleStringProperty("");

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    private final SimpleStringProperty date = new SimpleStringProperty("");

    public ModelMail() {
        this("", "","");
    }

    public ModelMail(String email, String subject,String date) {
        setEmail(email);
        setSubject(subject);
        setDate(date);
    }

    public ModelMail(Message message) {
        setEmail(message.getAuteur().getEmail());
        setSubject(message.getSujet());
        setDate(message.getDate());
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getSubject() {
        return subject.get();
    }

    public SimpleStringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    @Override
    public String toString() {
        return "\n{ModelMail:" +
                " mail = " + this.getEmail() +
                " subject = " +this.getSubject() +
                "}\n";
    }
}
