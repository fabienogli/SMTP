package ServerSmtp;

public enum HeadersEnum {
    FROM("From: "),
    DATE("Date: "),
    SUJET("Sujet: "),
    TO("To: "),
    CRLF("\n.");

    private String string;

    HeadersEnum(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
