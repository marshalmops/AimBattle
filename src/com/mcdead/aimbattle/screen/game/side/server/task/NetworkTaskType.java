package com.mcdead.aimbattle.screen.game.side.server.task;

public enum NetworkTaskType implements TaskType {
    INIT_CLIENT(), START_PROCESSING(), STOP_PROCESSING(),
    SEND_PACKET(), REMOVE_CLIENT(),
    RESTART_GAME(), CLOSE(), EXIT();
}
