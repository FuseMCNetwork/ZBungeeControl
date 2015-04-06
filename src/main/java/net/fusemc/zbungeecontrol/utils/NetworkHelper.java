package net.fusemc.zbungeecontrol.utils;


import com.google.common.collect.Lists;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.server.PlayerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Kevin on 07.04.14.
 * modified by michidk
 */
public class NetworkHelper {

    final static String LOBBYPREFIX = "LOBBY";
    final static String VIPLOBBYPREFIX = "VIPLOBBY";

    public static boolean isOnline(String player) {
        if (ProxyServer.getInstance().getPlayer(player) != null) {
            return true;
        }
        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM network_users WHERE name=? AND online=1");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            boolean result = rs.next();
            rs.close();
            ps.close();
            return result;
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return false;
    }

    public static PlayerData getUserInfo(String player) {
        if (ProxyServer.getInstance().getPlayer(player) != null) {
            ProxiedPlayer playerObj = ProxyServer.getInstance().getPlayer(player);
            return new PlayerData(player, playerObj.getServer().getInfo().getName(), ZNetworkPlugin.getInstance().getConnectionName());
        }
        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT bukkit, bungee FROM network_users WHERE name=? AND online=1");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            PlayerData info = new PlayerData(player, rs.getString(1), rs.getString(2));
            rs.close();
            ps.close();
            return info;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRank(String player) {
        try (Connection con = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT rank FROM rank WHERE uuid=?");
            ps.setString(1, getUUID(player));
            ResultSet rs = ps.executeQuery();
            rs.next();
            String rank = rs.getString(1);
            rs.close();
            ps.close();
            return (rank == null || rank.equalsIgnoreCase("") ? "user" : rank);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "user";
    }

    public static ArrayList<String> getOnlinePlayers() {
        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            ArrayList<String> users = new ArrayList<>();
            PreparedStatement s = conn.prepareStatement("SELECT name FROM network_users WHERE online=1");
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                users.add(rs.getString(1));
            }
            rs.close();
            s.close();
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getName(String uuid) {
        try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT name FROM network_users WHERE uuid = ?");
            preparedStatement.setString(1, uuid);
            ResultSet rs = preparedStatement.executeQuery();
            String name = rs.getString(1);

            rs.close();
            preparedStatement.close();
            return name;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<String> getLobbys() {
        ArrayList<String> lobbys = Lists.newArrayList();
        for (String s : ProxyServer.getInstance().getServers().keySet()) {
            if (s.toLowerCase().startsWith(LOBBYPREFIX.toLowerCase()) && !s.equalsIgnoreCase(LOBBYPREFIX) && !s.toLowerCase().startsWith(VIPLOBBYPREFIX)) {
                lobbys.add(s);
            }
        }

        return lobbys;
    }

    public static ArrayList<String> getPremiumLobbys() {
        ArrayList<String> lobbys = Lists.newArrayList();
        for (String s : ProxyServer.getInstance().getServers().keySet()) {
            if (s.toLowerCase().startsWith(VIPLOBBYPREFIX.toLowerCase()) && !s.equalsIgnoreCase(VIPLOBBYPREFIX)) {
                lobbys.add(s);
            }
        }

        return lobbys;
    }

    public static String getUUID(String player) {
        if (ProxyServer.getInstance().getPlayer(player) != null) {
            return ProxyServer.getInstance().getPlayer(player).getUniqueId().toString();
        }
        String uuid = null;
        try (Connection con = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT uuid FROM network_users WHERE name=?");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            uuid = rs.getString(1);
            ps.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuid;
    }

    public static boolean isOnLobbyServer(ProxiedPlayer player) {
        return player.getServer().getInfo().getName().startsWith("LOBBY");
    }


}