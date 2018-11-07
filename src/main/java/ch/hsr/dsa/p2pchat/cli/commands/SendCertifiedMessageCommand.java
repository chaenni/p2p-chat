package ch.hsr.dsa.p2pchat.cli.commands;


import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.function.Consumer;

public class SendCertifiedMessageCommand extends Command {
    @Override
    public String getName() {
        return "sendCertified";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        handler.sendMessage(new User(args[0]), args[1]);
    }

    @Override
    protected int getNumberOfArguments() {
        return 2;
    }

    @Override
    public String getUsage() {
        return "username message";
    }
}
