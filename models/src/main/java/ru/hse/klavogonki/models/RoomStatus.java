package ru.hse.klavogonki.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomStatus implements Serializable {
    public long remainingTime = 0;

    public String text = "";
    public boolean gameStarted = false;
    public List<String> playerNames = new ArrayList<>();

    @Override
    public String toString() {
        return "RoomStatus{" +
                "remainingTime=" + remainingTime +
                ", message='" + text + '\'' +
                ", gameStarted=" + gameStarted +
                ", playerNames=" + playerNames +
                '}';
    }
}