package bg.uni.sofia.fmi.mjt.battleships.game;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board implements Serializable {

    @Serial
    private static final long serialVersionUID = 134948039702643492L;

    //Map<String, Table> tables;
    private final Table[] tables;
    private final String[] names;
    private final String creator;
    private final String gameName;
    private int numberOfPlayers;
    private int balance;
    private Status status;
    private int winner;

    public Board(String creator, String gameName) {
        this.tables = new Table[2];
        this.names = new String[2];
        //tables = new HashMap<>();
        this.creator = creator;
        this.gameName = gameName;
        this.numberOfPlayers = 0;
        this.balance = 0;
        this.status = Status.PENDING;
        winner = -1;
    }

    public Status getStatus() {
        return status;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public boolean isFull() {
        return numberOfPlayers >= 2;
    }

    public void addPlayer(String name, Table table) {
        //tables.put(name,table);
        tables[numberOfPlayers] = table;
        names[numberOfPlayers] = name;
        numberOfPlayers++;
        if (numberOfPlayers == 2) {
            status = Status.IN_PROGRESS;
        }
    }

    public void surrender(String username) {
        if (names[0].equals(username)) {
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
                return winner == 0 ? "WIN" : "LOST";
            }
            if (username.equals(names[1])) {
                return winner == 1 ? "WIN" : "LOST";
            }
        }
        if (tables[0].getShipCellsRemaining() == 0 && tables[1].getShipCellsRemaining() == 0) {
            return "Unresolved";
        }
        if (username.equals(names[0]) && balance < 1) {
            tables[1].attack(point);
            balance++;
            if (tables[0].getShipCellsRemaining() == 0) {
                return "you lose";
            }
            if (tables[1].getShipCellsRemaining() == 0) {
                return "you win";
            }
            return getCreatorOutput();
        }
        if (username.equals(names[1]) && balance > -1) {
            tables[0].attack(point);
            balance--;
            if (tables[1].getShipCellsRemaining() == 0) {
                return "you lose";
            }
            if (tables[0].getShipCellsRemaining() == 0) {
                return "you win";
            }
            return getOpponentOutput();
        }
        return "error while attacking";
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
        if (username.equals(names[0])) {
            return getCreatorOutput();
        }
        if (username.equals(names[1])) {
            return getOpponentOutput();
        }
        return "wrong username in get output";
    }
}
