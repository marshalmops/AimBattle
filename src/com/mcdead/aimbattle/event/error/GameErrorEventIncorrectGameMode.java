package com.mcdead.aimbattle.event.error;

public class GameErrorEventIncorrectGameMode extends GameErrorEvent {
    public GameErrorEventIncorrectGameMode() {
        super("Incorrect game mode has been occurred!", true);
    }

    @Override
    public ErrorEventType getType() {
        return GameErrorEventType.INCORRECT_GAME_MODE;
    }
}
