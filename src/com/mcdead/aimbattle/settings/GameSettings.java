package com.mcdead.aimbattle.settings;

public class GameSettings implements GameSettingsModifiable {
    private static GameSettings m_instance;

    private GameSettings() {

    }

    public static boolean createInstance() {
        if (m_instance != null) return false;

        m_instance = new GameSettings();

        return true;
    }

    public static GameSettingsReadable getInstance() {
        return m_instance;
    }
}
