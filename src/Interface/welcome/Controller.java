package Interface.welcome;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
            this.connexionButton.setDisable(false);

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
        System.out.println(this.passwordTextField.getText() + this.mailTextField.getText());
        if (this.passwordTextField.getText().length() > 1 && this.mailTextField.getText().length() > 1) {
            System.out.println("bouton visible normalement");
            this.connexionButton.setDisable(false);
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        this.loginApp.showLogin();
    }

    @FXML
    private void login(ActionEvent event) {
        //@TODO initialisation du client, si le client a les bons identifiants check est vrai
        boolean check = true;
        if (check) {
            this.loginApp.accessApp();
        }
    }

}
