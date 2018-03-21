import Client.Client;
import Server.Server;

import java.io.IOException;

public class testConnexion {
    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.start();
            client.authentification();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
