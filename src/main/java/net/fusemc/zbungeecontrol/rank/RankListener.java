package net.fusemc.zbungeecontrol.rank;

import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.mysql.MySQL;
import net.fusemc.zbungeecontrol.mysql.MySQLDBType;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Marco on 28.07.2014.
 */
public class RankListener implements Listener {

    private RankManager manager;

    public RankListener(RankManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerJoin(LoginEvent event) {
        Rank rank = getRank(event.getConnection().getUniqueId().toString());
        manager.set(event.getConnection().getName(), rank);
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        manager.remove(event.getPlayer().getName());
    }

    public Rank getRank(String uuid) {
        try (Connection connection = ZBungeeControl.getPlayerMysql().getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT rank FROM rank WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            Rank rank = null;
            if (result.next()) {
                rank = Rank.getRankFromString(result.getString("rank"));
            }

            statement.close();
            result.close();
            if (rank == null) {
                return Rank.USER;
            }
            return rank;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Rank.USER;
    }
}
