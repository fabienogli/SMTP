package Interface.main;

import Interface.Client;
import common.Message;
import common.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class WritingPage {

    @FXML
    private TextField senderTextField;

    @FXML
    private TextField recipientTextField;

    @FXML
    private TextField subjectTextField;

    @FXML
    private TextArea corpsTextArea;

    @FXML
    private Button sendButton;

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

        for (String recipient : recipients.split(";")) {
            message.addDestinataire(new Utilisateur(recipient));
        }
        if (recipients.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Aucun destinataire");
            alert.setContentText("Veuillez saisir au moins un destinataire !");
            alert.showAndWait();
        } else if (client.sendMail(message).contains("1")) {
            String[] reponse = client.sendMail(message).split(";");
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= reponse.length - 1; i++) {
                sb.append(reponse[i]).append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Adresse erronée");
            alert.setHeaderText("Voir ci-dessous la liste :");
            alert.setContentText(sb.toString());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message envoyé");
            alert.setContentText("Le message a été envoyé avec succès !");
            alert.showAndWait();
            this.stage.close();
        }

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMail(Message message) {
        this.sendButton.setVisible(false);
        this.senderTextField.setText(message.getAuteur().getNom() + " <" + message.getAuteur().getEmail() + ">");
        this.senderTextField.setEditable(false);
        List<Utilisateur> destinataires = message.getDestinataires();
        String tmp = "";
        for (int i = 0; i < destinataires.size(); i ++ ) {
            tmp += destinataires.get(i).getNom() + " <" + destinataires.get(i).getEmail() + ">";
            if (i != destinataires.size() - 1) {
                tmp += ";";
            }
        }
        this.recipientTextField.setText(tmp);
        this.recipientTextField.setEditable(false);
        this.subjectTextField.setText(message.getSujet());
        this.subjectTextField.setEditable(false);
        this.corpsTextArea.setText(message.getCorps());
        this.corpsTextArea.setEditable(false);
    }
}
