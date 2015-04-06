package net.fusemc.zbungeecontrol;

import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.packet.ext.ServerStatusChangeEvent;
import com.xxmicloxx.znetworkplugin.EventListener;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Marco on 16.08.2014.
 */
public class ServerStatusListener {

    private static String motD;

    public static void register() {
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT motd FROM bungee_config");
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                motD = rs.getString(1).replace("\\n", System.getProperty("line.separator"));
            }
            ps.close();
            rs.close();
        } catch (SQLException e){
            e.printStackTrace();
        }

        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT name, address, port FROM runtime_servers");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String name = rs.getString(1);
                String address = rs.getString(2);
                int port = rs.getInt(3);
                ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, new InetSocketAddress(address, port), motD, false);
                ProxyServer.getInstance().getConfig().getServers().put(name, info);
            }
            ps.close();
            rs.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        ZNetworkPlugin.getInstance().registerEvent("minecraft_server_started", new EventListener() {
            @Override
            public void onEventReceived(String event, String sender, NetworkEvent networkEvent) {
                ServerStatusChangeEvent statusChange = (ServerStatusChangeEvent) networkEvent;
                ServerInfo info = ProxyServer.getInstance().constructServerInfo(statusChange.getServerId(), new InetSocketAddress(statusChange.getAddress(), statusChange.getPort()), motD, false);
                ProxyServer.getInstance().getConfig().getServers().put(statusChange.getServerId(), info);
            }
        });
        ZNetworkPlugin.getInstance().registerEvent("minecraft_server_stopped", new EventListener() {
            @Override
            public void onEventReceived(String s, String s2, NetworkEvent networkEvent) {
                ServerStatusChangeEvent statusChange = (ServerStatusChangeEvent) networkEvent;
                ProxyServer.getInstance().getConfig().getServers().remove(statusChange.getServerId());
            }
        });
    }
}
