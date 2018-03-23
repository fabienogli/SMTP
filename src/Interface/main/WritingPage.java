package Interface.main;

import smtp.ClientSmtp;
import common.Message;
import common.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
        this.senderTextField.setText(this.app.getClientSmtp().getUtilisateur().getEmail());
        this.senderTextField.setDisable(true);
    }

    @FXML
    void attach(ActionEvent event) {

    }

    @FXML
    void send(ActionEvent event) {
        ClientSmtp clientSmtp = this.app.getClientSmtp();
        Message message = new Message(clientSmtp.getUtilisateur());
        message.setSujet(this.subjectTextField.getText());
        message.setCorps(this.corpsTextArea.getText());
        String recipients = this.recipientTextField.getText();
        for (String recipient: recipients.split(";")) {
            message.addDestinataire(new Utilisateur(recipient));
        }
        clientSmtp.sendMail(message);
        this.stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
