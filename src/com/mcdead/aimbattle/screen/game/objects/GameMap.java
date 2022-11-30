package com.mcdead.aimbattle.screen.game.objects;

import com.mcdead.aimbattle.screen.game.objects.object.GameObject;
import com.mcdead.aimbattle.utils.ConcurrentList;

import java.awt.*;

public class GameMap {
    public static int C_DEFAULT_MAP_WIDTH = 640;
    public static int C_DEFAULT_MAP_HEIGHT = 480;
    private ConcurrentList<GameObject> m_objects;
    private Dimension m_size;

    public GameMap() {
        m_objects = new ConcurrentList<>();
        m_size = new Dimension(C_DEFAULT_MAP_WIDTH, C_DEFAULT_MAP_HEIGHT);
    }

    public GameMap(final int width, final int height) {
        m_objects = new ConcurrentList<>();
        m_size = new Dimension(width, height);
    }

    public boolean changeSize(final Dimension size) {
        if (size == null) return false;
        if (size.height <= 0 || size.width <= 0) return false;

        m_size = size;

        return true;
    }

    public void clearObjects() {
        m_objects = new ConcurrentList<>();
    }

    public Dimension getSize() {
        return m_size;
    }

    public boolean addObject(final GameObject object) {
        if (object == null) return false;

        m_objects.addItem(object);

        return true;
    }

    public boolean removeObjectById(final int objectId) {
        if (objectId < 0) return false;

        return m_objects.removeOneIf((obj) -> obj.getId() == objectId);
    }

    public boolean modifyObject(final GameObject object) {
        if (object == null) return false;

        return m_objects.forFirstIf(
                    (obj) -> obj.getId() == object.getId(),
                    (obj) -> object);
    }

    public GameObject getObjectAtCoords(final int x, final int y) {
        return m_objects.getValueIf((obj) -> obj.getGameShape().getShape().contains(x , y));
    }

    public ConcurrentList<GameObject> getObjectList() {
        return m_objects;
    }
}
