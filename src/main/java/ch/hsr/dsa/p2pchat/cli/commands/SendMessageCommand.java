package ch.hsr.dsa.p2pchat.cli.commands;


import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.User;

public class SendMessageCommand implements Command {
    @Override
    public String getName() {
        return "send";
    }

    @Override
    public void run(ChatHandler handler, String commandInput) {
        // /send username test
        var args = CommandHelper.getArguements(2, commandInput);
        handler.sendMessage(new User(args[0]), args[1]);

    }
}
