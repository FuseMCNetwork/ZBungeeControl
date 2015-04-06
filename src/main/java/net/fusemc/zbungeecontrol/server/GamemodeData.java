package net.fusemc.zbungeecontrol.server;

import net.fusemc.zbungeecontrol.ZBungeeControl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Marco on 28.07.2014.
 */
public class GamemodeData {

    /**
     * The name of the gamemode
     * e.g "PORTALWAR"
     */
    private String name;
    /**
     * how much players are online on that specific gamemode
     */
    private int players;
    /**
     * how much players can max be on this gamemode
     */
    private int maxPlayers;
    /**
     * how much server of this gamemode are online?
     */
    private int server;
    /**
     * how much server can the gamemode max have
     */
    private int maxServer;

    public GamemodeData(String name) {
        this.name = name;
        update();
    }

    public GamemodeData(String name, int players, int maxPlayers, int server, int maxServer) {
        this.name = name;
        this.players = players;
        this.maxPlayers = maxPlayers;
        this.server = server;
        this.maxServer = maxServer;
    }

    public void update() {
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT players, max_players, server, max_server FROM bc_list WHERE name=?;");
            ps.setString(1, name);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                players = rs.getInt(1);
                maxPlayers = rs.getInt(2);

                server = rs.getInt(3);
                maxServer = rs.getInt(4);

            }
            rs.close();
            ps.close();
        } catch (SQLException exec) {
            exec.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public int getPlayers() {
        return players;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getServer() {
        return server;
    }

    public int getMaxServer() {
        return maxServer;
    }
}
