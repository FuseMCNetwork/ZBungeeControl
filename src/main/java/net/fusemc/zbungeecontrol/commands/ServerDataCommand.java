package net.fusemc.zbungeecontrol.commands;

import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.server.ServerData;
import net.md_5.bungee.api.CommandSender;
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
public class ServerDataCommand extends Command implements TabExecutor {

    public ServerDataCommand() {
        super("serverdata", null, "serverinfo", "sd", "server");
    }

    public Rank rank = Rank.DEVELOPER;

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!rank.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /server <server>"));
            return;
        }

        String server = args[0];

        ServerData sd = new ServerData(server);
        if (!sd.exists()) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Dieser Server exestiert nicht!"));
            return;
        }

        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "\u00A76" + sd.getName() + "\u00A7f:"));
        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Spieler: \u00A73" + sd.getCurrentPlayers()));
        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Max Spieler: \u00A73" + sd.getMaxPlayers()));

    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> result = new ArrayList<>();

        if (!rank.isRank(commandSender)) return result;
        if (!(args.length == 0 || args.length == 1)) return result;

        String begin = "";
        if (args.length == 1) {
            begin = args[0];
        }

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

        return result;
    }

}
