package net.fusemc.zbungeecontrol.utils;


import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by michidk on 05.08.2014.
 */
public class MySQLLog {

    public static void logInfo(String message) {
        log("[INFO] " + message);
    }

    public static void logWarning(String message) {
        log("[WARNING] " + message);
    }

    public static void logError(String message) {
        log("[ERROR] " + message);
    }

    private static void log(String message) {
        message = TextComponent.toPlainText(TextComponent.fromLegacyText(message));
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO log (action) VALUES (?);");
            ps.setString(1, message);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
