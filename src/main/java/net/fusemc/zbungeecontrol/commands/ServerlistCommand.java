package net.fusemc.zbungeecontrol.commands;

import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.server.GamemodeData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by michidk on 03.08.2014.
 */
public class ServerlistCommand extends Command implements TabExecutor {

    public ServerlistCommand() {
        super("serverlist", null, "list", "servers");
    }

    public Rank rank = Rank.DEVELOPER;
    public static final String[] OPTIONS = {"single"};    //for tabcomplete
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!rank.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        boolean single = false;
        List<String> options = Arrays.asList(args);
        if (options.contains("single") || options.contains("ps") || options.contains("old")) single = true;

        if (!single) {
            showOverview(sender);
        } else {
            showSingle(sender);
        }

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        if (!rank.isRank(commandSender)) return new ArrayList<>();
        return Arrays.asList(OPTIONS);
    }

    public static void showOverview(CommandSender sender) {
        List<GamemodeData> list = new ArrayList<>();

        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT gamemode, players, max_players, server, max_server FROM bc_list;");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                String name = rs.getString(1);

                int players = rs.getInt(2);
                int maxPlayers = rs.getInt(3);

                int server = rs.getInt(4);
                int maxServer = rs.getInt(5);

                list.add(new GamemodeData(name, players, maxPlayers, server, maxServer));

            }
            rs.close();
            ps.close();
        } catch (SQLException exec) {
            exec.printStackTrace();
        }

        Strings.send(sender, ChatColor.YELLOW + "======= " + ChatColor.GOLD + "FuseMC Server List" + ChatColor.YELLOW + " =======");
        for (GamemodeData data : list) {
            Strings.send(sender, ChatColor.GOLD + data.getName() + ": " +
                    (data.getPlayers() <= 0 ? ChatColor.RED : ChatColor.GREEN) + data.getPlayers() +
                    ChatColor.GREEN + "/" + data.getMaxPlayers() +
                    ChatColor.GRAY + " [" + data.getServer() + "/" + data.getMaxServer() + "]");
        }
    }

    public static void showSingle(CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT name, online, players, max_players FROM runtime_servers;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {

                String server = rs.getString(1);

                boolean online = rs.getInt(2) == 1;

                int players = rs.getInt(3);
                int maxPlayers = rs.getInt(4);

                String color;
                if (online) {
                    color = "\u00A7a";
                } else {
                    color = "\u00A7c";
                }

                String playercount = "";
                if (true)    //showPlayerCount
                    playercount = " \u00A79(\u00A7f" + players + "\u00A77/\u00A7f" + maxPlayers + "\u00A79)";

                sb.append(color);
                sb.append(server);
                sb.append(playercount);

                sb.append("\u00A7f, ");

            }
            ps.close();
            rs.close();
        } catch (SQLException exec) {
            exec.printStackTrace();
        }

        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "\u00A76Server: " + sb.toString()));
    }

}
