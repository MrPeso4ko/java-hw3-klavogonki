package ru.hse.klavogonki.server.net;

import ru.hse.klavogonki.models.PlayerInfo;

public class Player implements Comparable<Player> {
    public PlayerInfo info;
    public ServerSideConnection connection;

    public Player(PlayerInfo pi, ServerSideConnection c) {
        info = pi;
        connection = c;
    }

    @Override
    public int compareTo(Player o) {
        return info.compareTo(o.info);
    }
}
