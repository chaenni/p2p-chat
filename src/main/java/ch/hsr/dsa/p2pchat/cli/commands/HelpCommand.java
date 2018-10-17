package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HelpCommand extends Command {

    private final Collection<Command> commands;

    public HelpCommand(Collection<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        systemMessage.accept("\nCommands: \n" + commands.stream().map(c -> "/" + c.getName() + " " + c.getUsage()).collect(Collectors.joining("\n")));
    }

    @Override
    protected int getNumberOfArguments() {
        return 0;
    }

    @Override
    protected String getUsage() {
        return "";
    }
}
