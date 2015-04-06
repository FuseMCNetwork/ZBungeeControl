package net.fusemc.zbungeecontrol;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerCountHandler implements Listener {
    private AtomicInteger playerCount = new AtomicInteger(0);
    private final AtomicInteger delta = new AtomicInteger(0);

    public PlayerCountHandler() {
        ProxyServer.getInstance().getScheduler().schedule(ZBungeeControl.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection conn = ZBungeeControl.getNetworkMysql().getConnection()) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE bungee_global SET online_players = online_players + ?;");
                    ps.setInt(1, delta.getAndSet(0));
                    ps.executeUpdate();
                    ps.close();

                    Statement s = conn.createStatement();
                    ResultSet rs = s.executeQuery("SELECT online_players FROM bungee_global;");
                    if (rs.next()) {
                        playerCount.set(rs.getInt(1));
                    }
                    rs.close();
                    s.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent e) {
        delta.decrementAndGet();
    }

    @EventHandler
    public void onPlayerJoin(LoginEvent e) {
        delta.incrementAndGet();
    }

    @EventHandler
    public void onPing(ProxyPingEvent e) {
        e.getResponse().getPlayers().setOnline(playerCount.get() + delta.get());
        e.getResponse().getPlayers().setMax(e.getConnection().getListener().getDisplayMaxPlayers());
    }
}
