package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import java.util.Collections;

public class SendGroupMessageCommand extends Command{

    @Override
    public String getName() {
        return "sendGroupMessage";
    }

    @Override
    protected void onSuccess(ChatHandler handler, String[] args) {
        handler.sendGroupMessage(new Group(args[0], Collections.emptyList()), args[1]); // TODO get real group
    }

    @Override
    protected int getNumberOfArguments() {
        return 2;
    }

    @Override
    public void printUsage() {

    }
}
