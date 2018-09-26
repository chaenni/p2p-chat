package ch.hsr.dsa.p2pchat.cli.commands;


import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.model.User;

public class SendMessageCommand implements Command {

    private final ChatHandler handler;

    public SendMessageCommand(ChatHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getName() {
        return "send";
    }

    @Override
    public void run(String commandInput) {
        // /send username test
        int commandEndIndex = commandInput.indexOf(getName(), 1) + getName().length();
        var inputWithOutCommand = commandInput.substring(commandEndIndex).trim();
        var username = inputWithOutCommand.substring(0, inputWithOutCommand.indexOf(" ")).trim();
        var message = inputWithOutCommand.substring(inputWithOutCommand.indexOf(username)+username.length()).trim();

        handler.sendMessage(new User(username), message);

    }
}
