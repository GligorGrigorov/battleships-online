package bg.uni.sofia.fmi.mjt.battleships.commands;

public enum UserStatus {
    IN_GAME("type start to start:"),
    OFFLINE(""),
    PLAYING("game>"),
    IN_MAIN_MENU("main>");

    private final String prompt;

    UserStatus(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }
}
