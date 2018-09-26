package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;

public class InviteToGroupCommand implements Command {

    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public void run(ChatHandler handler, String commandInput) {
        var args = CommandHelper.getArguements(2, commandInput);
        handler.inviteToGroup(new Group(args[1]), new User(args[2]));
    }
}
