package com.mcdead.aimbattle.screen.game.side.server.packet;

import com.mcdead.aimbattle.screen.game.objects.object.GameObject;
import com.mcdead.aimbattle.screen.game.objects.object.GameObjectFromBytesFactory;

import java.nio.ByteBuffer;

public abstract class GameNetworkPacketGameObjectActionSolid extends GameNetworkPacketGameObjectAction {
    private GameObject m_gameObject;

    public GameNetworkPacketGameObjectActionSolid(final GameNetworkPacketType type) {
        super(type);

        m_gameObject = null;
    }

    public GameNetworkPacketGameObjectActionSolid(final GameNetworkPacketType type, final GameObject obj) {
        super(type);

        m_gameObject = (GameObject) obj.copy();
    }

    public GameObject getGameObject() {
        return m_gameObject;
    }

    @Override
    public byte[] toBytes() {
        byte[] rawSuperBytes = super.toBytes();

        byte[] rawGameObjBytes = m_gameObject.toBytes();

        byte[] rawBytes = new byte[rawSuperBytes.length + rawGameObjBytes.length];

        System.arraycopy(rawSuperBytes, 0, rawBytes, 0, rawSuperBytes.length);
        System.arraycopy(rawGameObjBytes, 0, rawBytes, rawSuperBytes.length, rawGameObjBytes.length);

        return rawBytes;
    }

    @Override
    public boolean fromBytes(final ByteBuffer byteBuffer) {
        if (!super.fromBytes(byteBuffer)) return false;

        m_gameObject = GameObjectFromBytesFactory.produceGameObjectWithBytes(byteBuffer);//m_gameObject.fromBytes(byteBuffer);

        return (m_gameObject != null);
    }
}
