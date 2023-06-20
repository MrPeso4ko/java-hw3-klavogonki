package ru.hse.klavogonki.server.game;

import ru.hse.klavogonki.models.GameStatus;
import ru.hse.klavogonki.models.PlayerInfo;
import ru.hse.klavogonki.models.RoomStatus;
import ru.hse.klavogonki.models.Timings;
import ru.hse.klavogonki.server.net.Player;
import ru.hse.klavogonki.server.util.TextLoader;

import java.io.IOException;
import java.util.*;

public class Game implements Runnable {
    private static final TextLoader textLoader = new TextLoader();
    private final List<Player> players;

    private GameMessageHandler handler;

    public Game(List<Player> players) {
        this.players = Collections.synchronizedList(new ArrayList<>(players));
    }

    private void sendText() {
        RoomStatus status = new RoomStatus();
        synchronized (players) {
            status.playerNames = players.stream().map(player -> player.info.name).toList();
        }
        status.text = textLoader.getText();
        status.gameStarted = true;
        status.remainingTime = 0;
        synchronized (players) {
            for (Player player : players) {
                try {
                    player.connection.getOutputStream().writeObject(status);
                } catch (IOException e) {
                    System.out.println("Can't communicate with player " + player.info.name + e);
                    player.info.disconnected = true;
                }
            }
        }
    }

    private void sendPlayerInfo(GameStatus gameStatus) {
        synchronized (players) {
            Collections.sort(players);
            gameStatus.players = players.stream().map(player -> new PlayerInfo(player.info)).toList();
            for (int i = 0; i < players.size(); ++i) {
                if (!players.get(i).info.disconnected) {
                    gameStatus.players.get(i).isYou = true;
                    try {
                        gameStatus.id = new Date().getTime();
                        players.get(i).connection.getOutputStream().writeObject(gameStatus);
                        System.out.println("gameStatus = " + gameStatus);
                    } catch (IOException e) {
                        System.out.println("Can't communicate with player " + players.get(i).info.name + e);
                        players.get(i).info.disconnected = true;
                    }
                    gameStatus.players.get(i).isYou = false;
                }
            }
        }
    }

    private void countdown() {
        long timeStarted = new Date().getTime();
        Timer beforeGameTimer = new Timer();
        beforeGameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long remainingTime = Timings.BEFORE_GAME_DURATION - (new Date().getTime() - timeStarted) / 1000;
                if (remainingTime >= 0) {
                    GameStatus gameStatus = new GameStatus();
                    gameStatus.GameStarted = false;
                    gameStatus.remainingTime = remainingTime;
                    sendPlayerInfo(gameStatus);
                } else {
                    beforeGameTimer.cancel();
                    beforeGameTimer.purge();
                    startGame();
                }
            }
        }, 0, 1000);
    }

    private void startGame() {
        long timeStarted = new Date().getTime();
        handler = new GameMessageHandler(players);
        Timer gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long remainingTime = Timings.GAME_DURATION - (new Date().getTime() - timeStarted) / 1000;
                GameStatus gameStatus = new GameStatus();
                gameStatus.GameStarted = true;
                gameStatus.remainingTime = remainingTime;
                if (remainingTime >= 0) {
                    gameStatus.GameFinished = false;
                } else {
                    gameStatus.GameFinished = true;
                    gameTimer.cancel();
                    gameTimer.purge();
                    endGame();
                }
                sendPlayerInfo(gameStatus);
            }
        }, 0, 1000);
    }

    public void endGame() {
        synchronized (players) {
            for (Player player : players) {
                try {
                    player.connection.close();
                } catch (IOException e) {
                    System.out.println("Can't close socket for player: " + player.info.name);
                }
            }
        }
    }

    public void run() {
        System.out.println("Game started!");
        sendText();
        countdown();
    }

}
