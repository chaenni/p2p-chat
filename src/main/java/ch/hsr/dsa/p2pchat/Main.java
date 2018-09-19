package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.cli.ChatCLI;
import ch.hsr.dsa.p2pchat.cli.Command;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String args[]) throws ExecutionException, InterruptedException {
        var chat = new ChatCLI(Collections.singletonList(new Command() {
            @Override
            public boolean matches(String message) {
                return message.startsWith("/hello");
            }

            @Override
            public void run() {
                System.out.println("Hello Command Triggered");
            }
        }));
        var done = chat.start();
        chat.displayMessage(new ChatMessage("Hans", "Hello", LocalDateTime.now()));
        chat.displayMessage(new ChatMessage("Peter", "Hi", LocalDateTime.now()));
        chat.displayMessage(new ChatMessage("Emma", "Hello", LocalDateTime.now()));
        done.get();
    }
}
