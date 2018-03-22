package codes;

public enum SmtpCodes {
    //ServerSmtp
    READY("ready"),
    AUTHENTIFICATED("220 Bienvenue sur le Serveur STMP de Mark-Florian-Fabien"),
    OK("250 OK"),
    USER_UNKNOWN("550 Cet utilisateur n'existe pas"),
    MESSAGE("354 Start mail input; end with <CRLF>.<CRLF>"),
    LOGOUT("221 Deconnexion"),
    COMMAND_UNKNOWN("500 Syntax error, command unrecognized"),
    COMMAND_NOTIMPLEMENTED("502 Command not implemented"),
    WRONG_COMMAND("503 Bad sequence of commands"),
    //Client
    MAIL_FROM("MAIL FROM:"),
    RCPT_TO("RCPT TO:"),
    RESET("RSET"),
    EHLO("EHLO "),
    DATA("DATA"),
    QUIT("QUIT"),
    //Not from Stmp but for checking for password
    WRONG_PASSWORD("500 mauvais mot de passe");

    private String message;

    SmtpCodes(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }

    /**
     * from https://www.ietf.org/rfc/rfc2821.txt
     *
     *
     211 System status, or system help reply
     214 Help message
     (Information on how to use the receiver or the meaning of a
     particular non-standard command; this reply is useful only
     to the human user)
     220 <domain> Service ready
     221 <domain> Service closing transmission channel
     250 Requested mail action okay, completed
     251 User not local; will forward to <forward-path>
     252 Cannot VRFY user, but will accept message and attempt
     delivery
     354 Start mail input; end with <CRLF>.<CRLF>
     421 <domain> Service not available, closing transmission channel
     (This may be a reply to any command if the service knows it
     must shut down)
     450 Requested mail action not taken: mailbox unavailable
     (e.g., mailbox busy)
     451 Requested action aborted: local error in processing
     452 Requested action not taken: insufficient system storage
     500 Syntax error, command unrecognized
     (This may include errors such as command line too long)
     501 Syntax error in parameters or arguments
     502 Command not implemented (see section 4.2.4)
     503 Bad sequence of commands
     504 Command parameter not implemented
     550 Requested action not taken: mailbox unavailable
     (e.g., mailbox not found, no access, or command rejected
     for policy reasons)
     551 User not local; please try <forward-path>
     (See section 3.4)
     552 Requested mail action aborted: exceeded storage allocation
     553 Requested action not taken: mailbox name not allowed
     (e.g., mailbox syntax incorrect)
     554 Transaction failed  (Or, in the case of a connection-opening
     response, "No SMTP service here")
     */
}
