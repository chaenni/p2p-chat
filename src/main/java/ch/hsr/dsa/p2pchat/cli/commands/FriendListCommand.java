package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.FriendsListEntry;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.function.Consumer;

public class FriendListCommand extends Command {

    @Override
    public String getName() {
        return "friends";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        systemMessage.accept("Friendlist: ");
        handler.friendsList().forEach(f -> {
            systemMessage.accept(f.getFriend().getName() + " is " + (f.isOnline() ? "online" : "offline"));
        });
    }

    @Override
    protected int getNumberOfArguments() {
        return 0;
    }

    @Override
    public String getUsage() {
        return "";
    }
}
