import Interface.Client;
import database.BdConnexion;
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
//        testPop3();
//        testSmtp();
        testSuperClient();
//        testBdConnexion();
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

    public static void testSuperClient() {
        Utilisateur utilisateur = new Utilisateur("foo@mail.com", "bar");
        Client superClient = null;
        try {
            superClient = new Client();
            superClient.start();
            superClient.setUtilisateur(utilisateur);
            superClient.authentification();
            superClient.quit();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            superClient.quit();
        }
    }

    public static void testBdConnexion() {
        String rawMail = "From: john <john@mail.com>\n" +
                "To: foo <foo@mail.com>\n" +
                "Sujet: Message 2\n" +
                "Date: Fri, 21 Nov 1997 09:55:06 -0600\n" +
                "Message-ID: <2@local.machine.example>\n" +
                "\n" +
                "Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim.\n" +
                "Aliquam lorem ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet.\n" +
                "Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies nisi.";
        System.out.println(BdConnexion.parseMail(rawMail));
    }

}
