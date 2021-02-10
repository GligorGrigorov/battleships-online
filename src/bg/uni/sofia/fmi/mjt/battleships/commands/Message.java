package bg.uni.sofia.fmi.mjt.battleships.commands;

public enum Message {

    ALREADY_REGISTERED("[ user already registered ]"),
    ALREADY_LOGGED_IN("[ user already logged in ]"),
    SUCCESSFUL_LOGIN("[ successfully logged in ]"),
    SUCCESSFUL_LOGOUT("[ successfully logged out ]"),
    NOT_ALLOWED("[ not allowed command ]"),
    GAME_EXISTS("[ game exists ]"),
    GAME_DO_NOT_EXIST("[ game don't exist ]"),
    GAME_SUCCESSFULLY_CREATED("[ game successfully created ]"),
    GAME_PROBLEM_IN_CREATION("[ problem creating this game ]"),
    GAME_LEFT("[ successfully left the game ]"),
    WRONG_COMMAND("[ wrong command ]"),
    NOT_LOGGED_IN("[ not logged in ]");
    private final String message;

    Message(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
