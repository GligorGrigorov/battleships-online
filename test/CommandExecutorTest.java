import bg.uni.sofia.fmi.mjt.battleships.commands.*;
import bg.uni.sofia.fmi.mjt.battleships.files.FileHandler;
import bg.uni.sofia.fmi.mjt.battleships.game.Game;
import bg.uni.sofia.fmi.mjt.battleships.game.Status;
import bg.uni.sofia.fmi.mjt.battleships.server.Pair;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.nio.channels.SocketChannel;
import java.util.*;

import static org.junit.Assert.assertTrue;
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
    public void testLoginWithUniqueUsername(){
        when(command.getName()).thenReturn("login");
        when(command.getArguments()).thenReturn(new String[]{});
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.isRegisteredUser(USERNAME)).thenReturn(false);
        when(storage.isLoggedInUser(USERNAME)).thenReturn(false);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals(Message.SUCCESSFUL_LOGIN.toString(),response);
    }
    @Test
    public void testLoginWithAlreadyTakenUsername(){
        when(command.getName()).thenReturn("login");
        when(command.getArguments()).thenReturn(new String[]{});
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.isRegisteredUser(USERNAME)).thenReturn(true);
        when(storage.isLoggedInUser(USERNAME)).thenReturn(true);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals(Message.ALREADY_LOGGED_IN.toString(),response);
    }
    @Test
    public void testLogoutWhenNotInMainMenu(){
        when(command.getName()).thenReturn("logout");
        when(command.getArguments()).thenReturn(new String[]{});
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.PLAYING);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals(Message.WRONG_COMMAND.toString(),response);
    }
    @Test
    public void testLogoutWhenInMainMenu(){
        when(command.getName()).thenReturn("logout");
        when(command.getArguments()).thenReturn(new String[]{});
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals(Message.SUCCESSFUL_LOGOUT.toString(),response);
    }
    @Test
    public void testCreateGameWhenNotInMainMenu(){
        when(command.getName()).thenReturn("create-game");
        when(command.getArguments()).thenReturn(new String[11]);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_GAME);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals(Message.NOT_ALLOWED.toString(),response);
    }
    @Test
    public void testCreateGameWhenInMainMenuButGameExists(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[11];
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(any(String.class))).thenReturn(true);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals(Message.GAME_EXISTS.toString(),response);
    }

    @Test
    public void testCreateGameWithValidCoordinates(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[11];
        args[0] = "testGame";
        args[1] = "A1,E1";
        args[2] = "A3,D3";
        args[3] = "A5,D5";
        args[4] = "A7,C7";
        args[5] = "E7,G7";
        args[6] = "I4,I6";
        args[7] = "J10,J9";
        args[8] = "H10,G10";
        args[9] = "J1,J2";
        args[10] = "I2,I3";
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(args[0])).thenReturn(false);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals("Created game " + args[0] + ", players " + 0 + "/2",response);
    }
    @Test
    public void testCreateGameWithOverlapShips(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[11];
        args[0] = "testGame";
        args[1] = "A1,E1";
        args[2] = "A1,A4";
        args[3] = "A5,D5";
        args[4] = "A7,C7";
        args[5] = "E7,G7";
        args[6] = "I4,I6";
        args[7] = "J10,J9";
        args[8] = "H10,G10";
        args[9] = "J1,J2";
        args[10] = "I2,I3";
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(args[0])).thenReturn(false);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals("Ships can't overlap",response);
    }
    @Test
    public void testCreateGameWithWrongShipLength(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[11];
        args[0] = "testGame";
        args[1] = "A1,F1";
        args[2] = "A3,D3";
        args[3] = "A5,D5";
        args[4] = "A7,C7";
        args[5] = "E7,G7";
        args[6] = "I4,I6";
        args[7] = "J10,J9";
        args[8] = "H10,G10";
        args[9] = "J1,J2";
        args[10] = "I2,I3";
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(args[0])).thenReturn(false);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals("Ship with length 6 not available",response);
    }
    @Test
    public void testCreateGameWithInvalidCoordinates(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[11];
        args[0] = "testGame";
        args[1] = "Y1,C1";
        args[2] = "A3,D3";
        args[3] = "A5,D5";
        args[4] = "A7,C7";
        args[5] = "E7,G7";
        args[6] = "I4,I6";
        args[7] = "J10,J9";
        args[8] = "H10,G10";
        args[9] = "J1,J2";
        args[10] = "I2,I3";
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(args[0])).thenReturn(false);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals("Wrong ship coordinates",response);
    }

    @Test
    public void testCreateGameWithDiagonalShipPosition(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[11];
        args[0] = "testGame";
        args[1] = "A1,B6";
        args[2] = "A3,D3";
        args[3] = "A5,D5";
        args[4] = "A7,C7";
        args[5] = "E7,G7";
        args[6] = "I4,I6";
        args[7] = "J10,J9";
        args[8] = "H10,G10";
        args[9] = "J1,J2";
        args[10] = "I2,I3";
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(args[0])).thenReturn(false);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals("Ships can't go diagonal",response);
    }

    @Test
    public void testCreateGameWhenNoMoreShipsAvailableWithThisLength(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[11];
        args[0] = "testGame";
        args[1] = "A1,E1";
        args[2] = "A3,E3";
        args[3] = "A5,D5";
        args[4] = "A7,C7";
        args[5] = "E7,G7";
        args[6] = "I4,I6";
        args[7] = "J10,J9";
        args[8] = "H10,G10";
        args[9] = "J1,J2";
        args[10] = "I2,I3";
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(args[0])).thenReturn(false);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals("No more ships available of this type",response);
    }
    @Test
    public void testCreateGameWithMoreThanExpectedNumberOfShips(){
        when(command.getName()).thenReturn("create-game");
        String[] args = new String[12];
        args[0] = "testGame";
        args[1] = "A1,E1";
        args[2] = "A3,D3";
        args[3] = "A5,D5";
        args[4] = "A7,C7";
        args[5] = "E7,G7";
        args[6] = "I4,I6";
        args[7] = "J10,J9";
        args[8] = "H10,G10";
        args[9] = "J1,J2";
        args[10] = "I2,I3";
        args[10] = "I2,I3";
        when(command.getArguments()).thenReturn(args);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        when(storage.containsGameName(args[0])).thenReturn(false);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        assertEquals(Message.WRONG_COMMAND.toString(),response);
    }
    @Test
    public void testListGames() {
        when(command.getName()).thenReturn("list-games");
        when(command.getArguments()).thenReturn(new String[0]);
        when(command.getUsername()).thenReturn(USERNAME);
        when(storage.getUserStatus(USERNAME)).thenReturn(UserStatus.IN_MAIN_MENU);
        Game g1 = mock(Game.class);
        when(g1.getCreator()).thenReturn("g1creator");
        when(g1.getStatus()).thenReturn(Status.FINISHED);
        when(g1.getName()).thenReturn("g1");
        when(g1.getNumberOfPlayers()).thenReturn(0);
        Game g2 = mock(Game.class);
        when(g2.getCreator()).thenReturn("g2creator");
        when(g2.getStatus()).thenReturn(Status.PENDING);
        when(g2.getName()).thenReturn("g2");
        when(g2.getNumberOfPlayers()).thenReturn(1);
        Game g3 = mock(Game.class);
        when(g3.getCreator()).thenReturn("g3creator");
        when(g3.getStatus()).thenReturn(Status.IN_PROGRESS);
        when(g3.getName()).thenReturn("g3");
        when(g3.getNumberOfPlayers()).thenReturn(2);
        Set<Game> games = new HashSet<>();
        games.add(g1);
        games.add(g2);
        games.add(g3);
        when(storage.getGames()).thenReturn(games);
        cmdExecutor.executeCommand(command,channel);
        assertEquals(1,responses.size());
        Pair pair = responses.remove();
        String response = splitResponse(pair.response());
        List<String> expectedLines = new ArrayList<>();
        SocketChannel channel = pair.channel();
        assertEquals(this.channel,channel);
        expectedLines.add("| NAME              | CREATOR           | STATUS            | PLAYERS           ");
        expectedLines.add("| g3                | g3creator         | IN_PROGRESS       | 2                 ");
        expectedLines.add("| g2                | g2creator         | PENDING           | 1                 ");
        expectedLines.add("| g1                | g1creator         | FINISHED          | 0                 ");
        Set<String> returnedLines = new HashSet<>(Arrays.asList(pair.response().split(System.lineSeparator()).clone()));
        assertTrue(returnedLines.contains(expectedLines.get(0)));
        assertTrue(returnedLines.contains(expectedLines.get(1)));
        assertTrue(returnedLines.contains(expectedLines.get(2)));
        assertTrue(returnedLines.contains(expectedLines.get(3)));
    }
}
