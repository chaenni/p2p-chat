package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.cli.ChatCLI;
import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import org.slf4j.MDC;

public class Main {

    public static void main(String args[])
        throws ExecutionException, InterruptedException, IOException {

        var input = new Scanner(System.in);

        var loadedConfig = ConfigHandler.loadConfiguration();

        var config = loadedConfig.orElseGet(() -> {
            System.out.println("Hello, What is your name? ");
            var username = input.next();

            System.out.println(username + ", What is your wallet path");
            var path = input.next();

            System.out.println(username + ", What is your wallet password");
            var password = input.next();

            return ChatConfiguration.builder()
                .setOwnUser(username)
                .setEthereumWalletPath(path)
                .setEthereumWalletPassword(password)
                .build();
        });

        MDC.put("user", config.getOwnUser().getName());

        System.out.println(config.getOwnUser().getName() + ", Do you want to create your own chat network? [y(es)/n(o)]");
        P2PChatHandler chatHandler;

        if(input.next().startsWith("y")) {
            chatHandler = P2PChatHandler.start(config);
        } else {
            System.out.println(config.getOwnUser().getName() + ", Could you tell me the IP.Address of somebody you now in the network");
            var ip = input.next(Pattern.compile("(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])"));

            System.out.println(config.getOwnUser().getName() + ", Could you tell me the Port of " + ip);
            var port = input.nextInt();

            chatHandler = P2PChatHandler.start(Inet4Address.getByName(ip), port, config);
        }

        var chat = new ChatCLI(chatHandler);
        chat.start().get();

        ChatHandler finalChatHandler = chatHandler;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                chat.stop();
                chatHandler.stop();
                ConfigHandler.storeConfiguration(finalChatHandler.getConfiguration());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }));
    }

}
