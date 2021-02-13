import bg.uni.sofia.fmi.mjt.battleships.exceptions.GameCapacityExceededException;
import bg.uni.sofia.fmi.mjt.battleships.game.Game;
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
    public void setUp(){
        table1 = mock(Table.class);
        table2 = mock(Table.class);
        game = new Game(CREATOR,GAME_NAME);
    }
    @Test
    public void testGetNumberOfPlayers(){
        assertEquals(0, game.getNumberOfPlayers());
        game.addPlayer(CREATOR,table1);
        assertEquals(1,game.getNumberOfPlayers());
        game.addPlayer(OPPONENT, table2);
        assertEquals(2,game.getNumberOfPlayers());
    }

    @Test(expected = GameCapacityExceededException.class)
    public void testAddPlayerThrowsGameCapacityExceededExceptionWithMoreThanTwoPlayers() {
        game.addPlayer(CREATOR,table1);
        game.addPlayer(OPPONENT,table2);
        game.addPlayer(CREATOR,table1);
    }
    @Test
    public void testGetGameOutputWhenGameNotStarted(){
        String expected = "Game not in progress";
        assertEquals(expected, game.getOutput(CREATOR));
        game.addPlayer(CREATOR,table1);
        assertEquals(expected, game.getOutput(CREATOR));
        game.addPlayer(OPPONENT,table2);
        when(table1.toString(false)).thenReturn("t1");
        when(table2.toString(true)).thenReturn("t2");
        assertEquals(game.getOutput(CREATOR),"t1" + "t2");
    }

    @Test
    public void testSurrenderWithPendingGame(){
        game.addPlayer(CREATOR,table1);
        game.surrender(CREATOR);
        assertEquals(0,game.getNumberOfPlayers());
        assertEquals("Game not in progress",game.getOutput(CREATOR));
    }
    @Test
    public void testSurrenderWithGameInProgress() {
        game.addPlayer(CREATOR,table1);
        game.addPlayer(OPPONENT,table2);
        game.surrender(OPPONENT);
        assertEquals("WIN",game.getOutput(CREATOR));
    }
}
