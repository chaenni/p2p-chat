package ch.hsr.dsa.p2pchat.cli.commands;

public class CommandHelper {

    public static String[] getArguments(int numberOfArguments, String commandInput) throws IllegalArgumentException {
        String[] args = new String[numberOfArguments];

        try {
            int commandEndIndex = commandInput.indexOf(" ");
            var restInput = commandInput.substring(commandEndIndex).trim();
            for (int i = 0; i < numberOfArguments; i++) {
                args[i] = restInput.substring(0,
                    (i == numberOfArguments - 1) ? restInput.length() : restInput.indexOf(" "));
                restInput = restInput.substring(restInput.indexOf(" ")).trim();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return args;
    }

}
