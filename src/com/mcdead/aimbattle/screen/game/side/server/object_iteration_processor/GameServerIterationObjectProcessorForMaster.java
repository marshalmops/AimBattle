package com.mcdead.aimbattle.screen.game.side.server.object_iteration_processor;

import com.mcdead.aimbattle.screen.game.objects.object.GameObject;

import java.util.List;

public interface GameServerIterationObjectProcessorForMaster extends GameServerIterationObjectProcessorForSlave {
    GameObject trySpawnNewObject();
    List<Integer> takeOutdatedObjectsIds();
}
