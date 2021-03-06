package ch.hsr.dsa.p2pchat.cli;

import ch.hsr.dsa.p2pchat.ChatHandler;
import ch.hsr.dsa.p2pchat.cli.colorprinter.AnsiColor;
import ch.hsr.dsa.p2pchat.cli.colorprinter.ColorPrinter;
import ch.hsr.dsa.p2pchat.cli.commands.AcceptRequestCommand;
import ch.hsr.dsa.p2pchat.cli.commands.Command;
import ch.hsr.dsa.p2pchat.cli.commands.CreateGroupCommand;
import ch.hsr.dsa.p2pchat.cli.commands.FriendListCommand;
import ch.hsr.dsa.p2pchat.cli.commands.FriendRequestCommand;
import ch.hsr.dsa.p2pchat.cli.commands.GetCertifiedMessageStateCommand;
import ch.hsr.dsa.p2pchat.cli.commands.GetGroupInformationCommand;
import ch.hsr.dsa.p2pchat.cli.commands.HelpCommand;
import ch.hsr.dsa.p2pchat.cli.commands.InviteToGroupCommand;
import ch.hsr.dsa.p2pchat.cli.commands.LeaveGroupCommand;
import ch.hsr.dsa.p2pchat.cli.commands.RejectRequestCommand;
import ch.hsr.dsa.p2pchat.cli.commands.SendCertifiedMessageCommand;
import ch.hsr.dsa.p2pchat.cli.commands.SendGroupMessageCommand;
import ch.hsr.dsa.p2pchat.cli.commands.SendMessageCommand;
import ch.hsr.dsa.p2pchat.model.CertifiedChatMessage;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.GroupInvite;
import ch.hsr.dsa.p2pchat.model.GroupMessage;
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
        Collections.addAll(commands, new CreateGroupCommand(), new FriendRequestCommand(), new InviteToGroupCommand(), new LeaveGroupCommand(), new SendGroupMessageCommand(), new SendMessageCommand(), new AcceptRequestCommand(), new RejectRequestCommand(), new FriendListCommand(), new SendCertifiedMessageCommand(), new GetCertifiedMessageStateCommand(), new GetGroupInformationCommand());
        commands.add(new HelpCommand(commands));

        handler.chatMessages().subscribe(this::displayMessage);
        handler.friendCameOnline().subscribe(this::displayFriendCameOnline);
        handler.friendWentOffline().subscribe(this::displayFriendWentOffline);
        handler.userLeftGroup().subscribe(this::displayUserLeftGroup);
        handler.receivedFriendRequest().subscribe(this::displayFriendRequest);
        handler.friendRequestAccepted().subscribe(this::displayFriendRequestAccepted);
        handler.friendRequestRejected().subscribe(this::displayFriendRequestRejected);
        handler.receivedGroupRequest().subscribe(this::displayGroupRequest);
        handler.systemMessage().subscribe(this::displaySystemMessage);
        handler.groupChatMessages().subscribe(this::displayGroupMessage);
        handler.receivedCertifiedMessage().subscribe(this::displayCertifiedMessageReceived);

    }

    private void displayCertifiedMessageReceived(CertifiedChatMessage certifiedChatMessage) {
        var hash = certifiedChatMessage.getBase64Hash();
        displayMessage(AnsiColor.BLUE, Optional.empty(), Optional.of(certifiedChatMessage.getFromUser()),
            "You have received the following certified message. "
                + "Do you want to reject or accept: "
                + "type \"/accept message " + hash + "\" or \"/reject message " + hash + "\" ");

        displayMessage(new ChatMessage(certifiedChatMessage.getFromUser(), certifiedChatMessage.getMessage()));
    }

    private void displayGroupMessage(GroupMessage groupMessage) {
        displayMessage(AnsiColor.BLUE, Optional.of(groupMessage.getGroup()), Optional.of(groupMessage.getFromUser()), groupMessage.getMessage());
    }

    private void displayGroupRequest(GroupInvite group) {
        displayMessage(AnsiColor.BLUE, Optional.of(new Group(group.getGroupName())), Optional.empty(),
            "You have received a Group Request type \"/accept group " + group.getGroupName() + "\" or \"/reject group " + group.getGroupName() + "\" "
                + "or type \"/group " + group.getGroupName() +"\" to get information about the group");
    }

    private void displayFriendRequestRejected(User user) {
        displayMessage(AnsiColor.BLUE, Optional.empty(), Optional.of(user), "User has rejected your request");
    }

    private void displayFriendRequestAccepted(User user) {
        displayMessage(AnsiColor.BLUE, Optional.empty(), Optional.of(user), "User has accepted your request");
    }

    private void displayFriendRequest(User user) {
        displayMessage(AnsiColor.BLUE, Optional.empty(), Optional.of(user), "User has send you a friend request type \"/accept user " + user.getName() + "\" or \"/reject user " + user.getName() + "\"");

    }

    public void displayMessage(AnsiColor color, Optional<Group> group, Optional<User> user, String message) {
        var userString = "";
        if(group.isPresent() && user.isPresent()) {
            userString = group.get().getName() + ", " + user.get().getName();
        } else if(group.isPresent()) {
            userString = group.get().getName();
        } else if(user.isPresent()) {
            userString = user.get().getName();
        }
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
        displayMessage(AnsiColor.BLUE, Optional.empty(), Optional.of(message.getFromUser()), message.getMessage());
    }

    private void displayFriendCameOnline(User user) {
        displayMessage(AnsiColor.GREEN, Optional.empty(), Optional.of(user), "is Online");
    }

    private void displayFriendWentOffline(User user) {
        displayMessage(AnsiColor.GREEN, Optional.empty(), Optional.of(user), "went Offline");
    }

    private void displayUserLeftGroup(LeaveMessage message) {
        displayMessage(AnsiColor.GREEN, Optional.ofNullable(message.getGroup()),Optional.of(message.getUser()), "has left the group");
    }

    public Future<?> start() {
        scannerTask = ForkJoinPool.commonPool().submit(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String nextEntry = scanner.nextLine();
                try {
                    if (nextEntry.charAt(0) == '/') {
                        handleCommandInput(nextEntry);
                    } else {
                        displayMessage(new ChatMessage(new User("User"), nextEntry));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    displaySystemMessage("Continue");
                }
            }
        });
        return scannerTask;
    }

    private void handleCommandInput(String commandInput) {
        var commandName = commandInput.substring(1);
        if(commandInput.indexOf(" ") > 0)
            commandName = commandName.substring(0, commandInput.indexOf(" ")).trim();

        String finalCommandName = commandName;
        commands.stream().filter(command -> command.getName().equals(finalCommandName))
            .findFirst()
            .ifPresent(c -> {
                c.run(ChatCLI.this::displaySystemMessage, handler, commandInput);
            });
    }

    public void stop() {
        scannerTask.cancel(true);
    }

}
