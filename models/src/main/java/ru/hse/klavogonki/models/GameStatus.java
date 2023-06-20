package ru.hse.klavogonki.models;

import java.io.Serializable;
import java.util.List;

public class GameStatus implements Serializable {
    public long id;
    public boolean GameStarted;
    public boolean GameFinished;
    public List<PlayerInfo> players;
    public long remainingTime;

    @Override
    public String toString() {
        return "GameStatus{" +
                "GameStarted=" + GameStarted +
                ", GameFinished=" + GameFinished +
                ", players=" + players +
                ", remainingTime=" + remainingTime +
                ", id=" + id +
                '}';
    }
}
