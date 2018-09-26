package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;

public class CreateGroupCommand implements Command {

    @Override
    public String getName() {
        return "createGroup";
    }

    @Override
    public void run(ChatHandler handler, String commandInput) {
        var args = CommandHelper.getArguements(1, commandInput);
        handler.createGroup(args[0]);
    }
}
