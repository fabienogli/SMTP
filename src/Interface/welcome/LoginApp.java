package Interface.welcome;

import smtp.ClientSmtp;
import Interface.main.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApp extends Application {
    private Stage primaryStage;
    private AnchorPane rootLayout;
    private App mainApp;
    private ClientSmtp clientSmtp;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Boite au lettre");
        this.mainApp = new App();
        /**test
        initilazeClient();

        showMain();
         **/
        accessApp();
    }

    public void initilazeClient() {
        try {
            this.clientSmtp = new ClientSmtp();
            clientSmtp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Scene getMain() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(LoginApp.class.getResource(
                "welcomePage.fxml"
        ));
        AnchorPane main = (AnchorPane) loader.load();
        Controller controller = (Controller)loader.getController();
        controller.setLoginApp(this);
        return new Scene(main);
    }

    private void showMain() throws IOException {
        showScene(getMain());
    }

    public void accessApp() {
        this.primaryStage.close();
        this.mainApp.setClientSmtp(this.clientSmtp);
        this.mainApp.showMain();
    }

    private void showScene(Scene main) {
        this.primaryStage.setScene(main);
        this.primaryStage.show();
    }

    public void showLogin() {
        try {
            showScene(getLogin());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Scene getLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(LoginApp.class.getResource(
                "LoginPage.fxml"
        ));
        AnchorPane main = (AnchorPane) loader.load();
        Controller controller = (Controller)loader.getController();
        controller.setLoginApp(this);
        return new Scene(main);
    }

    public static void main(String[] args) {
        launch(args);
    }

    public ClientSmtp getClientSmtp() {
        return clientSmtp;
    }

    public void setClientSmtp(ClientSmtp clientSmtp) {
        this.clientSmtp = clientSmtp;
    }

    @Override
    public void stop() throws Exception {
        this.clientSmtp.quit();
        super.stop();
    }
}
