package ch.hsr.dsa.p2pchat.cli.colorprinter;

public class ColorPrinter {
    private static final String ANSI_RESET = "\u001B[0m";

    public static void printlnInColor(AnsiColor color, String message) {
        System.out.println(color.getValue() + message + ANSI_RESET);
    }

    public static void printInColor(AnsiColor color, String message) {
        System.out.print(color.getValue() + message + ANSI_RESET);
    }
}
