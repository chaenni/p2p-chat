package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.function.Consumer;

public class RejectRequestCommand extends Command {

    @Override
    public String getName() {
        return "reject";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        switch (args[0]) {
            case "user":
                handler.rejectFriendRequest(new User(args[1]));
                break;
            case "group":
                handler.rejectGroupRequest(new Group(args[1]));
                break;
            default:
                printUsage(systemMessage);
        }
    }

    @Override
    protected int getNumberOfArguments() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "user username || group groupname";
    }
}
