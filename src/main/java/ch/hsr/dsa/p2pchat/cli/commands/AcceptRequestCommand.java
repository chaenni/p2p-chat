package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.Base64;
import java.util.function.Consumer;

public class AcceptRequestCommand extends Command {

    @Override
    public String getName() {
        return "accept";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        switch (args[0]) {
            case "user":
                handler.acceptFriendRequest(new User(args[1]));
                systemMessage.accept("Request accepted");
                break;
            case "group":
                handler.acceptGroupRequest(new Group(args[1]));
                systemMessage.accept("Request accepted");
                break;
            case "message":
                handler.acceptCertifiedMessage(Base64.getDecoder().decode(args[1]));
                break;
            default:
                printUsage(systemMessage);
        }
    }

    @Override
    protected int getNumberOfArguments() {
        return 2;
    }

    @Override
    public String getUsage() {
        return "user <username> || group <groupname> || message <hash>";
    }
}
