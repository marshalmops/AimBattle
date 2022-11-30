package com.mcdead.aimbattle.screen.game.side.server.task;

import java.util.UUID;

public class GameRemovePlayerTask extends Task implements GameTaskInterface {
    private UUID m_uuid;

    public GameRemovePlayerTask(final UUID uuid) {
        m_uuid = uuid;
    }

    public UUID getUUID() {
        return m_uuid;
    }

    @Override
    public TaskType getType() {
        return GameTaskType.REMOVE_PLAYER;
    }
}
