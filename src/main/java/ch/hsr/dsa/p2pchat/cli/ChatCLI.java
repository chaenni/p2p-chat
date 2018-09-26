package ch.hsr.dsa.p2pchat.cli;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.cli.colorprinter.AnsiColor;
import ch.hsr.dsa.p2pchat.cli.colorprinter.ColorPrinter;
import ch.hsr.dsa.p2pchat.cli.commands.Command;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ChatCLI {
    private Collection<Command> commands;
    private Future<?> scannerTask;

    public ChatCLI(ChatHandler handler) {
        this.commands = new ArrayList<>();
    }

    public void displayMessage(ChatMessage message) {
        ColorPrinter.printInColor(AnsiColor.RED, message.getFromUser().getName() + ", " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ": ");
        System.out.println(message.getMessage());
    }

    public Future<?> start() {
        scannerTask = ForkJoinPool.commonPool().submit(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String nextEntry = scanner.next();
                if (nextEntry.charAt(0) == '/') {
                    handleCommandInput(nextEntry);
                } else {
                    displayMessage(new ChatMessage(new User("User"), nextEntry));
                }
            }
        });
        return scannerTask;
    }

    private void handleCommandInput(String commandInput) {
        var commandName = commandInput.substring(1, commandInput.indexOf("/"));
        commands.stream().filter(command -> command.getName().equals(commandName))
            .findFirst()
            .ifPresent(c -> c.run(commandInput));
    }

    public void stop() {
        scannerTask.cancel(true);
    }

}
