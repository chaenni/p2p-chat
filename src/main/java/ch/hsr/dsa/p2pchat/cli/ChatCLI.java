package ch.hsr.dsa.p2pchat.cli;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.cli.colorprinter.AnsiColor;
import ch.hsr.dsa.p2pchat.cli.colorprinter.ColorPrinter;
import ch.hsr.dsa.p2pchat.cli.commands.AcceptFriendRequestCommand;
import ch.hsr.dsa.p2pchat.cli.commands.Command;
import ch.hsr.dsa.p2pchat.cli.commands.CreateGroupCommand;
import ch.hsr.dsa.p2pchat.cli.commands.FriendListCommand;
import ch.hsr.dsa.p2pchat.cli.commands.FriendRequestCommand;
import ch.hsr.dsa.p2pchat.cli.commands.InviteToGroupCommand;
import ch.hsr.dsa.p2pchat.cli.commands.LeaveGroupCommand;
import ch.hsr.dsa.p2pchat.cli.commands.RejectFriendRequestCommand;
import ch.hsr.dsa.p2pchat.cli.commands.SendGroupMessageCommand;
import ch.hsr.dsa.p2pchat.cli.commands.SendMessageCommand;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.LeaveMessage;
import ch.hsr.dsa.p2pchat.model.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ChatCLI {

    private final ChatHandler handler;
    private Collection<Command> commands;
    private Future<?> scannerTask;

    public ChatCLI(ChatHandler handler) {
        this.handler = handler;
        this.commands = new ArrayList<>();
        Collections.addAll(commands, new CreateGroupCommand(), new FriendRequestCommand(), new InviteToGroupCommand(), new LeaveGroupCommand(), new SendGroupMessageCommand(), new SendMessageCommand(), new AcceptFriendRequestCommand(), new RejectFriendRequestCommand(), new FriendListCommand());

        handler.chatMessages().subscribe(this::displayMessage);
        handler.friendCameOnline().subscribe(this::displayFriendCameOnline);
        handler.userLeftGroup().subscribe(this::displayUserLeftGroup);
        handler.receivedFriendRequest().subscribe(this::displayFriendRequest);
    }

    private void displayFriendRequest(User user) {
        displayMessage(AnsiColor.BLUE, Optional.empty(), user, "User has send you a friend request type \"/accept" + user + "\" or \"/reject " + user + "\"");

    }

    public void displayMessage(AnsiColor color, Optional<Group> group, User user, String message) {
        var userString = group.map(g -> g +", ").orElse("")+user.getName();
        ColorPrinter.printInColor(color ,userString + ", " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ": ");
        System.out.println(message);
    }

    public void displaySystemMessage(String message) {
        ColorPrinter.printInColor(AnsiColor.YELLOW ,"System" + ", " + LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ": ");
        System.out.println(message);
    }

    private void displayMessage(ChatMessage message) {
        displayMessage(AnsiColor.BLUE, Optional.empty(), message.getFromUser(), message.getMessage());
    }

    private void displayFriendCameOnline(User user) {
        displayMessage(AnsiColor.GREEN, Optional.empty(), user, "is Online");
    }

    private void displayUserLeftGroup(LeaveMessage message) {
        displayMessage(AnsiColor.GREEN, Optional.ofNullable(message.getGroup()), message.getUser(), "has left the group");
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
        var commandName = commandInput.substring(1, commandInput.indexOf(" "));
        commands.stream().filter(command -> command.getName().equals(commandName))
            .findFirst()
            .ifPresent(c -> {
                c.run(ChatCLI.this::displaySystemMessage, handler, commandInput);
            });
    }

    public void stop() {
        scannerTask.cancel(true);
    }

}
