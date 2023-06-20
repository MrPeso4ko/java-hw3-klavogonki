package ru.hse.klavogonki.models;

import java.io.Serializable;

public class PlayerInfo implements Serializable, Comparable<PlayerInfo> {
    public String name = "";
    public int written = 0;
    public int mistakes = 0;
    public boolean isYou = false;
    public boolean disconnected = false;

    public PlayerInfo() {
    }

    public PlayerInfo(PlayerInfo o) {
        this.name = o.name;
        this.written = o.written;
        this.mistakes = o.mistakes;
        this.disconnected = o.disconnected;
        this.isYou = o.isYou;
    }

    @Override
    public int compareTo(PlayerInfo o) {
        if (written != o.written) {
            return Integer.compare(o.written, written);
        } else {
            return Integer.compare(mistakes, o.mistakes);
        }
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "name='" + name + '\'' +
                ", written=" + written +
                ", mistakes=" + mistakes +
                ", isYou=" + isYou +
                ", disconnected=" + disconnected +
                '}';
    }
}
