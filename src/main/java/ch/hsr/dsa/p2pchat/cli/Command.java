package ch.hsr.dsa.p2pchat.cli;

public interface Command {
    boolean matches(String message);
    void run();
}
