package codes;

public enum Pop3Codes {
    //Command ClientPop3
    APOP("APOP "),
    RETR("RETR "),
    LIST("LIST"),
    STAT("STAT"),
    LOGOUT("QUIT"),
    USER("USER "),
    PASS("PASS "),
    //Command Server
    READY("Serveur POP3 de Mark-Fabien-Florian Ready "),
    QUIT("+OK Serveur POP3 de Mark-Florian-Fabien signing off"),
    MAILBOX_INVALID("-ERR Boite aux lettres invalide"),
    MAILBOX_VALID("+OK Boite aux lettres valide"),
    PASSWORD_INVALID("-ERR Mot de passe invalide"),
    PASSWORD_VALID("+OK Authentification reussie"),
    APOP_INVALID("-ERR APOP invalide"),
    APOP_VALID("+OK authentification APOP réussie"),
    SUCCESS("+OK ");

    private String message;

    Pop3Codes(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
