package com.mcdead.aimbattle.screen.game.side.client;

import com.mcdead.aimbattle.command.game.*;
import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.MenuEvent;
import com.mcdead.aimbattle.event.error.GameErrorEventIllegalState;
import com.mcdead.aimbattle.screen.game.GameMode;
import com.mcdead.aimbattle.screen.game.GameRole;
import com.mcdead.aimbattle.screen.game.objects.GameState;
import com.mcdead.aimbattle.screen.game.objects.Player;
import com.mcdead.aimbattle.screen.game.processor.GameProcessorCallbackStoreClient;
import com.mcdead.aimbattle.screen.game.side.GameSide;
import com.mcdead.aimbattle.utils.ConcurrentList;
import com.mcdead.aimbattle.utils.SharedObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.BlockingQueue;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class GameClient extends GameSide {
    public static int C_FRAME_TIME = 16;
    public static int C_SCORE_BAR_HEIGHT = 40;

    private JComponent m_screen;
    private JLabel m_playersCount;

    public GameClient(BlockingQueue<Event> appEventQueueRef,
                      GameProcessorCallbackStoreClient callbackStore,
                      GameState gameState,
                      SharedObject<GameMode> gameModeRef,
                      SharedObject<GameRole> gameRoleRef,
                      JComponent screen)
    {
        super(appEventQueueRef, callbackStore, gameState, gameModeRef, gameRoleRef);

        m_screen = screen;
        m_playersCount = new JLabel();
    }

    @Override
    public boolean processCommand(GameCommand command) {
        if (command == null) return false;

        switch (command.getType()) {
            case CREATE -> onGameCreate();
            case CONNECT -> onGameConnect();
            case START -> onGameStart();
            case STOP -> onGameStop();
            case RESTART -> onGameRestart();
            case PAUSE -> onGamePause();
            case RESUME -> onGameResume();
            case CLOSE -> onGameClose();
            case EXIT -> onGameExit();
            default -> {return false;}
        }

        return false;
    }

    private void setLobbyLayout() {
        EventQueue.invokeLater(() -> {
            m_screen.removeAll();

            JPanel lobbyPanel = new JPanel();

            lobbyPanel.setLayout(new BoxLayout(lobbyPanel, BoxLayout.Y_AXIS));

            JPanel outputPanel = new JPanel();

            outputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

            JLabel playersCountLabel = new JLabel("Players: ");

            m_playersCount.setText("0");

            outputPanel.add(playersCountLabel);
            outputPanel.add(m_playersCount);

            JButton startButton = new JButton("Start");
            JButton backButton = new JButton("Back");

            if (m_gameRoleRef.getValue() == GameRole.MASTER) {
                startButton.addActionListener((e) -> {
                    startButton.setEnabled(false);
                    backButton.setEnabled(false);

                    m_callbackStore.processCommand(new GameCommandStart(GameCommand.Origin.LOCAL));
                });
            }

            backButton.addActionListener((e) -> {
                startButton.setEnabled(false);
                backButton.setEnabled(false);

                m_callbackStore.processCommand(new GameCommandClose(GameCommand.Origin.LOCAL));
                //m_appEventQueueRef.offer(MenuEvent.OPEN);
            });

            lobbyPanel.add(outputPanel);

            if (m_gameRoleRef.getValue() == GameRole.MASTER)
                lobbyPanel.add(startButton);

            lobbyPanel.add(backButton);

            m_screen.add(lobbyPanel);
            m_screen.revalidate();
            m_screen.repaint();
        });
    }

    private void onGameCreate() {
        setLobbyLayout();
        startClientThread();
    }

    private void onGameConnect() {
        setLobbyLayout();
        startClientThread();
    }

    private void onGameStart() {
        EventQueue.invokeLater(() -> {
            m_screen.removeAll();
            m_screen.repaint();
        });
    }

    private void onGameStop() {
        m_isSideCyclePaused.set(true);

        processGameEnd();
    }

    private void onGameRestart() {
        setLobbyLayout();

        startClientThread();
    }

    private void onGamePause() {
        if (m_gameRoleRef.getValue() == GameRole.MASTER) return;

        showPauseScreen();
    }

    private void onGameResume() {
        EventQueue.invokeLater(() -> {
            m_screen.removeAll();
            m_screen.repaint();
        });
    }

    private void onGameClose() {
        EventQueue.invokeLater(() -> {
            m_screen.removeAll();
            m_screen.repaint();
        });

        m_isSideCyclePaused.set(true);
    }

    private void onGameExit() {
        m_isSideCycleAlive.set(true);
    }

    @Override
    protected void processGameEnd() {
        ConcurrentList<Player> players = m_gameStateRef.getPlayerList();

        EventQueue.invokeLater(() -> {
            JPanel scoreBoardPanel = new JPanel();

            scoreBoardPanel.setLayout(new BoxLayout(scoreBoardPanel, BoxLayout.Y_AXIS));

            JLabel scoreBoardLabel = new JLabel("Score board:");

            scoreBoardLabel.setHorizontalAlignment(JLabel.CENTER);

            scoreBoardPanel.add(scoreBoardLabel);

            UnaryOperator<Player> showPlayerScore = new UnaryOperator<Player>() {
                @Override
                public Player apply(Player player) {
                    JLabel playerLabel = new JLabel(player.getUUID().toString());
                    JLabel playerScore = new JLabel(Integer.toString(player.getScore()));

                    JPanel playerInfo = new JPanel();

                    playerInfo.setLayout(new FlowLayout());

                    playerInfo.add(playerLabel);
                    playerInfo.add(playerScore);

                    scoreBoardPanel.add(playerInfo);

                    return player;
                }
            };

            showPlayerScore.apply(m_gameStateRef.getLocalPlayer().getValue());

            players.forEach(showPlayerScore);

            JButton restartButton = new JButton("Restart");
            JButton toMenuButton = new JButton("Go to Menu");

            if (m_gameRoleRef.getValue() == GameRole.MASTER) {
                restartButton.addActionListener((e) -> {
                    restartButton.setEnabled(false);
                    toMenuButton.setEnabled(false);

                    m_callbackStore.processCommand(new GameCommandRestart(GameCommand.Origin.LOCAL));
                });
            }

            toMenuButton.addActionListener((e) -> {
                restartButton.setEnabled(false);
                toMenuButton.setEnabled(false);

                m_callbackStore.processCommand(new GameCommandClose(GameCommand.Origin.LOCAL));
            });

            if (m_gameRoleRef.getValue() == GameRole.MASTER)
                scoreBoardPanel.add(restartButton);

            scoreBoardPanel.add(toMenuButton);

            m_screen.removeAll();
            m_screen.add(scoreBoardPanel);
            m_screen.repaint();
            m_screen.revalidate();
        });
    }

    private void startClientThread() {
        if (m_isSideCycleAlive.get()) {
            m_isSideCyclePaused.set(false);

            return;
        }

        m_isSideCycleAlive.set(true);
        m_isSideCyclePaused.set(false);

        new Thread(() -> {
            while (m_isSideCycleAlive.get()) {
                try {Thread.sleep(C_FRAME_TIME);} catch (Throwable e) {}

                if (m_isSideCyclePaused.get()) continue;

                switch (m_gameModeRef.getValue()) {
                    case WAITING_PLAYERS -> {
                        EventQueue.invokeLater(() -> {
                            m_playersCount.setText(Integer.toString(m_gameStateRef.getPlayerList().getSize()));
                        });
                    }
                    case PAUSED -> {}
                    case EXEC_GAME -> showNewFrame();
                    case STOP -> {}
                    default -> {
                        m_isSideCyclePaused.set(true);

                        if (!m_appEventQueueRef.offer((Event) new GameErrorEventIllegalState()))
                            m_callbackStore.processCommand(new GameCommandStop(GameCommand.Origin.LOCAL));
                    }
                }
            }

        }).start();
    }

    private void showPauseScreen() {
        EventQueue.invokeLater(() -> {
            JPanel pausePanel = new JPanel();

            pausePanel.setLayout(new BoxLayout(pausePanel, BoxLayout.Y_AXIS));

            JLabel pauseLabel = new JLabel("PAUSE.");
            JButton toMenuButton = new JButton("Go to Menu");

            pauseLabel.setFont(new Font("Roboto", Font.BOLD, 24));

            toMenuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    m_callbackStore.processCommand(new GameCommandClose(GameCommand.Origin.LOCAL));
                    //m_appEventQueueRef.offer(MenuEvent.OPEN);
                }
            });

            pausePanel.add(pauseLabel);
            pausePanel.add(toMenuButton);

            m_screen.removeAll();
            m_screen.add(pausePanel);
            m_screen.repaint();
            m_screen.revalidate();
        });
    }

    private void showNewFrame() {
        EventQueue.invokeLater(() -> {
            Dimension mapSize = m_gameStateRef.getMap().getSize();
            BufferedImage frame = new BufferedImage(mapSize.width, mapSize.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D frameGraphics = frame.createGraphics();

            frameGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            frameGraphics.setBackground(Color.WHITE);
            frameGraphics.clearRect(0,0, mapSize.width, mapSize.height);

            // drawing objects:

            m_gameStateRef.getMap().getObjectList().forEach((obj) -> {
                frameGraphics.setColor(obj.getColor());
                frameGraphics.fill(obj.getGameShape().getShape());

                return obj;
            });

            Graphics2D graphics2D = (Graphics2D) m_screen.getGraphics();

            graphics2D.setColor(Color.GRAY);
            graphics2D.fillRect(0, 0, m_screen.getWidth(), C_SCORE_BAR_HEIGHT);
            graphics2D.setColor(Color.WHITE);

            Font scoreFont = new Font("Roboto", Font.PLAIN, 14);
            String scoreString = "Score: " + m_gameStateRef.getLocalPlayer().getValue().getScore();

            FontMetrics metrics = graphics2D.getFontMetrics(scoreFont);
            Rectangle2D scoreTextBounds = metrics.getStringBounds(scoreString, graphics2D);

            graphics2D.setFont(scoreFont);
            graphics2D.drawString(scoreString, (float) 10, (float) (C_SCORE_BAR_HEIGHT / 2 + scoreTextBounds.getHeight() / 2));

            graphics2D.drawImage(frame,0, C_SCORE_BAR_HEIGHT, m_screen.getWidth(), m_screen.getHeight() - C_SCORE_BAR_HEIGHT, null);
        });
    }
}
