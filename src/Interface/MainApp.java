package Interface;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainApp {

    private ObservableList<Client> clientData = FXCollections.observableArrayList();

    public MainApp() {
        clientData.add(new Client("ma@mail.com", "kkk"));
    }

    public ObservableList<Client> getClientData() {
        return clientData;
    }
}
