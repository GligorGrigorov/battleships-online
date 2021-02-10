import bg.uni.sofia.fmi.mjt.battleships.commands.Command;
import bg.uni.sofia.fmi.mjt.battleships.commands.CommandExecutor;
import bg.uni.sofia.fmi.mjt.battleships.files.FileHandler;
import bg.uni.sofia.fmi.mjt.battleships.game.Board;
import bg.uni.sofia.fmi.mjt.battleships.game.Table;
import bg.uni.sofia.fmi.mjt.battleships.server.Pair;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CommandExecutorTest {

    private Storage storage;
    private SocketChannel channel;
    private CommandExecutor cmdExecutor;
    private Command command;

    @Before
    public void setUp() {
        storage = mock(Storage.class);
        command = mock(Command.class);
        try {
            channel = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cmdExecutor = new CommandExecutor(storage,new FileHandler(Path.of("test"), storage), new LinkedList<Pair>());
    }

    @Test
    public void CreateGameTest(){
        String username = "gligor";
        when(storage.isUserInGame(username)).thenReturn(false);
        when(storage.containsGameName("first-game")).thenReturn(false);
        when(storage.containsGame(any())).thenReturn(true);
        when(command.getUsername()).thenReturn("gligor");
        when(command.getArguments()).thenReturn(new String[]{"first-game", "A2,A3", "B1,B4", "B3,B8", "C4,C8", "D2,D4"});
        when(command.getName()).thenReturn("create-game");
        //assertEquals("game successfully created",cmdExecutor.executeCommand(command,channel));
    }

    @Test
    public void ListGamesTest(){
        String username = "gligor";
        when(storage.isUserInGame(username)).thenReturn(false);
        Set<Board> boards = new HashSet<>();
        Board b1 = mock(Board.class);
        when(b1.getCreator()).thenReturn(username);
        when(b1.getName()).thenReturn("test-game");
        boards.add(b1);
        when(storage.getGames()).thenReturn(boards);
        when(command.getUsername()).thenReturn("gligor");
        when(command.getArguments()).thenReturn(new String[0]);
        when(command.getName()).thenReturn("list-games");
       // assertEquals("game successfully created",cmdExecutor.executeCommand(command,channel));
    }

}
