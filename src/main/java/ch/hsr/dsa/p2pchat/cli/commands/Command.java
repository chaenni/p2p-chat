package ch.hsr.dsa.p2pchat.cli.commands;

import java.util.Map;

public interface Command {
    String getName();
    void run(String commandInput);
}
