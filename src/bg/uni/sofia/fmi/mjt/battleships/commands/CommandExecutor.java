package bg.uni.sofia.fmi.mjt.battleships.commands;

import bg.uni.sofia.fmi.mjt.battleships.exceptions.*;
import bg.uni.sofia.fmi.mjt.battleships.files.FileHandler;
import bg.uni.sofia.fmi.mjt.battleships.game.Game;
import bg.uni.sofia.fmi.mjt.battleships.game.Point;
import bg.uni.sofia.fmi.mjt.battleships.game.Ship;
import bg.uni.sofia.fmi.mjt.battleships.game.Status;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;
import bg.uni.sofia.fmi.mjt.battleships.server.Pair;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;

import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class CommandExecutor implements Executor {

    private final Storage storage;
    private final FileHandler fileHandler;
    private final Queue<Pair> responsesQueue;

    public CommandExecutor(Storage storage, FileHandler fileHandler, Queue<Pair> responsesQueue) {
        this.storage = storage;
        this.fileHandler = fileHandler;
        this.responsesQueue = responsesQueue;
    }

    private void addResponse(String username, String response, SocketChannel channel) {
        synchronized (responsesQueue) {
            responsesQueue.add(new Pair(response
                    + System.lineSeparator()
                    + storage.getUserStatus(username).getPrompt(), channel));
            responsesQueue.notifyAll();
        }
    }

    @Override
    public void executeCommand(Command command, SocketChannel channel) {
        String response = Message.WRONG_COMMAND.toString();
        String cmdName = command.getName();
        String username = command.getUsername();

        if (Commands.containsCommand(command)) {
            response = processMainCommands(command, channel);
        } else if (isValidCoordinate(cmdName) && storage.getUserStatus(username) == UserStatus.PLAYING) {
            response = storage.attack(username, pointFromString(cmdName));
            String opponent;
            try {
                opponent = storage.getOpponent(username);
            } catch (PlayerNotAvailableException e) {
                response = e.getMessage();
                addResponse(username, response, channel);
                return;
            }
            if (response.equals("WIN") || response.equals("DEFEAT")) {
                exit(username);
                exit(opponent);
            } else if (storage.getGameByName(storage.getCurrentGame(username)).getNumberOfPlayers() == 2) {
                addResponse(opponent, storage.getGameOutput(opponent), storage.getChannel(opponent));
            }
        }
        addResponse(username, response, channel);
    }

    private Point pointFromString(String coordinates) {
        int row = coordinates.charAt(0) - 'A' + 1;
        int column = Integer.parseInt(coordinates.substring(1));
        return new Point(column, row);
    }

    String processMainCommands(Command command, SocketChannel channel) {
        String cmdName = command.getName();
        String[] cmdArguments = command.getArguments();
        String username = command.getUsername();
        return switch (cmdName) {
            case "login" -> login(username, channel);
            case "logout" -> logout(username);
            case "create-game" -> createGame(username, cmdArguments);
            case "list-games" -> listGames(username);
            case "join-game" -> joinGame(username, cmdArguments);
            case "start" -> start(username);
            case "exit" -> exit(username);
            case "save-game" -> saveGame(username);
            case "saved-games" -> savedGames(username);
            case "load-game" -> loadGame(username, cmdArguments);
            default -> null;
        };
    }

    //Command logic methods
    String login(String username, SocketChannel channel) {
        if (storage.isRegisteredUser(username) && storage.isLoggedInUser(username)) {
            return Message.ALREADY_LOGGED_IN.toString();
        } else {
            storage.registerUser(username);
        }
        storage.logInUser(username, channel);
        storage.setUserStatus(username, UserStatus.IN_MAIN_MENU);
        return Message.SUCCESSFUL_LOGIN.toString();
    }

    String logout(String username) {
        if (storage.getUserStatus(username) == UserStatus.IN_MAIN_MENU) {
            storage.logOutUser(username);
            storage.setUserStatus(username, UserStatus.OFFLINE);
            return Message.SUCCESSFUL_LOGOUT.toString();
        }
        return Message.WRONG_COMMAND.toString();
    }

    String createGame(String username, String[] cmdArguments) {
        if (storage.getUserStatus(username) != UserStatus.IN_MAIN_MENU) {
            return Message.NOT_ALLOWED.toString();
        }
        if (storage.containsGameName(cmdArguments[0])) {
            return Message.GAME_EXISTS.toString();
        }
        Ship[] ships;
        try {
            ships = getShipsFromArgument(cmdArguments);
        } catch (ShipCreationException e) {
            return e.getMessage();
        }
        Table table;
        try {
            table = new Table(ships);
        } catch (TableCreationException e) {
            return e.getMessage();
        }
        Game game = new Game(username, cmdArguments[0]);
        storage.addGame(cmdArguments[0], game);
        storage.joinAGame(username, cmdArguments[0], table);
        storage.setUserStatus(username, UserStatus.IN_GAME);
        return "Created game " + game.getName() + ", players " + game.getNumberOfPlayers() + "/2";
    }

    String listGames(String username) {
        if (storage.getUserStatus(username) != UserStatus.IN_MAIN_MENU) {
            return Message.NOT_ALLOWED.toString();
        }
        return gamesTable(storage.getGames());
    }

    String joinGame(String username, String[] cmdArguments) {
        if (storage.getUserStatus(username) != UserStatus.IN_MAIN_MENU) {
            return Message.NOT_ALLOWED.toString();
        }
        String gameName = cmdArguments[0];
        if (!storage.containsGameName(gameName)) {
            return Message.GAME_DO_NOT_EXIST.toString();
        }
        Ship[] ships;
        try {
            ships = getShipsFromArgument(cmdArguments);
        } catch (ShipCreationException e) {
            return e.getMessage();
        }
        Table table;
        try {
            table = new Table(ships);
        } catch (TableCreationException e) {
            return e.getMessage();
        }
        storage.joinAGame(username, gameName, table);
        storage.setUserStatus(username, UserStatus.IN_GAME);
        String opponent = storage.getOpponent(username);
        if (opponent != null) {
            addResponse(username, username + " joined the game.", storage.getChannel(opponent));
        }
        return "Successfully joined a game " + gameName;
    }

    String start(String username) {
        if (storage.getUserStatus(username) != UserStatus.IN_GAME) {
            return Message.NOT_ALLOWED.toString();
        }
        Game game = storage.getGameByName(storage.getCurrentGame(username));
        if (game.getStatus() != Status.IN_PROGRESS) {
            return "Waiting for opponent";
        }
        storage.setUserStatus(username, UserStatus.PLAYING);
        return "You are in game";
    }

    String exit(String username) {
        if (storage.getUserStatus(username) == UserStatus.IN_MAIN_MENU) {
            return Message.NOT_ALLOWED.toString();
        }
        return storage.leaveGameWithoutSaving(username);
    }

    String saveGame(String username) {
        if (storage.getUserStatus(username) != UserStatus.PLAYING) {
            return Message.NOT_ALLOWED.toString();
        }
        Game game = storage.getGameByName(storage.getCurrentGame(username));
        if (game.getStatus() != Status.IN_PROGRESS) {
            return "Game not in progress";
        }
        String firstPlayer = game.getCreator();
        String secondPlayer = game.getOpponent();
        fileHandler.saveGame(username);
        storage.leaveGameWithoutSaving(firstPlayer);
        storage.leaveGameWithoutSaving(secondPlayer);
        storage.setUserStatus(firstPlayer, UserStatus.IN_MAIN_MENU);
        storage.setUserStatus(secondPlayer, UserStatus.IN_MAIN_MENU);
        storage.removeGame(storage.getCurrentGame(username));
        return "game saved";
    }

    String savedGames(String username) {
        if (storage.getUserStatus(username) == UserStatus.IN_MAIN_MENU) {
            return storage.getSavedGames(username).stream().collect(Collectors.joining(System.lineSeparator()));
        }
        return Message.NOT_ALLOWED.toString();
    }

    String loadGame(String username, String[] cmdArguments) {
        if (storage.getUserStatus(username) != UserStatus.IN_MAIN_MENU) {
            return Message.NOT_ALLOWED.toString();
        }
        fileHandler.loadGame(storage.getSavedGame(username, cmdArguments[0]));
        storage.setUserStatus(username, UserStatus.IN_GAME);
        storage.continuePlaying(username, cmdArguments[0]);
        return "successfully loaded game";
    }

    private String gamesTable(Collection<Game> games) {
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
        return Arrays.stream(elements)
                .map(x -> "| " + x + " ".repeat(cellSize - x.length() - 2))
                .collect(Collectors.joining(""));
    }

    private boolean isValidCoordinate(String coordinate) {
        if (coordinate.length() < 2) {
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

    private boolean isValidSegmentOrientation(Point p1, Point p2) {
        return p1.x() == p2.x() || p1.y() == p2.y();
    }

    private int getSegmentLength(Point p1, Point p2) {
        return (int) Math.round(Math.sqrt((p1.x() - p2.x()) * (p1.x() - p2.x()) + (p1.y() - p2.y()) * (p1.y() - p2.y()))) + 1;
    }

    private Ship[] getShipsFromArgument(String[] arguments) throws ShipCreationException {
        int shipsNumber = 10;
        Ship[] ships = new Ship[shipsNumber];
        Map<Integer, Integer> remainingShips = new HashMap<>();
        remainingShips.put(5, 1);
        remainingShips.put(4, 2);
        remainingShips.put(3, 3);
        remainingShips.put(2, 4);
        if (arguments.length == shipsNumber + 1) {
            for (int i = 1; i < arguments.length; i++) {
                String[] tokens = arguments[i].split(",");
                String first = tokens[0];
                String second = tokens[1];
                if (!isValidCoordinate(first) || !isValidCoordinate(second)) {
                    throw new IllegalShipCoordinateException("Wrong ship coordinates");
                }
                Point p1 = new Point(Integer.parseInt(first.substring(1)), first.charAt(0) - 'A' + 1);
                Point p2 = new Point(Integer.parseInt(second.substring(1)), second.charAt(0) - 'A' + 1);
                if (!isValidSegmentOrientation(p1, p2)) {
                    throw new IllegalShipCoordinateException("Ships can't go diagonal");
                }
                int shipLength = getSegmentLength(p1, p2);
                if (!remainingShips.containsKey(shipLength)) {
                    throw new IllegalShipCoordinateException("Ship with " + "length " + shipLength + " not available");
                }
                if (remainingShips.get(shipLength) == 0) {
                    throw new IllegalShipCoordinateException("No more ships available of this type");
                }
                int number = remainingShips.get(shipLength);
                remainingShips.put(shipLength, number - 1);
                ships[i - 1] = new Ship(p1, p2);
            }
        } else {
            throw new WrongNumberOFShipsException("Number of ships must be " + shipsNumber);
        }
        return ships;
    }
}
