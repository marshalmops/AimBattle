package com.mcdead.aimbattle.screen.game.side.server.object_iteration_processor;

import com.mcdead.aimbattle.screen.game.objects.GameState;
import com.mcdead.aimbattle.screen.game.objects.object.Aim;
import com.mcdead.aimbattle.screen.game.objects.object.GameObject;
import com.mcdead.aimbattle.screen.game.objects.object.GameObjectGrowing;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class GameServerIterationObjectProcessor implements GameServerIterationObjectProcessorForMaster {
    private GameState m_gameStateRef;
    private int m_spawnTimeInTicks;
    private int m_growingTimeInTicks;
    private int m_tps;

    private long m_startSpawnTimePoint;
    private long m_startGrowingTimePoint;

    public GameServerIterationObjectProcessor(GameState gameStateRef, final int spawnTimeInTicks, final int tps, final int growingTimeInTicks) {
        m_gameStateRef = gameStateRef;
        m_spawnTimeInTicks = spawnTimeInTicks;
        m_growingTimeInTicks = growingTimeInTicks;
        m_tps = tps;

        m_startSpawnTimePoint = System.currentTimeMillis();
        m_startGrowingTimePoint = System.currentTimeMillis();
    }

    public GameObject trySpawnNewObject() {
        if (m_startSpawnTimePoint + m_spawnTimeInTicks * (1000 / m_tps) > System.currentTimeMillis())
            return null;

        int x = 0;
        int y = 0;

        Random rand = new Random(System.currentTimeMillis());
        Dimension mapSize = m_gameStateRef.getMap().getSize();

        GameObject obj = null;

        while (true) {
            x = rand.nextInt(0, mapSize.width - Aim.C_DEFAULT_SIZE);
            y = rand.nextInt(0, mapSize.height - Aim.C_DEFAULT_SIZE);

            if (m_gameStateRef.getMap().getObjectAtCoords(x, y) != null)
                continue;

            Logger.getGlobal().info("new obj at " + x + ':' + y);

            obj = new Aim(x, y);

            m_gameStateRef.getMap().addObject(obj);

            m_startSpawnTimePoint = System.currentTimeMillis();

            break;
        }

        return obj;
    }

    public List<Integer> takeOutdatedObjectsIds() {
        List<Integer> outdatedObjectIdList = new ArrayList<>();

        m_gameStateRef.getMap().getObjectList().forEach((obj) -> {
            if (obj.getCreationTime() + (1000 / m_tps) * obj.getLifespanInTicks() <= System.currentTimeMillis()) {
                //Logger.getGlobal().info("Obj to remove:" + obj.getId());

                outdatedObjectIdList.add(obj.getId());
            }

            return obj;
        });

        return outdatedObjectIdList;
    }

    public boolean processObjectsGrowing() {
        if (m_startGrowingTimePoint + m_growingTimeInTicks * (1000 / m_tps) > System.currentTimeMillis())
            return true;

        m_gameStateRef.getMap().getObjectList().forEach((obj) -> {
            if (!(obj instanceof GameObjectGrowing)) return obj;

            int growingValue = ((GameObjectGrowing) obj).getRandGrowSpeed();

            switch(obj.getGameShape().getType()) {
                case CIRCLE -> {
                    Ellipse2D.Float circleShape = (Ellipse2D.Float) obj.getGameShape().getShape();

                    circleShape.height += growingValue;
                    circleShape.width += growingValue;
                }
                default -> {}
            }

            return obj;
        });

        return true;
    }

}
