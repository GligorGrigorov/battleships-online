import bg.uni.sofia.fmi.mjt.battleships.exceptions.GameCapacityExceededException;
import bg.uni.sofia.fmi.mjt.battleships.game.Game;
import bg.uni.sofia.fmi.mjt.battleships.game.Point;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameTest {

    public static final String CREATOR = "player1";
    public static final String OPPONENT = "player2";
    public static final String GAME_NAME = "testGame";

    private Game game;
    private Table table1;
    private Table table2;

    @Before
    public void setUp() {
        table1 = mock(Table.class);
        table2 = mock(Table.class);
        game = new Game(CREATOR, GAME_NAME);
    }

    @Test
    public void testGetNumberOfPlayers() {
        assertEquals(0, game.getNumberOfPlayers());
        game.addPlayer(CREATOR, table1);
        assertEquals(1, game.getNumberOfPlayers());
        game.addPlayer(OPPONENT, table2);
        assertEquals(2, game.getNumberOfPlayers());
    }

    @Test(expected = GameCapacityExceededException.class)
    public void testAddPlayerThrowsGameCapacityExceededExceptionWithMoreThanTwoPlayers() {
        game.addPlayer(CREATOR, table1);
        game.addPlayer(OPPONENT, table2);
        game.addPlayer(CREATOR, table1);
    }

    @Test
    public void testGetGameOutputWhenGameNotStarted() {
        String expected = "Game not in progress";
        assertEquals(expected, game.getOutput(CREATOR));
        game.addPlayer(CREATOR, table1);
        assertEquals(expected, game.getOutput(CREATOR));
        game.addPlayer(OPPONENT, table2);
        when(table1.toString(false)).thenReturn("t1");
        when(table2.toString(true)).thenReturn("t2");
        assertEquals(game.getOutput(CREATOR), "t1" + "t2");
    }

    @Test
    public void testSurrenderWithPendingGame() {
        game.addPlayer(CREATOR, table1);
        game.surrender(CREATOR);
        assertEquals(0, game.getNumberOfPlayers());
        assertEquals("Game not in progress", game.getOutput(CREATOR));
    }

    @Test
    public void testSurrenderWithGameInProgress() {
        game.addPlayer(CREATOR, table1);
        game.addPlayer(OPPONENT, table2);
        game.surrender(OPPONENT);
        assertEquals("WIN", game.getOutput(CREATOR));
    }

    @Test
    public void testAttackWhenNotEnoughPlayers() {
        game.addPlayer(CREATOR, table1);
        assertEquals("waiting for players", game.attack(CREATOR, new Point(1, 1)));
    }

    @Test
    public void testAttackWhenAttackingUserIsDefeated() {
        game.addPlayer(CREATOR, table1);
        game.addPlayer(OPPONENT, table2);
        when(table1.getShipCellsRemaining()).thenReturn(5);
        when(table2.getShipCellsRemaining()).thenReturn(0);
        String response = game.attack(OPPONENT, new Point(1, 1));
        assertEquals("DEFEAT", response);
    }

    @Test
    public void testAttackWhenAttackingUserIsWinner() {
        game.addPlayer(CREATOR, table1);
        game.addPlayer(OPPONENT, table2);
        when(table1.getShipCellsRemaining()).thenReturn(0);
        when(table2.getShipCellsRemaining()).thenReturn(5);
        String response = game.attack(OPPONENT, new Point(1, 1));
        assertEquals("WIN", response);
    }

    @Test
    public void testAttackWhenWinnerDoNotExist() {
        game.addPlayer(CREATOR, table1);
        game.addPlayer(OPPONENT, table2);
        when(table1.getShipCellsRemaining()).thenReturn(8);
        String creatorSide = "ad";
        when(table1.toString(false)).thenReturn("a");
        when(table1.toString(true)).thenReturn("b");
        when(table2.getShipCellsRemaining()).thenReturn(5);
        String opponentSide = "cb";
        when(table2.toString(false)).thenReturn("c");
        when(table2.toString(true)).thenReturn("d");
        String opponentResponse = game.attack(OPPONENT, new Point(1, 1));
        assertEquals(opponentSide, opponentResponse);
        String creatorResponse = game.attack(CREATOR, new Point(1, 1));
        assertEquals(creatorSide, creatorResponse);
    }

    @Test
    public void testAttackWhenNotYourTurn() {
        game.addPlayer(CREATOR, table1);
        game.addPlayer(OPPONENT, table2);
        String creatorResponse = game.attack(CREATOR, new Point(1, 1));
        assertEquals("not your turn", creatorResponse);
    }
}
