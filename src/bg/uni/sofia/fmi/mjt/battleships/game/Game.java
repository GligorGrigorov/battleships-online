package bg.uni.sofia.fmi.mjt.battleships.game;

import bg.uni.sofia.fmi.mjt.battleships.exceptions.GameCapacityExceededException;
import bg.uni.sofia.fmi.mjt.battleships.exceptions.PlayerNotAvailableException;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 134948039702643492L;

    private final Set<String> players;
    private final Map<String, Table> playerTable;
    private final String creator;
    private final String gameName;
    private int numberOfPlayers;
    private Status status;
    private String winner;
    private final String[] playerNames;
    private int counter;

    public Game(String creator, String gameName) {
        players = new HashSet<>();
        playerTable = new HashMap<>();
        playerNames = new String[2];
        this.creator = creator;
        this.gameName = gameName;
        this.numberOfPlayers = 0;
        counter = 1;
        this.status = Status.PENDING;
    }

    private boolean isNextToPlay(String name) {
        return playerNames[counter % 2].equals(name);
    }

    private String getOpponent(String name) {
        return players.stream().filter(x -> !x.equals(name)).collect(Collectors.joining());
    }

    public Status getStatus() {
        return status;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void addPlayer(String name, Table table) throws GameCapacityExceededException {
        if (status != Status.PENDING) {
            throw new GameCapacityExceededException("Game is full");
        }
        playerNames[numberOfPlayers] = name;
        numberOfPlayers++;
        playerTable.put(name, table);
        players.add(name);
        if (numberOfPlayers == 2) {
            status = Status.IN_PROGRESS;
        }
    }

    public void surrender(String username) {
        if (!players.contains(username)) {
            throw new PlayerNotAvailableException("Player not in game");
        }
        if (status == Status.PENDING || status == Status.FINISHED && numberOfPlayers > 0) {
            numberOfPlayers--;
            return;
        }
        if (numberOfPlayers == 2) {
            winner = players.stream().filter(x -> !x.equals(username)).collect(Collectors.joining());
            status = Status.FINISHED;
            numberOfPlayers--;
        }
    }

    public String attack(String username, Point point) {
        if (status == Status.PENDING) {
            return "waiting for players";
        }
        if (status == Status.FINISHED) {
            return getOutput(username);
        }
        if (isNextToPlay(username)) {
            String opponent = getOpponent(username);
            playerTable.get(getOpponent(username)).attack(point);
            counter++;
            if (playerTable.get(username).getShipCellsRemaining() == 0) {
                winner = opponent;
                status = Status.FINISHED;
                return "DEFEAT";
            }
            if (playerTable.get(opponent).getShipCellsRemaining() == 0) {
                winner = username;
                status = Status.FINISHED;
                return "WIN";
            }
            return getOutput(username);
        }
        return "not your turn";
    }

    public String getOpponent() {
        return getOpponent(creator);
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return gameName;
    }

    public String getOutput(String username) {
        if (status == Status.FINISHED) {
            return winner.equals(username) ? "WIN" : "DEFEAT";
        }
        if (status != Status.IN_PROGRESS) {
            return "Game not in progress";
        }
        return playerTable.get(username).toString(false)
                + playerTable.get(getOpponent(username)).toString(true);
    }
}
