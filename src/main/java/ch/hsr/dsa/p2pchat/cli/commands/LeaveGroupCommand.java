package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;

public class LeaveGroupCommand extends Command {

    @Override
    public String getName() {
        return "leaveGroup";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        if(!handler.leaveGroup(new Group(args[0]))) {
            systemMessage.accept("Could not leave group + " + args[0]);
        }
        systemMessage.accept("You have left the Group \""+args[0]+"\"");
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
