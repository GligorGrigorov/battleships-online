package bg.uni.sofia.fmi.mjt.battleships.game;

import java.io.Serial;
import java.io.Serializable;

public class Game implements Serializable {

    @Serial
    private static final long serialVersionUID = 134948039702643492L;

    private final Table[] tables;
    private final String[] names;
    private final String creator;
    private final String gameName;
    private int numberOfPlayers;
    private String nextToPlay;
    private Status status;
    private int winner;

    public Game(String creator, String gameName) {
        this.tables = new Table[2];
        this.names = new String[2];
        this.creator = creator;
        this.gameName = gameName;
        this.numberOfPlayers = 0;
        nextToPlay = creator;
        this.status = Status.PENDING;
        winner = -1;
    }

    public Status getStatus() {
        return status;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void addPlayer(String name, Table table) {
        tables[numberOfPlayers] = table;
        names[numberOfPlayers] = name;
        numberOfPlayers++;
        if (numberOfPlayers == 2) {
            status = Status.IN_PROGRESS;
        }
    }

    public void surrender(String username) {
        if(username.equals(creator)){
            winner = 1;
        }
        if (names[1].equals(username)) {
            winner = 0;
        }
        status = Status.FINISHED;
        numberOfPlayers--;
    }

    public String attack(String username, Point point) {
        if (status == Status.PENDING) {
            return "waiting for players";
        }
        if (status == Status.FINISHED) {
            if (username.equals(names[0])) {
                return winner == 0 ? "WIN" : "DEFEAT";
            }
            if (username.equals(names[1])) {
                return winner == 1 ? "WIN" : "DEFEAT";
            }
        }
        if (username.equals(names[0]) && username.equals(nextToPlay)) {
            tables[1].attack(point);
            nextToPlay = names[1];
            if (tables[0].getShipCellsRemaining() == 0) {
                return "DEFEAT";
            }
            if (tables[1].getShipCellsRemaining() == 0) {
                return "WIN";
            }
            return getCreatorOutput();
        }
        if (username.equals(names[1]) && username.equals(nextToPlay)) {
            tables[0].attack(point);
            nextToPlay = names[0];
            if (tables[1].getShipCellsRemaining() == 0) {
                return "DEFEAT";
            }
            if (tables[0].getShipCellsRemaining() == 0) {
                return "WIN";
            }
            return getOpponentOutput();
        }
        return "not your turn";
    }

    public String getOpponent() {
        return names[1];
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return gameName;
    }

    public String getCreatorOutput() {
        return tables[0].toString(false) + tables[1].toString(true);
    }

    public String getOpponentOutput() {
        return tables[1].toString(false) + tables[0].toString(true);
    }

    public String getOutput(String username) {
        Table table = null;
        if (username.equals(names[0])) {
            table = tables[0];
        }
        if (username.equals(names[1])) {
            table = tables[1];
        }
        if(table == null){
            return "error loading tables";
        }
        return table.toString(false) + tables[1].toString(true);
    }
}
