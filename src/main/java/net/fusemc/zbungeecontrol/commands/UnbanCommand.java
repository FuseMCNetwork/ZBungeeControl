package net.fusemc.zbungeecontrol.commands;

import net.fusemc.zbungeecontrol.utils.MySQLLog;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.rank.Rank;
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

/**
 * Created by Marco on 02.10.2014.
 */
public class UnbanCommand extends Command implements TabExecutor{

    private static final Rank RANK = Rank.SUPPORTER;

    public UnbanCommand() {
        super("unban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!RANK.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }
        if(args.length != 1) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /unban <player>"));
            return;
        }
        String uuid = getUuid(args[0]);
        if(uuid == null){
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Dieser Spieler ist nicht in der Datenbank verzeichnet!"));
            return;
        }
        if(ZBungeeControl.getBanManager().isBanned(uuid) == null) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Dieser Spieler ist nicht gebannt!"));
            return;
        }
        ZBungeeControl.getBanManager().removeBan(uuid);
        sender.sendMessage(TextComponent.fromLegacyText("\u00a7fDu hast \u00a73" + args[0] + "\u00a7f entbannt!"));
        MySQLLog.logInfo(sender.getName() + " unbanned " + args[0]);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> result = new ArrayList<>();

        if (!Rank.DEVELOPER.isRank(commandSender)) return result;
        if (!(args.length == 0 || args.length == 1)) return result;

        String begin = "";
        if (args.length == 1) {
            begin = args[0];
        }

        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()) {
            PreparedStatement ps = connection.prepareStatement("SELECT name FROM network_users WHERE name LIKE ?;");
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

    private String getUuid(String player){
        try (Connection connection = ZBungeeControl.getNetworkMysql().getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT uuid FROM network_users WHERE name LIKE ?;");
            ps.setString(1, player);
            ResultSet rs = ps.executeQuery();
            String result = null;
            if(rs.next()){
                result = rs.getString(1);
            }
            ps.close();
            rs.close();
            return result;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
