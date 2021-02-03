package bg.uni.sofia.fmi.mjt.battleships.game;

import java.util.List;

public class Board {
    private final Table[] tables;
    private final String creator;
    private String opponent;
    private final String gameName;
    private int numberOfPlayers;
    public Board(String creator, String gameName, Table creatorTable){
        this.tables = new Table[2];
        this.tables[0] = creatorTable;
        this.creator = creator;
        this.gameName = gameName;
        this.numberOfPlayers = 1;
    }

    public int getNumberOfPlayers(){
        return numberOfPlayers;
    }
    public void addOpponentTable(String opponent, Table opponentTable){
        this.tables[1] = opponentTable;
        this.opponent = opponent;
        numberOfPlayers++;
    }
    public String getCreator() {
        return creator;
    }

    public String getName() {
        return gameName;
    }

    public String getCreatorOutput() {
        return "creator side";
    }

    public String getOpponentOutput(){
        return "opponent side";
    }
}
