package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;

public class CreateGroupCommand extends Command {

    @Override
    public String getName() {
        return "createGroup";
    }

    @Override
    protected void onSuccess(ChatHandler handler, String[] args) {
        handler.createGroup(args[0]);
    }

    @Override
    protected int getNumberOfArguments() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "groupname";
    }
}
