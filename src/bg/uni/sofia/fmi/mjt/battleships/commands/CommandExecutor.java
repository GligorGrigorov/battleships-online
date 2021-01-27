package bg.uni.sofia.fmi.mjt.battleships.commands;

import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;

import java.nio.channels.SocketChannel;

public class CommandExecutor implements Executor {
    private final Storage storage;
    public CommandExecutor(Storage storage) {
        this.storage = storage;
    }

    @Override
    public String executeCommand(Command command, SocketChannel channel) {
        if (!Commands.containsCommand(command)){
            return "wrong command";
        }
        switch (command.getName()){
            case "login":
                if(storage.isRegisteredUser(command.getArgument())){
                    return "user already exist";
                } else {
                    storage.registerUser(command.getUsername());
                }
                if(storage.isLoggedInUser(command.getUsername())){
                    return "user already logged in";
                }
                storage.logInUser(command.getUsername(),channel);
                return "successfully logged in";
        }
        return null;
    }
}
