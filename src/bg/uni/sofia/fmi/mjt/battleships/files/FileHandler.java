package bg.uni.sofia.fmi.mjt.battleships.files;

import bg.uni.sofia.fmi.mjt.battleships.game.Game;
import bg.uni.sofia.fmi.mjt.battleships.storage.Storage;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.newDirectoryStream;

public class FileHandler {

    private Path dirPath;
    private Storage storage;

    public FileHandler(Path dirPath, Storage storage) {
        this.dirPath = dirPath;
        this.storage = storage;

        if (Files.notExists(dirPath)) {
            try {
                Files.createDirectory(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("Problem occurred while creating directory", e);
            }
        }

        try (DirectoryStream<Path> directoryStream = newDirectoryStream(dirPath)) {
            for (Path filePath :
                    directoryStream) {
                String[] tokens = filePath.toString().split("\\.")[0].split("-");
                if (storage.isRegisteredUser(tokens[0]) && storage.isRegisteredUser(tokens[1])) {
                    storage.addSavedGame(tokens[0], tokens[2], filePath);
                    storage.addSavedGame(tokens[1], tokens[2], filePath);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from directory");
        }
    }

    public void saveGame(String username) {
        Game game = storage.getGameByName(storage.getCurrentGame(username));
        Path gamePath = Path.of(dirPath.toString(), game.getCreator() + "_" + game.getName() + ".bin");
        Path gamePath2 = Path.of(dirPath.toString(), game.getOpponent() + "_" + game.getName() + ".bin");
        storage.addSavedGame(game.getCreator(), game.getName(), gamePath);
        storage.addSavedGame(game.getOpponent(), game.getName(), gamePath2);
        try (var objectOutputStream = new ObjectOutputStream(Files.newOutputStream(gamePath))) {
            objectOutputStream.writeObject(game);
        } catch (IOException e) {
            System.err.println("Error saving this game from player " + username);
        }
        try (var objectOutputStream = new ObjectOutputStream(Files.newOutputStream(gamePath2))) {
            objectOutputStream.writeObject(game);
        } catch (IOException e) {
            System.err.println("Error saving this game from player " + username);
        }
    }

    public void loadGame(Path path) {
        try (var objectInputStream = new ObjectInputStream(Files.newInputStream(path))) {
            Object gameObject;
            while ((gameObject = objectInputStream.readObject()) != null) {
                Game game = (Game) gameObject;
                storage.addGame(game.getName(), game);
                Files.delete(path);
            }
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading this game");
        }
    }
}
