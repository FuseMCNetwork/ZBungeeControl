package net.fusemc.zbungeecontrol.commands;

import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.server.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by michidk on 03.08.2014.
 */
public class PlayerDataCommand extends Command implements TabExecutor {

    public Rank rank = Rank.DEVELOPER;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    public PlayerDataCommand() {
        super("playerdata", null, "playerinfo", "pd", "player", "find", "whereis", "whereami");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!rank.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /player <player>"));
            return;
        }

        String player = args[0];
        if (player.equalsIgnoreCase("me")) {
            player = sender.getName();
        }

        PlayerData pd = new PlayerData(player);
        if (!pd.exists()) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Dieser Spieler exestiert nicht!"));
            return;
        }

        String online;
        if (pd.isOnline()) {
            online = "\u00A7aJa";
        } else {
            online = "\u00A7cNein";
        }

        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "\u00A76" + pd.getName() + ":"));
        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Online: " + online));
        if (pd.isOnline()) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Server: \u00A73" + pd.getServer()));
            sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Bungee: \u00A73" + pd.getBungee()));
        }
        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Last Seen: \u00A73" + dateFormat.format(pd.getLastSeen())));
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
            PreparedStatement ps = connection.prepareStatement("SELECT name FROM network_users WHERE name LIKE ? AND online = 1;");
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
