package com.mcdead.aimbattle.event.error;

public enum GameErrorEventType implements ErrorEventType {
    ILLEGAL_STATE(), INCORRECT_ROLE(), INCORRECT_COMMAND_ORIGIN(),
    INCORRECT_PACKET_TYPE(), INCORRECT_TASK_TYPE(), INCORRECT_GAME_MODE(),
    INCORRECT_GAME_INPUT(), STATE_MODIFICATION_FAILED(), EXIT_TIMEOUT();
}
