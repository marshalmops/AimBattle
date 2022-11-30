package com.mcdead.aimbattle.event.error;

public class GameErrorEventIncorrectTaskType extends GameErrorEvent {
    public GameErrorEventIncorrectTaskType() {
        super("Incorrect task type is occurred!", true);
    }

    @Override
    public ErrorEventType getType() {
        return GameErrorEventType.INCORRECT_TASK_TYPE;
    }
}
