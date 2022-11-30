package com.mcdead.aimbattle.screen.game.objects.object;

import com.mcdead.aimbattle.screen.game.objects.shape.GameShapeCircle;
import com.mcdead.aimbattle.utils.Copyable;

import java.awt.*;

public class Aim extends GameObjectGrowing implements RewardableInterface {
    public static int C_DEFAULT_SIZE = 20;
    private static int C_DEFAULT_MIN_GROW_SPEED = 1;
    private static int C_DEFAULT_MAX_GROW_SPEED = 2;
    private static Color C_DEFAULT_COLOR = Color.RED;
    private static int C_REWARD = 1;

    public Aim() {
        super(GameObjectType.AIM);
    }

    public Aim(int x, int y) {
        super(GameObjectType.AIM,
                C_DEFAULT_COLOR,
                new GameShapeCircle(x, y, C_DEFAULT_SIZE),
                C_DEFAULT_MIN_GROW_SPEED,
                C_DEFAULT_MAX_GROW_SPEED,
                C_DEFAULT_LIFESPAN_IN_TICKS);

    }

    public Aim(int x, int y, Color color) {
        super(GameObjectType.AIM,
                color,
                new GameShapeCircle(x, y, C_DEFAULT_SIZE),
                C_DEFAULT_MIN_GROW_SPEED,
                C_DEFAULT_MAX_GROW_SPEED,
                C_DEFAULT_LIFESPAN_IN_TICKS);

    }

    public Aim(final Aim other) {
        super(other);
    }

    @Override
    public GameShapeCircle getGameShape() {
        return (GameShapeCircle) super.getGameShape();
    }

    @Override
    public int getReward() {
        return C_REWARD;
    }

    @Override
    public Copyable copy() {
        return new Aim(this);
    }
}
