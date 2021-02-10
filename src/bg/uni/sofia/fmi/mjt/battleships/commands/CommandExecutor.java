package bg.uni.sofia.fmi.mjt.battleships.commands;

import bg.uni.sofia.fmi.mjt.battleships.files.FileHandler;
import bg.uni.sofia.fmi.mjt.battleships.game.Board;
import bg.uni.sofia.fmi.mjt.battleships.game.Point;
import bg.uni.sofia.fmi.mjt.battleships.game.Ship;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;

import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CommandExecutor implements Executor {
    private final Storage storage;
    private final FileHandler fileHandler;

    private static final String GAMES_FILENAME = "saved-games";

    public CommandExecutor(Storage storage) {
        this.storage = storage;
        this.fileHandler = new FileHandler(Path.of(GAMES_FILENAME), storage);
    }

    @Override
    public String executeCommand(Command command, SocketChannel channel) {
        String response = "wrong command";
        if (Commands.containsCommand(command)) {
            response = processMainCommands(command, channel);
        } else if (isValidCoordinate(command.getName())) {
            if (storage.isUserInGame(command.getUsername())) {
                response = storage.attack(command.getUsername(), pointFromString(command.getName()));
            }
        }
        return response + System.lineSeparator() + storage.getUserStatus(command.getUsername()).getPrompt();
    }

    private Point pointFromString(String coordinates) {
        int row = coordinates.charAt(0) - 'A' + 1;
        int column = Integer.parseInt(coordinates.substring(1));
        return new Point(column, row);
    }

    private boolean isValidCoordinate(String coordinate) {
        if (coordinate.length() != 2) {
            return false;
        }
        char row = coordinate.charAt(0);
        int column;
        try {
            column = Integer.parseInt(coordinate.substring(1));
        } catch (NumberFormatException e) {
            return false;
        }
        return row >= 'A' && row <= 'J' && column >= 1 && column <= 10;
    }

    String processMainCommands(Command command, SocketChannel channel) {
        String cmdName = command.getName();
        String[] cmdArguments = command.getArguments();
        String username = command.getUsername();
        switch (cmdName) {
            case "login" -> {
                if (storage.isRegisteredUser(username)) {
                    return Message.ALREADY_REGISTERED.toString();
                } else {
                    storage.registerUser(username);
                }
                if (storage.isLoggedInUser(username)) {
                    return Message.ALREADY_LOGGED_IN.toString();
                }
                storage.logInUser(username, channel);
                storage.setUserStatus(username, UserStatus.IN_MAIN_MENU);
                return Message.SUCCESSFUL_LOGIN.toString();
            }
            case "logout" -> {
                if (storage.isLoggedInUser(username)) {
                    storage.logOutUser(username);
                    storage.setUserStatus(username, UserStatus.OFFLINE);
                    return Message.SUCCESSFUL_LOGOUT.toString();
                }
                return Message.NOT_LOGGED_IN.toString();
            }
            case "create-game" -> {
                if (storage.isUserInGame(username)) {
                    return Message.NOT_ALLOWED.toString();
                }
                if (storage.containsGameName(cmdArguments[0])) {
                    return Message.GAME_EXISTS.toString();
                }
                Board board = new Board(username, cmdArguments[0]);
                storage.addGame(cmdArguments[0], board);
                storage.joinAGame(username, cmdArguments[0], getShipsFromArgument(cmdArguments));
                storage.setUserStatus(username, UserStatus.IN_GAME);
                return Message.GAME_SUCCESSFULLY_CREATED.toString();
            }
            case "list-games" -> {
                if (storage.isUserInGame(username)) {
                    return Message.NOT_ALLOWED.toString();
                }
                return gamesTable(storage.getGames());
            }
            case "join-game" -> {
                if (storage.getUserStatus(username) == UserStatus.IN_GAME || storage.getUserStatus(username) == UserStatus.PLAYING) {
                    return Message.NOT_ALLOWED.toString();
                }
                if (!storage.containsGameName(cmdArguments[0])) {
                    return Message.GAME_DO_NOT_EXIST.toString();
                }
                storage.joinAGame(username, cmdArguments[0], getShipsFromArgument(cmdArguments));
                storage.setUserStatus(username, UserStatus.IN_GAME);
                return "SUCCESFul join";
            }
            case "start" -> {
                if (storage.getUserStatus(username) != UserStatus.IN_GAME) {
                    return Message.NOT_ALLOWED.toString();
                }
                storage.setUserStatus(username, UserStatus.PLAYING);
                return "You are in game";
            }
            case "exit" -> {
                if (!storage.isUserInGame(username)) {
                    return Message.NOT_ALLOWED.toString();
                }
                storage.leaveGameWithoutSaving(username);
                storage.setUserStatus(username, UserStatus.IN_MAIN_MENU);
                return Message.GAME_LEFT.toString();
            }
            case "save-game" -> {
                if (storage.getUserStatus(username) != UserStatus.PLAYING) {
                    return Message.NOT_ALLOWED.toString();
                }
                fileHandler.saveGame(username);
                String firstPlayer = storage.getGameByName(storage.getCurrentGame(username)).getCreator();
                String secondPlayer = storage.getGameByName(storage.getCurrentGame(username)).getOpponent();
                storage.leaveGameWithoutSaving(firstPlayer);
                storage.leaveGameWithoutSaving(secondPlayer);
                storage.setUserStatus(firstPlayer, UserStatus.IN_MAIN_MENU);
                storage.setUserStatus(secondPlayer, UserStatus.IN_MAIN_MENU);
                storage.removeGame(storage.getCurrentGame(username));
                return "game saved";
            }
            case "saved-games" -> {
                if (storage.getUserStatus(username) == UserStatus.IN_MAIN_MENU) {
                    return storage.getSavedGames(username).stream().collect(Collectors.joining(System.lineSeparator()));
                }
                return Message.NOT_ALLOWED.toString();
            }
            case "load-game" -> {
                if (storage.getUserStatus(username) != UserStatus.IN_MAIN_MENU) {
                    return Message.NOT_ALLOWED.toString();
                }
                //TODO
                fileHandler.loadGame(storage.getSavedGame(username, cmdArguments[0]));
            }
        }
        return null;
    }

    private String gamesTable(Collection<Board> games) {
        StringBuilder builder = new StringBuilder();
        int cellSize = 20;
        builder.append(row(cellSize, new String[]{"NAME", "CREATOR", "STATUS", "PLAYERS"}))
                .append(System.lineSeparator());
        games.forEach(x -> builder.append(row(cellSize,
                new String[]{x.getName(), x.getCreator(), x.getStatus().toString(), x.getNumberOfPlayers() + ""}))
                .append(System.lineSeparator()));
        return builder.toString();
    }

    public String row(int cellSize, String[] elements) {
        StringBuilder builder = new StringBuilder();
        return Arrays.stream(elements)
                .map(x -> "| " + x + " ".repeat(cellSize - x.length() - 2))
                .collect(Collectors.joining(""));
    }

    private Ship[] getShipsFromArgument(String[] arguments) {
        Ship[] ships = new Ship[5];
        if (arguments.length == 6) {
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
