package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.function.Consumer;

public class FriendRequestCommand extends Command {
    @Override
    public String getName() {
        return "friend";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        handler.sendFriendRequest(new User(args[0]));
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
