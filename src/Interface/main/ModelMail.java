package Interface.main;

import common.Message;
import javafx.beans.property.SimpleStringProperty;

public class ModelMail {
    private final SimpleStringProperty email = new SimpleStringProperty("");
    private final SimpleStringProperty subject = new SimpleStringProperty("");

    public ModelMail() {
        this("", "");
    }

    public ModelMail(String email, String subject) {
        setEmail(email);
        setSubject(subject);
    }

    public ModelMail(Message message) {
        setEmail(message.getAuteur().getEmail());
        setSubject(message.getSujet());
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
