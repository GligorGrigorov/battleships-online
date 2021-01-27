package bg.uni.sofia.fmi.mjt.battleships.commands;

import java.util.Arrays;

public enum  Commands {
    CREATE_GAME("create-game",true),
    LOGIN("login",false);

    private final String name;
    private final Boolean hasArgument;
    Commands(String name, Boolean hasArgument){
        this.name = name;
        this.hasArgument = hasArgument;
    }

    public String getName() {
        return name;
    }
    public boolean hasArgument(){
        return hasArgument;
    }
    public static boolean containsCommand(Command command){
        return Arrays.stream(Commands.values()).anyMatch(x -> command.getName()
                .equals(x.getName()) && command.containsArgument() == x.hasArgument());
    }
}
