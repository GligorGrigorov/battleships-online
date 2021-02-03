package bg.uni.sofia.fmi.mjt.battleships.commands;

import bg.uni.sofia.fmi.mjt.battleships.game.Board;
import bg.uni.sofia.fmi.mjt.battleships.game.Point;
import bg.uni.sofia.fmi.mjt.battleships.game.Ship;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;

import java.nio.channels.SocketChannel;
import java.util.Collection;

public class CommandExecutor implements Executor {
    private final Storage storage;

    public CommandExecutor(Storage storage) {
        this.storage = storage;
    }

    @Override
    public String executeCommand(Command command, SocketChannel channel) {

        if (Commands.containsCommand(command)) {
            return processMainCommands(command, channel);
        }
        return "wrong command";
    }

    String processMainCommands(Command command, SocketChannel channel) {
        String cmdName = command.getName();
        String[] cmdArguments = command.getArguments();
        String username = command.getUsername();
        switch (cmdName) {
            case "login" -> {
                if (storage.isRegisteredUser(username)) {
                    return "user already exist";
                } else {
                    storage.registerUser(username);
                }
                if (storage.isLoggedInUser(username)) {
                    return "user already logged in";
                }
                storage.logInUser(username, channel);
                return "successfully logged in";
            }
            case "logout" -> {
                if (storage.isLoggedInUser(username)) {
                    storage.logOutUser(username);
                    return "successfully logged out";
                }
                return "not logged in";
            }
            case "create-game" -> {
                if (storage.isUserInGame(username)) {
                    return "not allowed command";
                }
                if (storage.containsGameName(cmdArguments[0])) {
                    return "game with this name already exist";
                }
                Board board = new Board(username, cmdArguments[0], new Table(getShipsFromArgument(cmdArguments)));
                storage.addGame(username, board);
                storage.joinAGame(username, board);
                if(storage.containsGame(board)){
                    return "game successfully created";
                }
                return "problem while creating a game";
            }
            case "list-games" -> {
                if (storage.isUserInGame(username)) {
                    return "not allowed command";
                }
                return gamesTable(storage.getGames());
            }
        }
        return null;
    }

    private String gamesTable(Collection<Board> games) {
        StringBuilder builder = new StringBuilder();
        for (Board game:
             games) {
            builder.append(game.getName()).append(" | ").append(game.getCreator()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private Ship[] getShipsFromArgument(String[] arguments){
        Ship[] ships = new Ship[5];
        if (arguments.length == 6){
            for (int i = 1; i < arguments.length; i++) {
                String[] tokens = arguments[i].split(",");
                String first = tokens[0];
                String second = tokens[1];
                ships[i - 1] = new Ship(new Point(first.charAt(1) - '0', first.charAt(0) - 'A' + 1),
                        new Point(second.charAt(1) - '0', second.charAt(0) - 'A' + 1));
            }
        }
        return ships;
    }
}
