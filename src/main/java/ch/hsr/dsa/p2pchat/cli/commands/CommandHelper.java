package ch.hsr.dsa.p2pchat.cli.commands;

public class CommandHelper {

    public static String[] getArguments(int numberOfArguments, String commandInput) throws IllegalArgumentException {
        String[] args = new String[numberOfArguments];

        try {
            var restInput = commandInput.trim();
            for (int i = 0; i < numberOfArguments; i++) {
                restInput = restInput.substring(restInput.indexOf(" ")).trim();
                args[i] = restInput.substring(0,
                    (i == numberOfArguments - 1) ? restInput.length() : restInput.indexOf(" "));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return args;
    }

}
