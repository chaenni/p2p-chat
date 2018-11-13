package ch.hsr.dsa.p2pchat.cli.commands;


import ch.hsr.dsa.p2pchat.ChatHandler;
import java.util.Base64;
import java.util.function.Consumer;

public class GetCertifiedMessageStateCommand extends Command {

    @Override
    public String getName() {
        return "getCertifiedState";
    }

    @Override
    protected void onSuccess(ChatHandler handler, Consumer<String> systemMessage, String[] args) {
        byte[] decodedHash = Base64.getDecoder().decode(args[0]);
        handler.getCertifiedMessageState(decodedHash)
            .subscribe(messageState -> systemMessage.accept(messageState.name()));
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
