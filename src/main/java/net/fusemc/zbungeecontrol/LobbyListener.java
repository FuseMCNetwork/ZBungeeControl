package net.fusemc.zbungeecontrol;

import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.*;
import java.util.logging.Level;

/**
 * Created by Marco on 05.08.2014.
 */
public class LobbyListener implements Listener {

    public static ServerInfo getTargetServer() {
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT name FROM runtime_servers WHERE gamemode = 'LOBBY' AND online = 1 ORDER BY players ASC LIMIT 1;");
            String server = null;
            if (rs.next()) {
                server = rs.getString(1);
            }
            s.close();
            rs.close();
            if (server == null) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Failed to connect a player to a free server!");
                return null;
            }
            return ProxyServer.getInstance().getServerInfo(server);
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    @EventHandler
    public void onPlayerKick(ServerKickEvent event) {
        String component = TextComponent.toPlainText(event.getKickReasonComponent());
        if (component.equals("lobby")) {
            event.setCancelled(true);
            //verteilung
            ServerInfo info = getTargetServer();
            if (info == null) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Failed to connect a player to a free server!");
                event.setKickReasonComponent(TextComponent.fromLegacyText("\u00a78\u00a7l[\u00a74\u2716\u00a78\u00a7l] \u00a7cDerzeit ist leider keine Lobby verf\u00fcgbar! Versuche es in wenigen Minuten erneut!"));
                return;
            }
            event.getPlayer().connect(info);
        }
    }

    public static class MySQLReconnectHandler implements ReconnectHandler {

        @Override
        public ServerInfo getServer(ProxiedPlayer proxiedPlayer) {
            ServerInfo info = getTargetServer();
            return info == null ? AbstractReconnectHandler.getForcedHost(proxiedPlayer.getPendingConnection()) : info;
        }

        @Override
        public void setServer(ProxiedPlayer proxiedPlayer) {

        }

        @Override
        public void save() {

        }

        @Override
        public void close() {

        }
    }
}
