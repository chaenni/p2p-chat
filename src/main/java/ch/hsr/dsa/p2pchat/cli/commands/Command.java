package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;

public abstract class Command {
    public abstract String getName();
    public final void run(ChatHandler handler, String commandInput) {
        try {
            String[] args = CommandHelper.getArguments(getNumberOfArguments(), commandInput);
            onSuccess(handler, args);
        } catch (IllegalArgumentException e) {
            printUsage();
        }
    }
    protected abstract void onSuccess(ChatHandler handler, String[] args);
    protected abstract int getNumberOfArguments();
    public abstract void printUsage();
}
