package ru.hse.klavogonki.server.game;

import ru.hse.klavogonki.models.PlayerInfo;
import ru.hse.klavogonki.server.net.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameMessageHandler implements AutoCloseable {
    private final List<Player> players;
    private final ExecutorService executor;

    public GameMessageHandler(List<Player> players) {
        this.players = players;
        executor = Executors.newFixedThreadPool(players.size());
        startHandler();
    }

    private void startHandler() {
        synchronized (players) {
            for (Player player : players) {
                executor.execute(() -> {
                    ObjectInputStream inputStream = player.connection.getInputStream();
                    while (true) {
                        try {
                            PlayerInfo info = (PlayerInfo) inputStream.readObject();
                            synchronized (players) {
                                player.info.written = info.written;
                                player.info.mistakes = info.mistakes;
                            }
                        } catch (ClassNotFoundException | StreamCorruptedException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            synchronized (players) {
                                System.out.println("Player " + player.info.name + " disconnected");
                                player.info.disconnected = true;
                            }
                            break;
                        }
                    }
                });
            }
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
