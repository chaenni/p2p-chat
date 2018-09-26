package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;

public interface Command {
    String getName();
    void run(ChatHandler handler, String commandInput);
}
