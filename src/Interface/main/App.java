package Interface.main;

import smtp.ClientSmtp;
import Interface.main.base.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class App {

    private Stage primaryStage;
    private ClientSmtp clientSmtp;

    public App() {
        this(new Stage());
    }

    private App(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Boite au lettre");
    }

    public void showMain() {
        try {
            BorderPane rootLayout = initRootLayout(this.primaryStage);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(
                    "Main.fxml"
            ));
            AnchorPane main = (AnchorPane) loader.load();
            Main controller = loader.getController();
            controller.setApp(this);
            rootLayout.setCenter(main);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BorderPane initRootLayout(Stage primaryStage) throws IOException {
        BorderPane rootLayout;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(App.class.getResource(
                "base/base.fxml"
        ));
        rootLayout = (BorderPane) loader.load();
        Controller controller = loader.getController();
        controller.setApp(this);
        controller.setStage(primaryStage);
        Scene scene = new Scene(rootLayout);
        primaryStage.setScene(scene);
        primaryStage.show();
        return rootLayout;
    }

    public void writeMessage() {
        Stage messageStage = new Stage();
        messageStage.setTitle("Nouveau Message");
        try {
            BorderPane rootLayout = initRootLayout(messageStage);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource(
                    "WritingPage.fxml"
            ));
            AnchorPane main = (AnchorPane) loader.load();
            WritingPage controller = loader.getController();
            controller.setApp(this);
            controller.setStage(messageStage);
            rootLayout.setCenter(main);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ClientSmtp getClientSmtp() {
        return clientSmtp;
    }

    public void setClientSmtp(ClientSmtp clientSmtp) {
        this.clientSmtp = clientSmtp;
    }
}
