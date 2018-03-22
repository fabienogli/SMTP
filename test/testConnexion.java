import Client.Client;
import ServerSmtp.Message;
import ServerSmtp.Utilisateur;
import codes.SmtpCodes;

import java.io.IOException;

public class testConnexion {
    public static void main(String[] args) {
        initializeClient();
//        testMail();
    }

    private static void initializeClient() {
        try {
            Client client = new Client();
            Utilisateur utilisateur = new Utilisateur("foo@mail.com", "bar");
            String result = client.start();
            client.setUtilisateur(utilisateur);
            if (!result.equals(SmtpCodes.READY.toString())) {
                System.out.println("il y a un erreur dans le serveur");
            }
            client.authentification();
            System.out.println("Le client est arrivé à se connecter ! " + client.isAuthentified());
            client.sendMail(testMail(client.getUtilisateur()));
            client.quit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Message testMail(Utilisateur auteur) {
        Utilisateur john = new Utilisateur("john@mail.com");
        Utilisateur root = new Utilisateur("root@mail.com");
        Message message = new Message(auteur);
        message.setSujet("Malcolm X");
        message.setCorps("A man who stands for nothing...\n\nwill fall for everything");
        message.addDestinataire(john);
        message.addDestinataire(root);
        return message;
    }
}
