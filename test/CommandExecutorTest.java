import bg.uni.sofia.fmi.mjt.battleships.commands.*;
import bg.uni.sofia.fmi.mjt.battleships.files.FileHandler;
import bg.uni.sofia.fmi.mjt.battleships.game.Game;
import bg.uni.sofia.fmi.mjt.battleships.server.Pair;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CommandExecutorTest {

    private static final String USERNAME = "test123";

    private Storage storage;
    private SocketChannel channel;
    private Executor cmdExecutor;
    private Command command;
    private FileHandler handler;
    private Queue<Pair> responses;

    private String splitResponse(String input) {
        return input.split(System.lineSeparator())[0];
    }
    @Before
    public void setUp() {
        storage = mock(Storage.class);
        command = mock(Command.class);
        handler = mock(FileHandler.class);
        channel = mock(SocketChannel.class);
        responses = new LinkedList<>();
        cmdExecutor = new CommandExecutor(storage,handler, responses);
    }

    @Test
    public void testLogin(){
        String request = "login " + USERNAME;
        when(command.getName()).thenReturn("login");
        when(command.getArguments()).thenReturn(new String[]{});
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.isRegisteredUser(USERNAME)).thenReturn(false);
        when(storage.isLoggedInUser(USERNAME)).thenReturn(false);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        cmdExecutor.executeCommand(command,channel);
        String response = splitResponse(responses.peek().response().toString());
        SocketChannel channel = responses.peek().channel();
        Assert.assertEquals(this.channel,channel);
        Assert.assertEquals(Message.SUCCESSFUL_LOGIN.toString(),response);
    }
}
