package com.mcdead.aimbattle.screen.game.side.server;

import com.mcdead.aimbattle.GameContext;
import com.mcdead.aimbattle.command.game.*;
import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.error.*;
import com.mcdead.aimbattle.screen.game.input.GameInput;
import com.mcdead.aimbattle.screen.game.GameMode;
import com.mcdead.aimbattle.screen.game.GameRole;
import com.mcdead.aimbattle.screen.game.input.GameInputMouseClick;
import com.mcdead.aimbattle.screen.game.objects.GameState;
import com.mcdead.aimbattle.screen.game.objects.Player;
import com.mcdead.aimbattle.screen.game.objects.object.GameObject;
import com.mcdead.aimbattle.screen.game.objects.object.RewardableInterface;
import com.mcdead.aimbattle.screen.game.processor.GameProcessorCallbackStoreServer;
import com.mcdead.aimbattle.screen.game.side.GameSide;
import com.mcdead.aimbattle.screen.game.side.server.object_iteration_processor.GameServerIterationObjectProcessor;
import com.mcdead.aimbattle.screen.game.side.server.object_iteration_processor.GameServerIterationObjectProcessorForMaster;
import com.mcdead.aimbattle.screen.game.side.server.object_iteration_processor.GameServerIterationObjectProcessorForSlave;
import com.mcdead.aimbattle.screen.game.side.server.packet.*;
import com.mcdead.aimbattle.screen.game.side.server.task.*;
import com.mcdead.aimbattle.utils.SharedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class GameServer extends GameSide {
    public static int C_DEFAULT_TPS = 25;
    public static int C_GAME_TIME = 60000;
    public static int C_DEFAULT_SPAWN_TIME_IN_TICKS = 70;
    public static int C_DEFAULT_GROWING_TIME_IN_TICKS = 16;

    private BlockingQueue<GameInput> m_gameInputQueue;
    private GameServerTaskStore m_gameServerTaskStore;
    private GameServerNetworkProcessor m_networkProcessor;

    private int m_tps;
    private long m_gameStartTimePoint;

    public GameServer(BlockingQueue<Event> appEventQueueRef,
                      GameProcessorCallbackStoreServer callbackStore,
                      GameState gameState,
                      SharedObject<GameMode> gameModeRef,
                      SharedObject<GameRole> gameRoleRef)
    {
        super(appEventQueueRef, callbackStore, gameState, gameModeRef, gameRoleRef);

        m_gameInputQueue = new LinkedBlockingQueue<>();
        m_gameServerTaskStore = new GameServerTaskStore();
        m_networkProcessor = new GameServerNetworkProcessor(gameModeRef, m_gameServerTaskStore, m_appEventQueueRef);
        m_tps = C_DEFAULT_TPS;
        m_gameStartTimePoint = 0;
    }

    private void startServerThread() {
        if (m_isSideCycleAlive.get()) {
            m_isSideCyclePaused.set(false);

            return;
        }

        m_isSideCycleAlive.set(true);
        m_isSideCyclePaused.set(false);

        new Thread(() -> {
            GameServerIterationObjectProcessor objectProcessor = new GameServerIterationObjectProcessor(
                    m_gameStateRef,
                    C_DEFAULT_SPAWN_TIME_IN_TICKS,
                    m_tps,
                    C_DEFAULT_GROWING_TIME_IN_TICKS);

            while (m_isSideCycleAlive.get()) {
                try {Thread.sleep(1000 / m_tps);} catch (Throwable e) {e.printStackTrace();}

                if (m_isSideCyclePaused.get()) continue;

                checkTaskStore();

                switch (m_gameModeRef.getValue()) {
                    case INIT:
                    case PAUSED:
                    case STOP:
                    case WAITING_PLAYERS: {continue;}
                    case EXEC_GAME:       {gameIteration(objectProcessor); break;}
                    default:              {processError(new GameErrorEventIncorrectGameMode());}
                }

                processInput();
            }

            stopNetworkProcessor();

        }).start();
    }

    private void stopNetworkProcessor() {
        m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkExitTask());

        final long startTimePoint = System.currentTimeMillis();

        while (m_networkProcessor.isAlive()) {
            if (startTimePoint + GameContext.C_THREAD_EXIT_TIMEOUT >= System.currentTimeMillis()) {
                m_appEventQueueRef.offer(new NetworkErrorEventExitTimeout());

                break;
            }
        }
    }

    @Override
    public boolean processCommand(final GameCommand command) {
        if (command == null) return false;

        boolean result = true;

        switch (command.getType()) {
            case CREATE -> {result = onGameCreate((GameCommandCreate) command);}
            case START -> onGameStart((GameCommandStart) command);
            case CONNECT -> {result = onGameConnect((GameCommandConnect) command);}
            case STOP -> onGameStop((GameCommandStop) command);
            case RESTART -> onGameRestart((GameCommandRestart) command);
            case RESUME -> onGameResume((GameCommandResume) command);
            case PAUSE -> onGamePause((GameCommandPause) command);
            case CLOSE -> onGameClose((GameCommandClose) command);
            case EXIT -> onGameExit((GameCommandExit) command);
            default -> {return false;}
        }

        return result;
    }

    public void addGameInputToProcess(final GameInput input) {
        m_gameInputQueue.offer(input);
    }

    private void gameIteration(final GameServerIterationObjectProcessorForSlave objectProcessor) {
        if (m_gameRoleRef.getValue() == GameRole.MASTER) {
            if (m_gameStartTimePoint + C_GAME_TIME <= System.currentTimeMillis()) {
                m_callbackStore.processCommand(new GameCommandStop(GameCommand.Origin.LOCAL));

                return;
            }

            GameServerIterationObjectProcessorForMaster objectProcessorForMaster =
                    (GameServerIterationObjectProcessorForMaster) objectProcessor;

            GameObject spawnedObj = objectProcessorForMaster.trySpawnNewObject();

            if (spawnedObj != null) {
                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkSendPacketTask(
                        new GameNetworkPacketGameObjectActionSolidSpawn(spawnedObj),
                        PacketDestinationType.BROADCAST));
            }

//            if (!objectProcessorForMaster.trySpawnNewObject()) {
//                processError(new GameErrorEventIllegalState());
//
//                return;
//            }

            List<Integer> outdatedObjectIdList = objectProcessorForMaster.takeOutdatedObjectsIds();

            for (final Integer objectId : outdatedObjectIdList) {
                if (!m_gameStateRef.getMap().removeObjectById(objectId)) {
                    processError(new GameErrorEventStateModificationFailed());

                    return;
                }

                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkSendPacketTask(
                        new GameNetworkPacketGameObjectActionFastRemove(objectId),
                        PacketDestinationType.BROADCAST));
            }
        }

        if (!objectProcessor.processObjectsGrowing()) {
            processError(new GameErrorEventIllegalState());

            return;
        }
    }

    private void checkTaskStore() {
        GameTaskInterface task = null;

        while ((task = m_gameServerTaskStore.getGameTaskQueue().poll()) != null) {
            processTask((Task) task);
        }
    }

    private void processTask(final Task task) {
        if (task == null) return;

        switch ((GameTaskType)task.getType()) {
            case ADD_PLAYER -> {processAddPlayerTask((GameAddPlayerTask) task);}
            case REMOVE_PLAYER -> {processRemovePlayerTask((GameRemovePlayerTask) task);}
            case PROCESS_PACKET -> {processProcessPacketTask((GameProcessPacketTask) task);}
            case PROCESS_PLAYER_INPUT -> {processPlayerInput((GameProcessPlayerInputTask) task);}
            default -> processError(new GameErrorEventIncorrectTaskType());
        }
    }

    private void processAddPlayerTask(final GameAddPlayerTask task) {
        if (m_gameRoleRef.getValue() != GameRole.MASTER) return;

        UUID newPlayerUUID = UUID.randomUUID();
//        List<UUID> players = new ArrayList<>();
//
//        m_gameStateRef.getPlayerList().forEach((player) -> {
//            players.add(player.getUUID());
//
//            return player;
//        });

        GameNetworkPacketPlayerAdd addPlayerPacket = new GameNetworkPacketPlayerAdd(newPlayerUUID);

        m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkSendPacketTask(addPlayerPacket,
                PacketDestinationType.BROADCAST));
        m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkInitClientTask(task.getSocket(),
                newPlayerUUID,
                new GameNetworkPacketInit(m_tps, m_gameStateRef.getMap().getSize(), newPlayerUUID)));

        // sending ALL the AddPlayer packets for new client:

        ArrayList<UUID> dstList = new ArrayList<>(){{add(newPlayerUUID);}};

        m_gameServerTaskStore.getNetTaskQueue().offer(
            new NetworkSendPacketTask(
                new GameNetworkPacketPlayerAdd(
                    m_gameStateRef.getLocalPlayer().getValue().getUUID()),
                    dstList));

        m_gameStateRef.getPlayerList().forEach((player) -> {
            m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkSendPacketTask(
                    new GameNetworkPacketPlayerAdd(player.getUUID()),
                    dstList));

            return player;
        });

        processAddPlayerPacket(addPlayerPacket);
    }

    private void processRemovePlayerTask(final GameRemovePlayerTask task) {
        if (m_gameRoleRef.getValue() != GameRole.MASTER) return;
        if (m_gameStateRef.getPlayerByUUID(task.getUUID()) == null) return;

        GameNetworkPacketPlayerRemove removePlayerPacket = new GameNetworkPacketPlayerRemove(task.getUUID());

        m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkRemoveClientTask(task.getUUID(),
                removePlayerPacket));

        processRemovePlayerPacket(removePlayerPacket);
    }

    private void processProcessPacketTask(final GameProcessPacketTask task) {
        processPacket(task.getPacket());
    }

    private void processPlayerInput(final GameProcessPlayerInputTask task) {
        processPlayerInputPacket((GameNetworkPacketPlayerInput) task.getPacket(), task.getClientUUID());
    }

    private void processPacket(final GameNetworkPacket packet) {
        if (packet == null) return;

        switch (packet.getType()) {
            case INIT -> {processInitPacket((GameNetworkPacketInit) packet);}
            case ADD_PLAYER -> {processAddPlayerPacket((GameNetworkPacketPlayerAdd) packet);}
            case REMOVE_PLAYER -> {processRemovePlayerPacket((GameNetworkPacketPlayerRemove) packet);}
            case START -> {processStartPacket((GameNetworkPacketStart) packet);}
            case SPAWN_OBJECT -> {processSpawnObjectPacket((GameNetworkPacketGameObjectActionSolidSpawn) packet);}
            case MODIFY_OBJECT -> {processModifyObjectPacket((GameNetworkPacketGameObjectActionSolidModify) packet);}
            //case MAP_CLICK -> {processMapClickPacket((GameNetworkPacketPlayerInputMapClick) packet);}
            case REMOVE_OBJECT -> {processRemoveObjectPacket((GameNetworkPacketGameObjectActionFastRemove) packet);}
            case SET_PLAYER_SCORE -> {processSetPlayerScorePacket((GameNetworkPacketPlayerModifySetScore) packet);}
            case END -> {processEndPacket((GameNetworkPacketEnd) packet);}
            case RESTART -> {processRestartPacket((GameNetworkPacketRestart) packet);}
            case CLOSE -> {processClosePacket((GameNetworkPacketClose) packet);}
            case RESUME -> {processResumePacket((GameNetworkPacketResume) packet);}
            case PAUSE -> {processPausePacket((GameNetworkPacketPause) packet);}
            default -> processError(new GameErrorEventIncorrectPacketType());
        }
    }

    private void processInitPacket(final GameNetworkPacketInit packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        m_gameStateRef.setLocalPlayer(new Player(packet.getLocalPlayerId()));
        m_gameStateRef.getMap().changeSize(packet.getMapSize());
        m_tps = packet.getTPS();
    }

    private void processAddPlayerPacket(final GameNetworkPacketPlayerAdd packet) {
        if (!m_gameStateRef.addPlayer(new Player(packet.getUUID())))
            processError(new GameErrorEventStateModificationFailed());
    }

    private void processSetPlayerScorePacket(final GameNetworkPacketPlayerModifySetScore packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        Logger.getGlobal().info("New score: " + packet.getScore());

        UnaryOperator<Player> playerScoreUpdater = (player) -> {
            player.setScore(packet.getScore());

            return player;
        };

        SharedObject<Player> localPlayer = m_gameStateRef.getLocalPlayer();

        if (packet.getUUID().compareTo(localPlayer.getValue().getUUID()) == 0) {
            if (!localPlayer.modifyValue(playerScoreUpdater))
                processError(new GameErrorEventStateModificationFailed());

            return;
        }

        boolean result = m_gameStateRef.getPlayerList().forFirstIf(
                player -> player.getUUID().compareTo(packet.getUUID()) == 0,
                playerScoreUpdater);

        if (!result) {
            processError(new GameErrorEventIllegalState());

            return;
        }
    }

    private void processRemovePlayerPacket(final GameNetworkPacketPlayerRemove packet) {
        m_gameStateRef.removePlayerByUUID(packet.getUUID());

//        if (!m_gameStateRef.removePlayerByUUID(packet.getUUID()))
//            processError(new GameErrorEventStateModificationFailed());
    }

    private void processStartPacket(final GameNetworkPacketStart packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        m_callbackStore.processCommand(new GameCommandStart(GameCommand.Origin.REMOTE));
    }

    private void processSpawnObjectPacket(final GameNetworkPacketGameObjectActionSolidSpawn packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        if (!m_gameStateRef.getMap().addObject(packet.getGameObject()))
            processError(new GameErrorEventStateModificationFailed());
    }

    private void processModifyObjectPacket(final GameNetworkPacketGameObjectActionSolidModify packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        if (!m_gameStateRef.getMap().modifyObject(packet.getGameObject()))
            processError(new GameErrorEventStateModificationFailed());
    }

    private void processRemoveObjectPacket(final GameNetworkPacketGameObjectActionFastRemove packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        Logger.getGlobal().info("REMOVING PACKET");

        if (!m_gameStateRef.getMap().removeObjectById(packet.getObjectId()))
            processError(new GameErrorEventStateModificationFailed());
    }

    private void processPlayerInputPacket(final GameNetworkPacketPlayerInput packet, final UUID clientUUID) {
        switch (packet.getGameInput().getType()) {
            case MOUSE_CLICK -> processMapClickPacket(packet, clientUUID);
            default -> processError(new GameErrorEventIncorrectGameInput());
        }
    }

    private void processMapClickPacket(final GameNetworkPacketPlayerInput packet, final UUID clientUUID) {
        if (m_gameRoleRef.getValue() != GameRole.MASTER) return;

        GameInputMouseClick mouseClick = (GameInputMouseClick)packet.getGameInput();
        GameObject aimObject = m_gameStateRef.getMap().getObjectAtCoords(mouseClick.getX(), mouseClick.getY());

        if (aimObject == null) return;

        if (!m_gameStateRef.getMap().removeObjectById(aimObject.getId()))
            processError(new GameErrorEventStateModificationFailed());

        m_gameServerTaskStore.getNetTaskQueue().offer(
                new NetworkSendPacketTask(
                        new GameNetworkPacketGameObjectActionFastRemove(aimObject.getId()),
                        PacketDestinationType.BROADCAST));

        int newScore = 0;

        if (aimObject instanceof RewardableInterface) {
            boolean result = m_gameStateRef.getPlayerList().forFirstIf(
                    player -> player.getUUID() == clientUUID,
                    player -> {
                        player.setScore(((RewardableInterface) aimObject).getReward() + player.getScore());

                        return player;
                    });

            newScore = m_gameStateRef.getPlayerByUUID(clientUUID).getScore();

            if (!result) {
                processError(new GameErrorEventStateModificationFailed());

                return;
            }
        }

        m_gameServerTaskStore.getNetTaskQueue().offer(
                new NetworkSendPacketTask(
                    new GameNetworkPacketPlayerModifySetScore(clientUUID, newScore),
                    PacketDestinationType.BROADCAST));
    }

    private void processPausePacket(final GameNetworkPacketPause packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        m_callbackStore.processCommand(new GameCommandPause(GameCommand.Origin.REMOTE));
    }

    private void processResumePacket(final GameNetworkPacketResume packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        m_callbackStore.processCommand(new GameCommandResume(GameCommand.Origin.REMOTE));
    }

    private void processEndPacket(final GameNetworkPacketEnd packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        m_callbackStore.processCommand(new GameCommandStop(GameCommand.Origin.REMOTE));
    }

    private void processRestartPacket(final GameNetworkPacketRestart packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        m_callbackStore.processCommand(new GameCommandRestart(GameCommand.Origin.REMOTE));
    }

    private void processClosePacket(final GameNetworkPacketClose packet) {
        if (m_gameRoleRef.getValue() != GameRole.SLAVE) return;

        m_callbackStore.processCommand(new GameCommandClose(GameCommand.Origin.REMOTE));
    }

    private boolean onGameCreate(final GameCommandCreate createCommand) {
        startServerThread();

        return m_networkProcessor.exec("0.0.0.0", GameRole.MASTER);
    }

    private void onGameStart(final GameCommandStart startCommand) {
        switch (m_gameRoleRef.getValue()) {
            case MASTER -> {
                m_gameStartTimePoint = System.currentTimeMillis();

                if (startCommand.getOrigin() != GameCommand.Origin.LOCAL) return;

                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkStartProcessingTask(new GameNetworkPacketStart()));
            }
            case SLAVE -> {
                // nothing to do??

            }
            default -> processError(new GameErrorEventIncorrectRole());
        }
    }

    private boolean onGameConnect(final GameCommandConnect connectCommand) {
        startServerThread();

        return m_networkProcessor.exec(connectCommand.getHost(), GameRole.SLAVE);
    }

    private void onGameStop(final GameCommandStop stopCommand) {
        switch (m_gameRoleRef.getValue()) {
            case MASTER -> {
                if (stopCommand.getOrigin() != GameCommand.Origin.LOCAL) return;

                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkStopProcessingTask(new GameNetworkPacketEnd()));

            }
            case SLAVE -> {
                // nothing to do??

            }
            default -> processError(new GameErrorEventIncorrectRole());
        }

        //processGameEnd();
    }

    private void onGameResume(final GameCommandResume resumeCommand) {
        if (m_gameRoleRef.getValue() != GameRole.MASTER) return;

        m_gameServerTaskStore.getNetTaskQueue().offer(
            new NetworkSendPacketTask(
                new GameNetworkPacketResume(),
                PacketDestinationType.BROADCAST));
    }

    private void onGamePause(final GameCommandPause pauseCommand) {
        if (m_gameRoleRef.getValue() != GameRole.MASTER) return;

        m_gameServerTaskStore.getNetTaskQueue().offer(
            new NetworkSendPacketTask(
                    new GameNetworkPacketPause(),
                    PacketDestinationType.BROADCAST));
    }

    private void onGameRestart(final GameCommandRestart restartCommand) {
        startServerThread();

        switch (m_gameRoleRef.getValue()) {
            case MASTER -> {
                if (restartCommand.getOrigin() != GameCommand.Origin.LOCAL) return;

                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkRestartGameTask(new GameNetworkPacketRestart()));

            }
            case SLAVE -> {

            }
            default -> processError(new GameErrorEventIncorrectRole());
        }
    }

    private void onGameClose(final GameCommandClose closeCommand) {
        Logger.getGlobal().info("CLOSING on SERVER");

        switch (m_gameRoleRef.getValue()) {
            case MASTER -> {
                if (closeCommand.getOrigin() != GameCommand.Origin.LOCAL) return;

                m_gameServerTaskStore.getNetTaskQueue().offer(
                        new NetworkSendPacketTask(
                            new GameNetworkPacketClose(),
                            PacketDestinationType.BROADCAST));
                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkCloseProcessingTask());

            }
            case SLAVE -> {
                switch (closeCommand.getOrigin()) {
                    case LOCAL -> {
                        m_gameServerTaskStore.getNetTaskQueue().offer(
                                new NetworkSendPacketTask(
                                    new GameNetworkPacketLeave(),
                                    PacketDestinationType.SERVER));
                        m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkCloseProcessingTask());

                    }
                    case REMOTE -> {
                        m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkCloseProcessingTask());
                    }
                    default -> processError(new GameErrorEventIncorrectCommandOrigin());
                }

            }
            default -> processError(new GameErrorEventIncorrectRole());
        }

        m_isSideCyclePaused.set(true);
    }

    private void onGameExit(final GameCommandExit command) {
        m_isSideCycleAlive.set(false);
    }

    private void processInput() {
        GameInput curInput;

        while ((curInput = m_gameInputQueue.poll()) != null) {
            switch (curInput.getType()) {
                case MOUSE_CLICK -> processMouseClick((GameInputMouseClick) curInput);
                default -> processError(new GameErrorEventIncorrectGameInput());
            }
        }
    }

    private void processMouseClick(final GameInputMouseClick mouseClick) {
        if (mouseClick == null) {
            processError(new GameErrorEventIncorrectGameInput());

            return;
        }

        switch (m_gameRoleRef.getValue()) {
            case MASTER -> {
                GameObject object = m_gameStateRef.getMap().getObjectAtCoords(mouseClick.getX(), mouseClick.getY());

                if (object == null) return;

                if (!m_gameStateRef.getMap().removeObjectById(object.getId())) {
                    processError(new GameErrorEventIllegalState());

                    return;
                }

                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkSendPacketTask(
                        new GameNetworkPacketGameObjectActionFastRemove(object.getId()),
                        PacketDestinationType.BROADCAST));

                SharedObject<Player> localPlayer = m_gameStateRef.getLocalPlayer();
                int newScore = 0;

                if (object instanceof RewardableInterface) {
                    localPlayer.modifyValue((player) -> {
                        player.setScore(player.getScore() + ((RewardableInterface) object).getReward());

                        return player;
                    });

                    newScore = localPlayer.getValue().getScore();
                }

                m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkSendPacketTask(
                        new GameNetworkPacketPlayerModifySetScore(m_gameStateRef.getLocalPlayer().getValue().getUUID(), newScore),
                        PacketDestinationType.BROADCAST));

            }
            case SLAVE -> {
                 m_gameServerTaskStore.getNetTaskQueue().offer(new NetworkSendPacketTask(
                         new GameNetworkPacketPlayerInput(mouseClick),
                         PacketDestinationType.SERVER));
            }
        }
    }

//    private void processMouseClickForPlayer(final UUID playerUUID, final GameInputMouseClick mouseClick) {
//        GameObject object = m_gameStateRef.getMap().getObjectAtCoords(mouseClick.getX(), mouseClick.getY());
//
//        if (object == null) return;
//
//        if (!m_gameStateRef.getMap().removeObjectById(object.getId())) {
//            processError(new GameErrorEventIllegalState());
//
//            return;
//        }
//
//        int reward = 0;
//
//        if (object instanceof RewardableInterface) {
//            int rewardLocal = ((RewardableInterface) object).getReward();
//
//            m_gameStateRef.get.modifyValue((player) -> {
//                player.setScore(player.getScore() + rewardLocal);
//
//                return player;
//            });
//
//            reward = rewardLocal;
//        }
//    }

    private void processError(final GameErrorEvent error) {
        m_isSideCyclePaused.set(true);
        m_appEventQueueRef.offer(error);
    }

    @Override
    protected void processGameEnd() {
        m_isSideCyclePaused.set(true);
    }
}
