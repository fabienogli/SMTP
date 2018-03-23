import pop3.ClientPop3;
import smtp.ClientSmtp;
import common.Message;
import common.Utilisateur;
import codes.SmtpCodes;

import java.io.IOException;

public class testConnexion {
    public static void main(String[] args) {
//        initializeClient();
//        testMail();
        testPop3();
//        testSmtp();
    }

    private static void initializeClient() {
        try {
            ClientSmtp clientSmtp = new ClientSmtp();
            ClientPop3 clientPop3 = new ClientPop3();
            Utilisateur utilisateur = new Utilisateur("foo@mail.com", "bar");
            utilisateur.setName();
            String result = clientSmtp.start();
            clientPop3.start();
            clientPop3.setUtilisateur(utilisateur);
            clientSmtp.setUtilisateur(utilisateur);
            if (!result.equals(SmtpCodes.READY.toString())) {
                System.out.println("il y a un erreur dans le serveur");
            }
            clientSmtp.authentification();
            clientPop3.authentificationApop();
            clientPop3.list();
            clientPop3.retr(1);
            System.out.println("Le clientSmtp est arrivé à se connecter ! " + clientSmtp.isAuthentified());
            clientSmtp.sendMail(testMail(clientSmtp.getUtilisateur()));
            clientSmtp.sendMail(testMail(clientSmtp.getUtilisateur()));
            clientSmtp.quit();
            clientPop3.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testSmtp() {
        ClientSmtp clientSmtp = null;
        String result = "";
        try {
            clientSmtp = new ClientSmtp();
            Utilisateur utilisateur = new Utilisateur("foo@mail.com", "bar");
            clientSmtp.setUtilisateur(utilisateur);
            result = clientSmtp.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!result.equals(SmtpCodes.READY.toString())) {
            System.out.println("il y a un erreur dans le serveur");
        }
        clientSmtp.authentification();
        clientSmtp.sendMail(testMail(clientSmtp.getUtilisateur()));
        clientSmtp.sendMail(testMail(clientSmtp.getUtilisateur()));
        clientSmtp.quit();

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

    private static void testPop3() {
        try {
            ClientPop3 clientPop3 = new ClientPop3();
            Utilisateur utilisateur = new Utilisateur("foo@mail.com", "bar");

            utilisateur.setName();
            clientPop3.start();
            clientPop3.setUtilisateur(utilisateur);
            clientPop3.authentificationApop();
            clientPop3.list();
            clientPop3.retr(1);
            clientPop3.logout();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
