package ru.hse.klavogonki.server.game;

import ru.hse.klavogonki.models.RoomStatus;
import ru.hse.klavogonki.models.Timings;
import ru.hse.klavogonki.server.net.Player;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Room implements AutoCloseable {
    private final List<Player> players = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private long timeStart;

    public Room() {
        timeStart = new Date().getTime();
        executor.scheduleAtFixedRate(() -> {
            List<Player> disconnected = new ArrayList<>();
            long remainingTime;
            synchronized (players) {
                if (players.isEmpty()) {
                    timeStart = new Date().getTime();
                }
                remainingTime = Timings.TIME_TO_WAIT - (new Date().getTime() - timeStart) / 1000;
                if (remainingTime == 0 || players.size() == 3) {
                    new Thread(new Game(players)).start();
                    players.clear();
                } else {
                    List<String> playerNames = players.stream().map(player -> player.info.name).toList();
                    RoomStatus status = new RoomStatus();
                    status.remainingTime = remainingTime;
                    status.playerNames = playerNames;
                    for (Player player : players) {
                        ObjectOutputStream outputStream = player.connection.getOutputStream();
                        try {
                            outputStream.writeObject(status);
                        } catch (IOException e) {
                            disconnected.add(player);
                        }
                    }
                    players.removeAll(disconnected);
                }
            }
            if (!disconnected.isEmpty()) {
                StringBuilder msg = new StringBuilder("Following players are disconnected:\n");
                for (Player player : disconnected) {
                    try {
                        player.connection.close();
                    } catch (IOException e) {
                        System.out.println("Can't close socket " + player.connection.getSocket().getInetAddress().toString());
                    }
                    msg.append(" - ").append(player.info.name).append("\n");
                }
                synchronized (players) {
                    List<String> playerNames = players.stream().map(player -> player.info.name).toList();
                    RoomStatus status = new RoomStatus();
                    status.remainingTime = remainingTime;
                    status.text = msg.toString();
                    status.playerNames = playerNames;
                    for (Player player : players) {
                        try {
                            ObjectOutputStream outputStream = player.connection.getOutputStream();
                            outputStream.writeObject(status);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void add(Player player) {
        synchronized (players) {
            players.add(player);
            System.out.println("Added player to room:" + player.info.name);
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
