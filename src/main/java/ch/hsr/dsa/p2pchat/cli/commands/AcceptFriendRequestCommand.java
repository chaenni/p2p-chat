package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.User;

public class AcceptFriendRequestCommand extends Command {

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    protected void onSuccess(ChatHandler handler, String[] args) {
        handler.acceptFriendRequest(new User(args[0]));
    }

    @Override
    protected int getNumberOfArguments() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "username";
    }
}
