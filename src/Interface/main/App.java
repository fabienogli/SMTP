package Interface.main;

import Interface.Client;
import Interface.welcome.LoginApp;
import common.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Interface.main.base.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;

public class App {

    private Stage primaryStage;
    private Client client;
    private final ObservableList<ModelMail> mails = FXCollections.observableArrayList();
    private LoginApp loginApp;

    public App() {
        this(new Stage());
    }

    private App(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Boîtes au lettres");
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
            controller.fulfillColumn();
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        System.out.println(client);
        this.client = client;
        this.mails.addAll(this.client.getModelMails());
    }


    public ObservableList<ModelMail> getMails() {
        return mails;
    }

    public void logout() {
        this.client.quit();
        this.primaryStage.close();
        this.loginApp.initilazeClient();
        try {
            this.loginApp.showMain();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.mails.clear();
        this.loginApp.getPrimaryStage().show();
    }

    public void setLoginApp(LoginApp loginApp) {
        this.loginApp = loginApp;
    }

    public void showMail(ModelMail rowData) {
        HashMap<ModelMail, Message> link = this.client.getLinkMails();
        Message mail = link.get(rowData);

    }
}
