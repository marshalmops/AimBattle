package com.mcdead.aimbattle.screen.game.objects;

import com.mcdead.aimbattle.utils.ConcurrentList;
import com.mcdead.aimbattle.utils.SharedObject;

import java.util.UUID;

public class GameState {
    private SharedObject<Player> m_localPlayer;
    private ConcurrentList<Player> m_players;
    private GameMap m_map;

    public GameState() {
        m_localPlayer = null;
        m_players = new ConcurrentList<>();
        m_map = new GameMap();
    }

    public boolean setLocalPlayer(final Player localPlayer) {
        if (localPlayer == null) return false;

        m_localPlayer = new SharedObject<>(localPlayer);

        return true;
    }

    public Player getPlayerByUUID(final UUID uuid) {
        return m_players.getValueIf((player) -> player.getUUID() == uuid);
    }

    public boolean addPlayer(final Player player) {
        if (player == null) return false;

        m_players.addItem(player);

        return true;
    }

    public boolean removePlayerByUUID(final UUID uuid) {
        return m_players.removeOneIf((player) -> player.getUUID() == uuid);
    }

    public SharedObject<Player> getLocalPlayer() {
        return m_localPlayer;
    }

    public ConcurrentList<Player> getPlayerList() {
        return m_players;
    }

    public GameMap getMap() {
        return m_map;
    }

    public void clearMap() {
        m_map = new GameMap();
    }

    public void clearPlayerScore() {
        m_players.forEach((player) -> {
            player.setScore(0);

            return player;
        });

        m_localPlayer.modifyValue((player) -> {
            player.setScore(0);

            return player;
        });
    }

    public void clearPlayerList() {
        m_players = new ConcurrentList<>();
    }
}
