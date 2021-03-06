package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.Collections;
import java.util.function.Consumer;

public class InviteToGroupCommand extends Command {

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        handler.inviteToGroup(new Group(args[0], Collections.emptyList()), new User(args[1]));
    }

    @Override
    protected int getNumberOfArguments() {
        return 2;
    }

    @Override
    public String getUsage() {
        return "groupname username";
    }
}
