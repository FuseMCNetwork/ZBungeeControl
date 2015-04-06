package net.fusemc.zbungeecontrol;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright by michidk
 * Created: 16.08.2014.
 */
public class MySQLHandler implements Listener {

    @EventHandler
    public void onPlayerConnectedServer(ServerConnectedEvent e) {
        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO network_users(uuid, name, bungee, bukkit, online) VALUES (?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE name = VALUES(name), bungee = VALUES(bungee), bukkit = VALUES(bukkit), online = 1;");
            ps.setString(1, e.getPlayer().getUniqueId().toString());
            ps.setString(2, e.getPlayer().getName());
            ps.setString(3, ZNetworkPlugin.getInstance().getConnectionName());
            ps.setString(4, e.getServer().getInfo().getName());
            ps.execute();
            ps.close();
            ps = conn.prepareStatement("UPDATE runtime_servers SET players=players+1 WHERE name=?");
            ps.setString(1, e.getServer().getInfo().getName());
            ps.execute();
            ps.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLeaveServer(ServerDisconnectEvent e) {
        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE runtime_servers SET players = GREATEST(players-1, 0) WHERE name=?");
            ps.setString(1, e.getTarget().getName());
            ps.execute();
            ps.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent e) {
        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE network_users SET bungee=NULL, bukkit=NULL, online=0 WHERE uuid=?");
            ps.setString(1, e.getPlayer().getUniqueId().toString());
            ps.execute();
            ps.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        try (Connection connection = ZBungeeControl.getPlayerMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("CALL disguise_player_leave(?);");
            ps.setString(1, e.getPlayer().getUniqueId().toString());
            ps.execute();
            ps.close();
        } catch (SQLException e1){
            e1.printStackTrace();
        }
    }

    public static void handleShutdown() {
        Map<String, Integer> playerCount = new HashMap<>();
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            String name = p.getServer().getInfo().getName();
            if (playerCount.containsKey(name)) {
                playerCount.put(name, playerCount.get(name) + 1);
            } else {
                playerCount.put(name, 1);
            }
        }

        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE runtime_servers SET players=players-? WHERE name=?");

            int i = 0;
            for (String server : playerCount.keySet()) {
                ps.setInt(1, playerCount.get(server));
                ps.setString(2, server);
                i++;
                ps.addBatch();
                if ((i + 1) % 1000 == 0) {
                    ps.executeBatch(); //executed every 1000 items because some JDBC drivers and/or DB's may have a limitation on batch length.
                }
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE network_users SET bungee=NULL, bukkit=NULL, online=0 WHERE bungee=?");
            ps.setString(1, ZNetworkPlugin.getInstance().getConnectionName());
            ps.execute();
            ps.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try (Connection conn = ZBungeeControl.getPlayerMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("CALL disguise_player_leave(?);");

            int i = 0;
            for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                ps.setString(1, player.getUniqueId().toString());
                ps.addBatch();
                if ((i + 1) % 1000 == 0) {
                    ps.executeBatch(); //executed every 1000 items because some JDBC drivers and/or DB's may have a limitation on batch length.
                }
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

    }
}
