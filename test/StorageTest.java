import bg.uni.sofia.fmi.mjt.battleships.game.Game;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;
import bg.uni.sofia.fmi.mjt.battleships.storage.ServerStorage;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;
import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SocketChannel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StorageTest {

    private static final String USERNAME = "testName";

    private SocketChannel channel;
    private Storage storage;

    @Before
    public void setUp() {
        channel = mock(SocketChannel.class);
        storage = new ServerStorage();
    }

    @Test
    public void testIsRegisteredUser() {
        assertFalse(storage.isRegisteredUser(USERNAME));
        storage.registerUser(USERNAME);
        assertTrue(storage.isRegisteredUser(USERNAME));
    }

    @Test
    public void testIsLoggedInUser() {
        assertFalse(storage.isLoggedInUser(USERNAME));
        storage.logInUser(USERNAME,channel);
        assertTrue(storage.isLoggedInUser(USERNAME));
    }

    @Test
    public void testLeaveGameWithoutSaving() {
        Game game = mock(Game.class);
        String gameName = "testGame";
        storage.addGame(gameName,game);
        Table table = mock(Table.class);
        storage.joinAGame(USERNAME,gameName,table);
        String output = "testOutput";
        when(game.getOutput(USERNAME)).thenReturn(output);
        assertEquals(output,storage.leaveGameWithoutSaving(USERNAME));
    }
}
