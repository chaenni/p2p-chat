package ch.hsr.dsa.p2pchat.cli.commands;

import ch.hsr.dsa.p2pchat.ChatHandler;
import java.util.function.Consumer;

public class GetGroupInformationCommand extends Command {

    @Override
    public String getName() {
        return "group";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        var group = handler.getGroupInformation(args[0]);
        if(group.isPresent()) {
            systemMessage.accept(group.get().toString());
        } else {
            systemMessage.accept("Group with name \"" + args[0] + "\" does not exist");
        }
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
