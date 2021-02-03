package bg.uni.sofia.fmi.mjt.battleships.commands;

import java.util.Arrays;
import java.util.Collections;

public class Command {
    private final String name;
    private final String[] arguments;
    private final String username;
    public Command(String input){
        String[] tokens = input.replaceAll("\\s{2,}", " ").trim().split(" ");
        this.name = tokens[0];
        this.arguments = Arrays.copyOfRange(tokens,1,tokens.length - 1);
        this.username = tokens[tokens.length - 1];

    }

    public String getUsername() {
        return username;
    }
    public String[] getArguments() {
        return arguments;
    }
    public String getName() {
        return name;
    }
}
