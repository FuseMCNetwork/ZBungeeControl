package net.fusemc.zbungeecontrol.commands;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.utils.MySQLLog;
import net.fusemc.zbungeecontrol.utils.NetworkHelper;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.packets.MovePlayerEvent;
import net.fusemc.zbungeecontrol.packets.SendMessageEvent;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.rank.RankManager;
import net.fusemc.zbungeecontrol.server.PlayerData;
import net.fusemc.zbungeecontrol.server.ServerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by michidk on 03.08.2014.
 */
public class SendCommand extends Command implements TabExecutor {

    public SendCommand() {
        super("send");
    }

    public Rank rank = Rank.DEVELOPER;

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!rank.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /send <spieler> <server>"));
            return;
        }


        String player = null;
        String server = null;

        if (args.length >= 2) {
            player = args[0];
            server = args[1];
        } else if (args.length == 1) {
            player = sender.getName();
            server = args[0];
        }


        //imposible error
        if (player == null) {
            MySQLLog.logError("SendCommand.java: player == null; server = " + server);
            return;
        } else if (server == null) {
            MySQLLog.logError("SendCommand.java: server == null; player = " + player);
        }


        if (!NetworkHelper.isOnline(player)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Spieler \u00A73" + player + " \u00A7cist nicht online."));
            return;
        }

        if (!ProxyServer.getInstance().getServers().containsKey(server)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Server \u00A73" + server + " \u00A7cexistiert nicht!"));
            return;
        }

        if (!new ServerData(server).isOnline()) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Server \u00A73" + server + " \u00A7cist nicht Online!"));
            return;
        }

        if (new PlayerData(player).getServer().equalsIgnoreCase(server)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Spieler \u00A73" + player + " \u00A7cist bereits auf dem Server \u00A73" + server + "\u00A7c!"));
            return;
        }

        ZNetworkPlugin.getInstance().sendEvent(MovePlayerEvent.EVENT_NAME, new MovePlayerEvent(player, server));
        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Du hast \u00A73" + player + "\u00A7a nach \u00A73" + server + "\u00A7a verschoben!"));
        ZNetworkPlugin.getInstance().sendEvent(SendMessageEvent.EVENT_NAME, new SendMessageEvent(player, Strings.INFO_PREFIX + "Du wurdest von \u00A73" + RankManager.getRank(sender.getName()).getColor() + sender.getName() + "\u00A7a verschoben!"));

        //MySQLLog.logInfo(sender.getName() + " send " + player + " to " + server);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> result = new ArrayList<>();

        if (!rank.isRank(commandSender)) return result;
        if (args.length == 1) {
            String beginPlayer = args[0];

            try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
                PreparedStatement ps = connection.prepareStatement("SELECT name FROM network_users WHERE name LIKE ? AND online = 1;");
                ps.setString(1, beginPlayer + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    result.add(rs.getString(1));
                }
                ps.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (args.length == 2) {
            String begin = args[1];

            try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
                PreparedStatement ps = connection.prepareStatement("SELECT name FROM runtime_servers WHERE name LIKE ? AND online = 1;");
                ps.setString(1, begin + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    result.add(rs.getString(1));
                }
                ps.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

}
