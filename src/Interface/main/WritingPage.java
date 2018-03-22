package Interface.main;

import Client.Client;
import ServerSmtp.Message;
import ServerSmtp.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private Stage stage;

    public void setApp(App app) {
        this.app = app;
        this.senderTextField.setText(this.app.getClient().getUtilisateur().getEmail());
        this.senderTextField.setDisable(true);
    }

    @FXML
    void attach(ActionEvent event) {

    }

    @FXML
    void send(ActionEvent event) {
        Client client = this.app.getClient();
        Message message = new Message(client.getUtilisateur());
        message.setSujet(this.subjectTextField.getText());
        message.setCorps(this.corpsTextArea.getText());
        String recipients = this.recipientTextField.getText();
        for (String recipient: recipients.split(";")) {
            message.addDestinataire(new Utilisateur(recipient));
        }
        client.sendMail(message);
        this.stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
