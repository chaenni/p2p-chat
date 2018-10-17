package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.Optional;

public class ConfigHandler  {

    private static final String CONFIG_FILE_NAME = "chatConfig.conf";

    public static Optional<ChatConfiguration> loadConfiguration() {
        return getConfigPath()
            .flatMap(ConfigHandler::loadConfigFromFile);
    }

    public static void storeConfiguration(ChatConfiguration configuration) throws IOException {
        if (getConfigPath().isPresent()) {
            var path = getConfigPath().get();
            storeConfigInFile(configuration, path);
        } else {
            throw new IllegalStateException("Config path not defined");
        }
    }

    private static Optional<ChatConfiguration> loadConfigFromFile(String path) {
        try {
            var config = (ChatConfiguration) new ObjectInputStream(new FileInputStream(path)).readObject();
            return Optional.of(config);

        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private static void storeConfigInFile(ChatConfiguration configuration, String path) throws IOException {
        var oos = new ObjectOutputStream(new FileOutputStream(path));
        oos.writeObject(configuration);
    }


    private static Optional<String> getConfigPath() {
        try {
            var jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            return Optional.of(jarPath + File.pathSeparator + CONFIG_FILE_NAME);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }
}
