package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;

public class FriendRequestCommand implements Command {
    @Override
    public String getName() {
        return "friend";
    }

    @Override
    public void run(ChatHandler handler, String commandInput) {
        var args = CommandHelper.getArguements(1, commandInput);
        handler.sendFriendRequest(args[0]);
    }
}
