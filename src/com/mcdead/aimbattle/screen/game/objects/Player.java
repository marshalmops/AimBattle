package com.mcdead.aimbattle.screen.game.objects;

import com.mcdead.aimbattle.utils.Copyable;

import java.util.UUID;

public class Player implements Copyable {
    private UUID m_uuid;
    private int m_score;

    public Player(final UUID uuid) {
        m_uuid = uuid;
        m_score = 0;
    }

    public Player(final Player other) {
        m_uuid = other.m_uuid;
        m_score = other.m_score;
    }

    public int getScore() {
        return m_score;
    }

    public UUID getUUID() {
        return m_uuid;
    }

    public boolean setScore(final int score) {
        m_score = score;

        return true;
    }

    @Override
    public Copyable copy() {
        return new Player(this);
    }
}
