package com.mcdead.aimbattle.screen.game.side.server.task;

public enum GameTaskType implements TaskType {
    ADD_PLAYER(), // Server;
    REMOVE_PLAYER(), // Server;
    PROCESS_PLAYER_INPUT(),
    PROCESS_PACKET(); // Client / Server;
}
