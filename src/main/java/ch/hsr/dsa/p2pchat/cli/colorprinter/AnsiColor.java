package ch.hsr.dsa.p2pchat.cli.colorprinter;

public enum AnsiColor {
    BLACK("\u001B[30m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    PURPLE("\u001B[35m"),
    CYAN("\u001B[36m"),
    WHITE("\u001B[37m");

    private String value;

    AnsiColor(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
