package net.fusemc.zbungeecontrol.ban;

import net.fusemc.zbungeecontrol.ZBungeeControl;

import java.sql.*;
import java.util.UUID;

/**
 * Created by Marco on 07.08.2014.
 */
public class BanManager {

    private BanListener listener;

    public BanManager() {
        this.listener = new BanListener(this);
    }

    public Ban isBanned(String uuid) {
        try (Connection connection = ZBungeeControl.getPlayerMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT ban_end, reason FROM bans WHERE uuid=?;");
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            Ban result = null;
            if (rs.next()) {
                Timestamp time = rs.getTimestamp(1);
                String reason = rs.getString(2);
                long deltaTime = time.getTime() - System.currentTimeMillis();
                if (deltaTime < 0) {
                    removeBan(uuid);
                } else {
                    result = new Ban(reason, time);
                }
            }
            ps.close();
            rs.close();
            return result;
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
        return null;
    }

    public void removeBan(String uuid) {
        try (Connection connection = ZBungeeControl.getPlayerMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM bans WHERE uuid=?;");
            ps.setString(1, uuid);
            ps.execute();
            ps.close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public void ban(String uuid, String reason, Timestamp timestamp, String sender) {
        try (Connection connection = ZBungeeControl.getPlayerMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO bans (uuid, reason, ban_end, sender) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE reason = VALUES(reason), ban_end = VALUES(ban_end), sender = VALUES(sender);");
            ps.setString(1, uuid);
            ps.setString(2, reason);
            ps.setTimestamp(3, timestamp);
            ps.setString(4, sender);
            ps.execute();
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public BanListener getListener() {
        return listener;
    }
}
