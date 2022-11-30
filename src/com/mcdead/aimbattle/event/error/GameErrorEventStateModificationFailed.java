package com.mcdead.aimbattle.event.error;

public class GameErrorEventStateModificationFailed extends GameErrorEvent {
    public GameErrorEventStateModificationFailed() {
        super("Game state modification failed!", true);
    }

    @Override
    public ErrorEventType getType() {
        return GameErrorEventType.STATE_MODIFICATION_FAILED;
    }
}
