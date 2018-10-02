package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.Collections;

public class InviteToGroupCommand extends Command {

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    protected void onSuccess(ChatHandler handler, String[] args) {
        handler.inviteToGroup(new Group(args[1], Collections.emptyList()), new User(args[2])); // TODO get real group?
    }

    @Override
    protected int getNumberOfArguments() {
        return 2;
    }

    @Override
    public String getUsage() {
        return "";
    }
}
