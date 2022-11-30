package com.mcdead.aimbattle.screen.game.side.server;

import com.mcdead.aimbattle.event.Event;
import com.mcdead.aimbattle.event.error.*;
import com.mcdead.aimbattle.screen.game.GameMode;
import com.mcdead.aimbattle.screen.game.GameRole;
import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacket;
import com.mcdead.aimbattle.screen.game.side.server.packet.GameNetworkPacketPlayerInput;
import com.mcdead.aimbattle.screen.game.side.server.socket.ClientSocket;
import com.mcdead.aimbattle.screen.game.side.server.socket.PlayerSocket;
import com.mcdead.aimbattle.screen.game.side.server.socket.ServerSocket;
import com.mcdead.aimbattle.screen.game.side.server.socket.Socket;
import com.mcdead.aimbattle.screen.game.side.server.task.*;
import com.mcdead.aimbattle.utils.SharedObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class GameServerNetworkProcessor {
    public static int C_PORT = 6060;

    private SharedObject<GameMode> m_gameModeRef;
    private SharedObject<GameRole> m_gameRole;
    private AtomicBoolean m_isNetThreadAlive;
    private AtomicBoolean m_isNetThreadPaused;

    private HashMap<UUID, PlayerSocket> m_clientSocketMap;
    private Socket m_socket;
    private GameServerTaskStore m_serverTaskStoreRef;
    private BlockingQueue<Event> m_appEventQueueRef;

    public GameServerNetworkProcessor(final SharedObject<GameMode> gameModeRef,
                                      GameServerTaskStore taskStore,
                                      BlockingQueue<Event> appEventQueueRef)
    {
        m_gameModeRef = gameModeRef;
        m_gameRole = new SharedObject<GameRole>(GameRole.INCORRECT);
        m_isNetThreadAlive = new AtomicBoolean(false);
        m_isNetThreadPaused = new AtomicBoolean(false);
        m_clientSocketMap = new HashMap<>();
        m_serverTaskStoreRef = taskStore;
        m_appEventQueueRef = appEventQueueRef;
    }

    public boolean isAlive() {
        return m_isNetThreadAlive.get();
    }

    public boolean exec(final String hostname, final GameRole role)
    {
        if (!initSocketWithRole(hostname, role)) {
            processError(new NetworkErrorEventConnectionFailed());

            return false;
        }

        m_gameRole.setValue(role);

        if (m_isNetThreadAlive.get()) {
            m_isNetThreadPaused.set(false);

            return true;
        }

        m_isNetThreadAlive.set(true);
        m_isNetThreadPaused.set(false);

        new Thread(() -> {
            while (m_isNetThreadAlive.get()) {
                if (m_isNetThreadPaused.get()) continue;

                processNetworkTasks();

                switch (m_gameRole.getValue()) {
                    case SLAVE -> processNetworkAsSlave();
                    case MASTER -> processNetworkAsMaster();
                    default -> processError(new NetworkErrorEventIncorrectRole());
                }
            }
        }).start();

        return true;
    }

    private void resetExec() {
        m_isNetThreadPaused.set(true);

        clearPendingTasks();

        if (!closeConnection())
            processError(new NetworkErrorEventSocketError());
    }

    private void killExec() {
        resetExec();

        m_isNetThreadAlive.set(false);
    }

    private void clearPendingTasks() {
        m_serverTaskStoreRef.getNetTaskQueue().clear();
    }

    private void processNetworkTasks() {
        Task task = null;

        while ((task = (Task) m_serverTaskStoreRef.getNetTaskQueue().poll()) != null) {
            switch ((NetworkTaskType)task.getType()) {
                case INIT_CLIENT -> {processInitClientTask((NetworkInitClientTask) task);}
                case REMOVE_CLIENT -> {processRemoveClientTask((NetworkRemoveClientTask) task);}
                case SEND_PACKET -> {processSendPacketTask((NetworkSendPacketTask) task);}
                case START_PROCESSING -> {processStartProcessingTask((NetworkStartProcessingTask) task);}
                case STOP_PROCESSING -> {processStopProcessingTask((NetworkStopProcessingTask) task);}
                case RESTART_GAME -> {processRestartGameTask((NetworkRestartGameTask) task);}
                case CLOSE -> {processCloseProcessingTask((NetworkCloseProcessingTask) task);}
                case EXIT -> {processExitTask((NetworkExitTask) task);}
                default -> {
                    processError(new NetworkErrorEventTaskProcessingFailed());
                }
            }
        }
    }

    private void processInitClientTask(final NetworkInitClientTask task) {
        if (m_gameRole.getValue() != GameRole.MASTER) return;

        PlayerSocket playerSocket = new PlayerSocket(task.getSocket(), task.getUUID());

        m_clientSocketMap.put(task.getUUID(), playerSocket);

        if (!sendPacketWithErrorCheck(playerSocket, task.getPacket()))
            handleClientKick(playerSocket);

    }

    private void processRemoveClientTask(final NetworkRemoveClientTask task) {
        if (m_gameRole.getValue() != GameRole.MASTER) return;

        if (m_clientSocketMap.remove(task.getClientToRemoveUUID()) == null)
            return;

        broadcastPacketSend(task.getPacket());
    }

    private void processSendPacketTask(final NetworkSendPacketTask task) {
        switch (m_gameRole.getValue()) {
            case SLAVE -> {
                if (!sendPacketWithErrorCheck((ClientSocket) m_socket, task.getPacket())) {
                    processError(new NetworkErrorEventSocketError());

                    return;
                }

            }
            case MASTER -> {
                switch (task.getDestinationType()) {
                    case SPECIFIED_BY_LIST -> sendPacketToClientsWithUUID(task.getPacket(), task.getDestinationList());
                    case BROADCAST -> broadcastPacketSend(task.getPacket());
                    default -> processError(new NetworkErrorEventTaskProcessingFailed());
                }
            }
            default -> {
                processError(new NetworkErrorEventTaskProcessingFailed());
            }
        }
    }

    private void processStartProcessingTask(final NetworkStartProcessingTask task) {
        switch (m_gameRole.getValue()) {
            case SLAVE -> {
                // nothing to do...
            }
            case MASTER -> {
                broadcastPacketSend(task.getPacket());
            }
            default -> {
                processError(new NetworkErrorEventTaskProcessingFailed());
            }
        }
    }

    private void processStopProcessingTask(final NetworkStopProcessingTask task) {
        switch (m_gameRole.getValue()) {
            case SLAVE -> {
                // nothing to do...
            }
            case MASTER -> {
                broadcastPacketSend(task.getPacket());
            }
            default -> {
                processError(new NetworkErrorEventTaskProcessingFailed());
            }
        }
    }

    private void processRestartGameTask(final NetworkRestartGameTask task) {
        switch (m_gameRole.getValue()) {
            case SLAVE -> {
                // nothing to do...
            }
            case MASTER -> {
                broadcastPacketSend(task.getPacket());
            }
            default -> {
                processError(new NetworkErrorEventTaskProcessingFailed());
            }
        }
    }

    private void processCloseProcessingTask(final NetworkCloseProcessingTask task) {
//        switch (m_gameRole.getValue()) {
//            case SLAVE -> {
//                if (!sendPacketWithErrorCheck((ClientSocket) m_socket, task.getPacket()))
//                    processError(new NetworkErrorEventSocketError());
//            }
//            case MASTER -> {
//                broadcastPacketSend(task.getPacket());
//            }
//            default -> {
//                processError(new NetworkErrorEventTaskProcessingFailed());
//            }
//        }

        resetExec();
    }

    private void processExitTask(final NetworkExitTask task) {
        killExec();
    }

    private void processNetworkAsSlave() {
        //Logger.getGlobal().info("Cur role: " + m_gameRole.getValue().name());

        switch (m_gameModeRef.getValue()) {
            case INIT: {break;}
            case PAUSED:
            case WAITING_PLAYERS:
            case STOP:
            case EXEC_GAME: {
                checkPacketsFromServer();
            }
        }
    }

    private void checkPacketsFromServer() {
        ClientSocket localSocket = (ClientSocket) m_socket;
        GameNetworkPacket packet = null;

        while ((packet = localSocket.tryReceivePacket()) != null) {
            m_serverTaskStoreRef.getGameTaskQueue().offer(new GameProcessPacketTask(packet));
        }
    }

    private void processNetworkAsMaster() {
        switch (m_gameModeRef.getValue()) {
            case INIT: {
                // nothing to do...

                break;
            }
            case WAITING_PLAYERS: {
                //checkClientsForAlive();
                checkPacketsFromClients();

                java.net.Socket newSocket = ((ServerSocket)m_socket).accept();

                if (newSocket == null) return;

                m_serverTaskStoreRef.getGameTaskQueue().offer(new GameAddPlayerTask(newSocket));
            }
            case PAUSED:
            case STOP:
            case EXEC_GAME: {
                //checkClientsForAlive();
                checkPacketsFromClients();
            }
        }
    }

//    private void checkClientsForAlive() {
//        ArrayList<UUID> playerUUIDList = new ArrayList<>();
//
//        m_clientSocketMap.keySet().forEach((key) -> playerUUIDList.add(key));
//
//        for (UUID socketUUID : playerUUIDList) {
//            PlayerSocket socket = m_clientSocketMap.get(socketUUID);
//
//            if (socket.isClosed()) {
//                handleClientKick(socket);
//            }
//        }
//    }

    private void checkPacketsFromClients() {
        ArrayList<UUID> clientSocketUUIDArray = new ArrayList<>();

        m_clientSocketMap.forEach((uuid, socket) -> {
            clientSocketUUIDArray.add(uuid);
        });

        for (final UUID clientUUID : clientSocketUUIDArray) {
            PlayerSocket clientSocket = m_clientSocketMap.get(clientUUID);

            GameNetworkPacket packet = clientSocket.tryReceivePacket();

            if (clientSocket.isCriticalErrorOccurred()) {
                handleClientKick(clientSocket);

                continue;
            }

            if (packet == null) continue;

            GameTaskInterface gameTask = null;

            switch (packet.getType()) {
                case LEAVE        -> {gameTask = new GameRemovePlayerTask(clientSocket.getUUID());}
                case PLAYER_INPUT -> {gameTask = new GameProcessPlayerInputTask((GameNetworkPacketPlayerInput) packet, clientUUID);}
                default           -> {gameTask = new GameProcessPacketTask(packet);}
            }

            m_serverTaskStoreRef.getGameTaskQueue().offer(gameTask);
        }
    }

    private void handleClientKick(PlayerSocket socket) {
        socket.close();

        m_serverTaskStoreRef.getGameTaskQueue().offer(new GameRemovePlayerTask(socket.getUUID()));
    }

    /*
    * Creating socket
    * with binding / connecting.
    */
    private boolean initSocketWithRole(final String hostname, final GameRole role) {
        Logger.getGlobal().info("SOCKET INIT. Role: " + role.name());

        try {
            switch (role) {
                case SLAVE -> {
                    m_socket = new ClientSocket();
                }
                case MASTER -> {
                    m_socket = new ServerSocket();
                }
                default -> {
                    processError(new NetworkErrorEventIncorrectRole());

                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();

            processError(new NetworkErrorEventSocketError());

            return false;
        }

        return m_socket.init(hostname, C_PORT);
    }

    private void broadcastPacketSend(final GameNetworkPacket packet) {
        if (packet == null) return;

        ArrayList<UUID> clientSocketUUIDArray = new ArrayList<>();

        m_clientSocketMap.keySet().forEach((uuid) -> clientSocketUUIDArray.add(uuid));

        for (UUID socketUUID : clientSocketUUIDArray) {
            PlayerSocket socket = m_clientSocketMap.get(socketUUID);

            if (!sendPacketWithErrorCheck(socket, packet))
                handleClientKick(socket);
        }
    }

    private void sendPacketToClientsWithUUID(final GameNetworkPacket packet, final List<UUID> destinationList) {
        if (packet == null || destinationList.size() <= 0) {
            processError(new NetworkErrorEventTaskProcessingFailed());

            return;
        }

        for (final UUID uuid : destinationList) {
            PlayerSocket curSocket = m_clientSocketMap.get(uuid);

            if (curSocket == null) {
                processError(new NetworkErrorEventTaskProcessingFailed());

                return;
            }

            if (!sendPacketWithErrorCheck(curSocket, packet))
                handleClientKick(curSocket);
        }
    }

    private boolean closeConnection() {
        for (PlayerSocket socket : m_clientSocketMap.values())
            if (!socket.close()) return false;

        m_clientSocketMap.clear();

        return m_socket.close();
    }

    private void processError(final NetworkErrorEvent error) {
        m_appEventQueueRef.offer((Event) error);
    }

    private boolean sendPacketWithErrorCheck(final ClientSocket socket, final GameNetworkPacket packet) {
        return socket.sendPacket(packet);
    }
}
