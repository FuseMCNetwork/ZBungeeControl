package net.fusemc.zbungeecontrol.server;

import net.fusemc.zbungeecontrol.ZBungeeControl;

import java.sql.*;

/**
 * Created by Kevin on 07.04.14.
 */
public class PlayerData {


    private String name;
    private String server;
    private String bungee;
    private Timestamp lastSeen;
    private boolean online;
    private boolean exists;

    public PlayerData(String name) {
        this.name = name;
        update();
    }

    public PlayerData(String name, String server, String bungee) {
        this.name = name;
        this.server = server;
        this.bungee = bungee;
    }

    public PlayerData(String name, String server, String bungee, Timestamp lastSeen) {
        this.name = name;
        this.server = server;
        this.bungee = bungee;
        this.lastSeen = lastSeen;
    }

    public void update() {
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT bungee, bukkit, online, seen FROM network_users WHERE name=?");
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                bungee = rs.getString(1);
                server = rs.getString(2);
                online = rs.getInt(3) == 1;
                lastSeen = rs.getTimestamp(4);
                exists = true;
            }
            ps.close();
            rs.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }


    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public String getBungee() {
        return bungee;
    }

    public Timestamp getLastSeen() {
        return lastSeen;
    }

    public boolean exists() {
        return exists;
    }

    public boolean isOnline() {
        return online;
    }
}
