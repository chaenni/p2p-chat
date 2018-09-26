package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.cli.ChatCLI;
import ch.hsr.dsa.p2pchat.cli.Command;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.User;
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
        chat.displayMessage(new ChatMessage(new User("Hans"), "Hello"));
        chat.displayMessage(new ChatMessage(new User("Peter"), "Hi"));
        chat.displayMessage(new ChatMessage(new User("Emma"), "Hello"));
        done.get();
    }
}
