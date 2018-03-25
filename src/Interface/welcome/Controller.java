package Interface.welcome;

import Interface.Client;
import Interface.WrongLoginException;
import javafx.scene.control.*;
import smtp.ClientSmtp;
import common.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {

    @FXML
    private Button connexionButton;

    @FXML
    private TextField mailTextField;

    @FXML
    private PasswordField passwordTextField;

    private LoginApp loginApp;

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public void setLoginApp(LoginApp loginApp) {
        this.loginApp = loginApp;
    }

    public void initialize() {
        if (this.mailTextField != null) {
            this.connexionButton.setDisable(true);

            this.mailTextField.textProperty().addListener(
                    ((observable, oldValue, newValue) ->{
                        checkForLoginButton();
                        if (!validate(this.mailTextField.getText())) {
                            this.mailTextField.setStyle("-fx-border-color: red ;");
                        } else {
                            this.mailTextField.setStyle("-fx-border-color: lawngreen ;");
                        }
                    })
            );
            this.passwordTextField.textProperty().addListener(
                    ((observable, oldValue, newValue) ->{
                        checkForLoginButton();
                    })
            );
        }
    }

    private void checkForLoginButton() {
        if (this.passwordTextField.getText().length() > 1 && this.mailTextField.getText().length() > 1 && validate(this.mailTextField.getText())) {
            this.connexionButton.setDisable(false);
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        this.loginApp.showLogin();
    }

    @FXML
    private void login(ActionEvent event) {
        Client client = this.loginApp.getClient();
        Utilisateur utilisateur = new Utilisateur(this.mailTextField.getText(), this.passwordTextField.getText());
        client.setUtilisateur(utilisateur);
        try {
            client.authentification();
            client.getReceivedMessages();
        } catch (WrongLoginException e) {
            new Alert(Alert.AlertType.ERROR, "Mauvais identifiants", ButtonType.FINISH);
            e.printStackTrace();
        }
        this.loginApp.setClient(client);
        if (client.isAuthentified()) {
            this.loginApp.accessApp();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Identifiants erronés");
            alert.setContentText("Veuillez resaisir vos identifiants !");
            alert.showAndWait();
        }
    }

    public boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
