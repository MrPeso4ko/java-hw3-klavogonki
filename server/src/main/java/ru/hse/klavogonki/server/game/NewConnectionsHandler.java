package ru.hse.klavogonki.server.game;

import ru.hse.klavogonki.server.net.Player;

import java.util.concurrent.BlockingQueue;

public class NewConnectionsHandler implements Runnable {
    private final BlockingQueue<Player> queue;

    public NewConnectionsHandler(BlockingQueue<Player> q) {
        queue = q;
    }

    @Override
    public void run() {
        try (Room room = new Room()) {
            while (true) {
                room.add(queue.take());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
