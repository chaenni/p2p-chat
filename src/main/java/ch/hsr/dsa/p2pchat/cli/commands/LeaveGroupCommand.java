package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import java.util.Collections;

public class LeaveGroupCommand implements Command {

    @Override
    public String getName() {
        return "leaveGroup";
    }

    @Override
    public void run(ChatHandler handler, String commandInput) {
        var args = CommandHelper.getArguements(1, commandInput);
        handler.leaveGroup(new Group(args[0], Collections.emptyList())); // TODO get real group
    }
}
