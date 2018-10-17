package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import java.io.IOException;
import java.util.function.Consumer;

public class CreateGroupCommand extends Command {

    @Override
    public String getName() {
        return "createGroup";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        if(!handler.createGroup(args[0])) {
            systemMessage.accept("Could not create group " + args[0]);
            return;
        }
        systemMessage.accept("Group \"" + args[0] + "\" created. ");
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
