package ch.hsr.dsa.p2pchat.cli.commands;


import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.MessageState;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.function.Consumer;

public class GetCertifiedMessageStateCommand extends Command {
    @Override
    public String getName() {
        return "getCertifiedState";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        MessageState state = handler.getCertifiedMessageState(args[1].getBytes());
        systemMessage.accept(state.name());
    }

    @Override
    protected int getNumberOfArguments() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "<hash>";
    }
}
