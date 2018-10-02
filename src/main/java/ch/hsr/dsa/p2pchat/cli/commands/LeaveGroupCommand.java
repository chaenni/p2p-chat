package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import java.util.Collections;

public class LeaveGroupCommand extends Command {

    @Override
    public String getName() {
        return "leaveGroup";
    }

    @Override
    protected void onSuccess(ChatHandler handler, String[] args) {
        handler.leaveGroup(new Group(args[0], Collections.emptyList())); // TODO get real group
    }

    @Override
    protected int getNumberOfArguments() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "";
    }
}
