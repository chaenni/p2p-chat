package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

public abstract class Command {
    public abstract String getName();
    public final void run(Consumer<String> systemMessage, ChatHandler handler, String commandInput) {
        try {
            String[] args = CommandHelper.getArguments(getNumberOfArguments(), commandInput);
            onSuccess(handler,systemMessage, args);
        } catch (IllegalArgumentException e) {
            printUsage(systemMessage);
        } catch (Exception e) {
            StringWriter str = new StringWriter();
            e.printStackTrace(new PrintWriter(str));
            systemMessage.accept("Could not do task. issue: " + str.toString());
        }
    }
    protected abstract void onSuccess(ChatHandler handler, Consumer<String> systemMessage,  String[] args);
    protected abstract int getNumberOfArguments();
    protected abstract String getUsage();
    public void printUsage(Consumer<String> systemMessage) {
        systemMessage.accept("Command usage: /" + getName() + " " + getUsage() );
    }
}
