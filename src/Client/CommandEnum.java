package Client;

public enum CommandEnum {
    READY("ready"),
    AUTHENTIFICATED("220 Bienvenue sur le Serveur STMP de Mark-Florian-Fabien"),
    OK("250 OK"),
    UNKNOWN("550 unknown user"),
    MESSAGE("354 Début des entrées du message"),
    LOGOUT("221 Deconnexion");

    private String message;

    CommandEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return this.getMessage();
    }
}
