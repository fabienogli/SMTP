package common;

public enum HeadersEnum {
    FROM("From"),
    DATE("Date"),
    SUJET("Subject"),
    TO("To"),
    ID("Message-ID"),
    CRLF("\n.\n");

    private String string;

    HeadersEnum(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string + ": ";
    }

    public String getString() {
        return string;
    }
}
