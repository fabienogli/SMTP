package Interface.main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WritingPage {

    @FXML
    private TextField senderTextField;

    @FXML
    private TextField recipientTextField;

    @FXML
    private TextField subjectTextField;

    @FXML
    private TextArea corpsTextArea;

    private App app;

    public void setApp(App app) {
        this.app = app;
    }

    @FXML
    void attach(ActionEvent event) {

    }

    @FXML
    void save(ActionEvent event) {

    }

    @FXML
    void send(ActionEvent event) {

    }

    public TextField getSenderTextField() {
        return senderTextField;
    }

    public void setSenderTextField(TextField senderTextField) {
        this.senderTextField = senderTextField;
    }

    public TextField getRecipientTextField() {
        return recipientTextField;
    }

    public void setRecipientTextField(TextField recipientTextField) {
        this.recipientTextField = recipientTextField;
    }

    public TextField getSubjectTextField() {
        return subjectTextField;
    }

    public void setSubjectTextField(TextField subjectTextField) {
        this.subjectTextField = subjectTextField;
    }

    public TextArea getCorpsTextArea() {
        return corpsTextArea;
    }

    public void setCorpsTextArea(TextArea corpsTextArea) {
        this.corpsTextArea = corpsTextArea;
    }
}
