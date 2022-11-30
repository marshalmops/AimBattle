package com.mcdead.aimbattle.screen.game.side.server.packet;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class GameNetworkPacketFromBytesFactory {
    public static GameNetworkPacket producePacketWithBytes(final byte[] bytes) {
        if (bytes.length < GameNetworkPacketType.C_BYTES_COUNT)
            return null;

        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        return producePacketWithBytes(buffer);
    }

    public static GameNetworkPacket producePacketWithBytes(final ByteBuffer byteBuffer) {
        if (byteBuffer.remaining() < GameNetworkPacketType.C_BYTES_COUNT)
            return null;

        GameNetworkPacketType type = GameNetworkPacketType.getTypeById(byteBuffer.get());
        GameNetworkPacket packet = null;

        Logger.getGlobal().info(type.name());

        switch (type) {
            case INIT -> {packet = produceEmptyPacketInitWithBytes();}
            case ADD_PLAYER -> {packet = produceEmptyPacketAddPlayerWithBytes();}
            case REMOVE_PLAYER -> {packet = produceEmptyPacketRemovePlayerWithBytes();}
            case START -> {packet = produceEmptyPacketStartWithBytes();}
            case END -> {packet = produceEmptyPacketEndWithBytes();}
            case SPAWN_OBJECT -> {packet = produceEmptyPacketSpawnObjectWithBytes();}
            case MODIFY_OBJECT -> {packet = produceEmptyPacketModifyObjectWithBytes();}
            case CLOSE -> {packet = produceEmptyPacketCloseWithBytes();}
            case RESTART -> {packet = produceEmptyPacketRestartWithBytes();}
            case SET_PLAYER_SCORE -> {packet = produceEmptyPacketSetPlayerScoreWithBytes();}
            case REMOVE_OBJECT -> {packet = produceEmptyPacketRemoveObjectWithBytes();}
            case PLAYER_INPUT -> {packet = produceEmptyPacketPlayerInputWithBytes();}
            case LEAVE -> {packet = produceEmptyPacketLeaveWithBytes();}
            case PAUSE -> {packet = produceEmptyPacketPauseWithBytes();}
            case RESUME -> {packet = produceEmptyPacketResumeWithBytes();}
        }

        return packet.fromBytes(byteBuffer) ? packet : null;
    }

    private static GameNetworkPacketPlayerAdd produceEmptyPacketAddPlayerWithBytes() {
        return new GameNetworkPacketPlayerAdd();
    }

    private static GameNetworkPacketInit produceEmptyPacketInitWithBytes() {
        return new GameNetworkPacketInit();
    }

    private static GameNetworkPacketPlayerRemove produceEmptyPacketRemovePlayerWithBytes() {
        return new GameNetworkPacketPlayerRemove();
    }

    private static GameNetworkPacketStart produceEmptyPacketStartWithBytes() {
        return new GameNetworkPacketStart();
    }

    private static GameNetworkPacketEnd produceEmptyPacketEndWithBytes() {
        return new GameNetworkPacketEnd();
    }

    private static GameNetworkPacketGameObjectActionSolidSpawn produceEmptyPacketSpawnObjectWithBytes() {
        return new GameNetworkPacketGameObjectActionSolidSpawn();
    }

    private static GameNetworkPacketGameObjectActionSolidModify produceEmptyPacketModifyObjectWithBytes() {
        return new GameNetworkPacketGameObjectActionSolidModify();
    }

    private static GameNetworkPacketClose produceEmptyPacketCloseWithBytes() {
        return new GameNetworkPacketClose();
    }

    private static GameNetworkPacketRestart produceEmptyPacketRestartWithBytes() {
        return new GameNetworkPacketRestart();
    }

    private static GameNetworkPacketPlayerModifySetScore produceEmptyPacketSetPlayerScoreWithBytes() {
        return new GameNetworkPacketPlayerModifySetScore();
    }

    private static GameNetworkPacketGameObjectActionFastRemove produceEmptyPacketRemoveObjectWithBytes() {
        return new GameNetworkPacketGameObjectActionFastRemove();
    }

    private static GameNetworkPacketPlayerInput produceEmptyPacketPlayerInputWithBytes() {
        return new GameNetworkPacketPlayerInput();
    }

    private static GameNetworkPacketLeave produceEmptyPacketLeaveWithBytes() {
        return new GameNetworkPacketLeave();
    }

    private static GameNetworkPacketResume produceEmptyPacketResumeWithBytes() {
        return new GameNetworkPacketResume();
    }

    private static GameNetworkPacketPause produceEmptyPacketPauseWithBytes() {
        return new GameNetworkPacketPause();
    }
}
