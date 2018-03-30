package Interface.main;

import Interface.Client;
import common.Message;
import common.Utilisateur;
import database.Dns;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import smtp.ClientSmtp;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static database.BdConnexion.writeToFile;

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

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
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

        ArrayList<String> error = validateRecipients(recipients);
        if (error.size() == 0) {
            for (String recipient : recipients.split(";")) {
                if (!recipient.split("@")[1].equals(client.getUtilisateur().domainName())) {
                    message.getDestinatairesDistants().add(new Utilisateur(recipient));
                } else message.addDestinataire(new Utilisateur(recipient));

            }
            if (recipients.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Aucun destinataire");
                alert.setContentText("Veuillez saisir au moins un destinataire !");
                alert.showAndWait();
            } else {
                ArrayList<Utilisateur> result = sendToOtherDomain(message);
                if (!message.getDestinataires().isEmpty() && client.sendMail(message).contains("1")) {
                        String[] reponse = client.sendMail(message).split(";");
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i <= reponse.length - 1; i++) {
                            sb.append(reponse[i]).append("\n");
                        }
                        if (result.size() != 0) {
                            for (int i = 0; i <= result.size() - 1; i++) {
                                sb.append(result.get(i).getEmail()).append("\n");
                            }
                        }
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Destinataires inexistants");
                        alert.setHeaderText("Voir ci-dessous la liste :");
                        alert.setContentText(sb.toString());
                        alert.showAndWait();

                } else if (result.size() != 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i <= result.size()-1; i++) {
                        sb.append(result.get(i).getEmail()).append("\n");
                    }
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Destinataires distants non atteints");
                    alert.setHeaderText("Voir ci-dessous la liste :");
                    alert.setContentText(sb.toString());
                    alert.showAndWait();
                    this.stage.close();
                } else {
                    writeToFile(message.getAuteur(), message.toString()+"\n", "sent");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Message envoyé");
                    alert.setContentText("Le message a été envoyé avec succès !");
                    alert.showAndWait();
                    this.stage.close();
                }
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String anError : error) {
                sb.append(anError).append("\n");
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Mauvais format d'email");
            alert.setContentText("Veuillez saisir ce(s) adresse(s) !" + "\n" + sb.toString());
            alert.showAndWait();
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
        for (int i = 0; i < destinataires.size(); i++) {
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

    public boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public ArrayList validateRecipients(String recipients) {
        ArrayList list = new ArrayList<String>();
        for (String recipient : recipients.split(";")) {
            if (!validate(recipient)) {
                list.add(recipient);
            }
        }
        return list;
    }

    private ArrayList<Utilisateur> sendToOtherDomain(Message message) {
        ArrayList<Utilisateur> destinatairesErronees = new ArrayList<>();
        if (!message.getDestinatairesDistants().isEmpty()) {
            for (Utilisateur utilisateur : message.getDestinatairesDistants()) {
                try {
                    System.out.println("envoi à un autre serveur");
                    ClientSmtp smtp = new ClientSmtp(java.net.InetAddress.getByName(Dns.getHost(utilisateur.domainName())), 2026);
//                  ClientSmtp smtp = new ClientSmtp(java.net.InetAddress.getByName("localhost"),2025);
                    try {
                        smtp.start();
                        smtp.authentification();

                    }catch (ConnectException e){
                        destinatairesErronees.add(utilisateur);
                        return destinatairesErronees;
                    }

                    if (smtp.sendMailDistant(message, utilisateur).contains("1")) {
                        destinatairesErronees.add(utilisateur);
                    }
                    smtp.quit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return destinatairesErronees;
    }
}
