package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.cli.ChatCLI;
import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class Main {

    public static void main(String args[])
        throws ExecutionException, InterruptedException, IOException {

        var input = new Scanner(System.in);

        var config = getChatConfiguration();

        if(config == null) {
            System.out.println("Hello, What is your name? ");
            var username = input.next();
            config = ChatConfiguration.builder().setOwnUser(username).build();
        }

        System.out.println(config.getOwnUser().getName() + ", Do you want to create your own chat network? [y(es)/n(o)]");
        if(input.next().startsWith("y")) {
            var chat = new ChatCLI(P2PChatHandler.start(config));
            chat.start().get();
        } else {
            System.out.println(config.getOwnUser().getName() + ", Could you tell me the IP.Address of somebody you now in the network");
            var ip = input.next(Pattern.compile("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])"));

            System.out.println(config.getOwnUser().getName() + ", Could you tell me the Port of " + ip);
            var port = input.nextInt();

            var chat = new ChatCLI((P2PChatHandler.start(Inet4Address.getByName(ip), port, config)));
            chat.start().get();
        }
    }

    private static ChatConfiguration getChatConfiguration() {
        return null;
    }
}
