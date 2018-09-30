package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import java.util.Collections;

public class SendGroupMessageCommand implements Command{

    @Override
    public String getName() {
        return "sendGroupMessage";
    }

    @Override
    public void run(ChatHandler handler, String commandInput) {
        var args = CommandHelper.getArguements(2, commandInput);
        handler.sendGroupMessage(new Group(args[0], Collections.emptyList()), args[1]); // TODO get real group
    }
}
