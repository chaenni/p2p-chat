package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import java.util.function.Consumer;

public class GetGroupInformationCommand extends Command {

    @Override
    public String getName() {
        return "group";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        var group = handler.getGroupInformation(args[0]);
        systemMessage.accept(group.toString());
    }

    @Override
    protected int getNumberOfArguments() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "groupname";
    }
}
