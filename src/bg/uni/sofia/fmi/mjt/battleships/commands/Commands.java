package bg.uni.sofia.fmi.mjt.battleships.commands;

import java.util.Arrays;

public enum Commands {
    CREATE_GAME("create-game", 11),
    LOGIN("login", 0),
    LOGOUT("logout", 0),
    JOIN_GAME("join-game", 11),
    LIST_GAMES("list-games", 0),
    EXIT_GAME("exit", 0),
    START("start", 0),
    SAVE_GAME("save-game", 0),
    SAVED_GAMES("saved-games", 0),
    LOAD_GAME("load-game", 1);
    private final String name;
    private final int numberOfArguments;

    Commands(String name, int numberOfArguments) {
        this.name = name;
        this.numberOfArguments = numberOfArguments;
    }

    public String getName() {
        return name;
    }

    public static boolean containsCommand(Command command) {
        return Arrays.stream(Commands.values()).anyMatch(x -> command.getName()
                .equals(x.getName()) && command.getArguments().length == x.numberOfArguments);
    }
}
