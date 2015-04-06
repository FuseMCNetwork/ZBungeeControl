package net.fusemc.zbungeecontrol.server;

import net.fusemc.zbungeecontrol.ZBungeeControl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Marco on 28.07.2014.
 */
public class ServerData {

    private String name;
    private boolean online;
    private boolean exists;
    private int maxPlayers;
    private int currentPlayers;

    public ServerData(String name) {
        this.name = name;
        update();
    }

    public ServerData(String name, int maxPlayers, int currentPlayers) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.currentPlayers = currentPlayers;
    }

    public void update() {
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT online, max_players, players FROM runtime_servers WHERE name = ?;");
            ps.setString(1, this.name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                this.online = rs.getInt(1) == 1;
                this.maxPlayers = rs.getInt(2);
                this.currentPlayers = rs.getInt(3);
                this.exists = true;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public boolean isFull() {
        return currentPlayers >= maxPlayers;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean exists() {
        return exists;
    }
}
