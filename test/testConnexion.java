import Client.Client;
import ServerSmtp.Utilisateur;
import codes.SmtpCodes;

import java.io.IOException;

public class testConnexion {
    public static void main(String[] args) {
        initializeClient();
    }

    public static void initializeClient() {
        try {
            Client client = new Client();
            Utilisateur utilisateur = new Utilisateur("foo@mail.com", "bar");
            String result = client.start();
            client.setUtilisateur(utilisateur);
            if (result.equals(SmtpCodes.READY.toString())) {
                boolean auth = client.authentification();
                System.out.println("Le client est arrivé à se connecter ! " + auth);
            }
            client.quit();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
