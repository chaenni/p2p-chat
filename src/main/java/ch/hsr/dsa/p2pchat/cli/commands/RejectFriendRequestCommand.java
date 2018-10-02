package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.function.Consumer;

public class RejectFriendRequestCommand extends Command {

    @Override
    public String getName() {
        return "reject";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        handler.rejectFriendRequest(new User(args[0]));
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
