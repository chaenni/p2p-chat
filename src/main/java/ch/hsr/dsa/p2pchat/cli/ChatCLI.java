package ch.hsr.dsa.p2pchat.cli;

import ch.hsr.dsa.p2pchat.cli.colorprinter.AnsiColor;
import ch.hsr.dsa.p2pchat.cli.colorprinter.ColorPrinter;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ChatCLI {
    private Collection<Command> commands;
    private Future<?> scannerTask;

    public ChatCLI(Collection<Command> commands) {
        this.commands = commands;
    }

    public void displayMessage(ChatMessage message) {
        ColorPrinter.printInColor(AnsiColor.RED, message.getFromUsername() + ", " + message.getTime().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ": ");
        System.out.println(message.getMessage());
    }

    public Future<?> start() {
        scannerTask = ForkJoinPool.commonPool().submit(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String nextEntry = scanner.next();
                if (nextEntry.charAt(0) == '/') {
                    commands.stream().filter(command -> command.matches(nextEntry))
                        .findFirst()
                        .ifPresent(Command::run);
                } else {
                    displayMessage(new ChatMessage("User", nextEntry, LocalDateTime.now()));
                }
            }
        });
        return scannerTask;
    }

    public void stop() {
        scannerTask.cancel(true);
    }

}
