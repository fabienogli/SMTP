package Interface.welcome;

import Interface.Client;
import smtp.ClientSmtp;
import common.Utilisateur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private Button connexionButton;

    @FXML
    private TextField mailTextField;

    @FXML
    private PasswordField passwordTextField;

    private LoginApp loginApp;

    public void setLoginApp(LoginApp loginApp) {
        this.loginApp = loginApp;
    }

    public void initialize() {
        if (this.mailTextField != null) {
            this.connexionButton.setDisable(true);

            this.mailTextField.textProperty().addListener(
                    ((observable, oldValue, newValue) ->{
                        checkForLoginButton();
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
        if (this.passwordTextField.getText().length() > 1 && this.mailTextField.getText().length() > 1) {
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
        this.loginApp.setClient(client);
        if (client.authentification()) {
            this.loginApp.accessApp();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Identifiants erronés");
            alert.setContentText("Veuillez resaisir vos identifiants !");
            alert.showAndWait();
        }
    }
}
