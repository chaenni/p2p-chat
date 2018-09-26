package ch.hsr.dsa.p2pchat.cli.commands;

public class CommandHelper {

    public static String[] getArguements(int numberOfArguments, String commandInput) {
        String[] args = new String[numberOfArguments];

        int commandEndIndex = commandInput.indexOf(" ");
        var restInput = commandInput.substring(commandEndIndex).trim();
        for(int i = 0; i<numberOfArguments; i++) {
            args[i] = restInput.substring(0, (i==numberOfArguments-1) ? restInput.length() : restInput.indexOf(" "));
            restInput = restInput.substring(restInput.indexOf(" ")).trim();
        }

        return args;
    }

}
