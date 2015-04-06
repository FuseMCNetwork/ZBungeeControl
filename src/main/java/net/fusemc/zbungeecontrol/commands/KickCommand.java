package net.fusemc.zbungeecontrol.commands;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.utils.MySQLLog;
import net.fusemc.zbungeecontrol.utils.NetworkHelper;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.packets.KickPlayerEvent;
import net.fusemc.zbungeecontrol.rank.Rank;
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
public class KickCommand extends Command implements TabExecutor {

    public KickCommand() {
        super("kick");
    }

    public Rank rank = Rank.SUPPORTER;

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!rank.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /kick <spieler> <grund>"));
            return;
        }

        String player = args[0];
        if (player.equals(sender.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Du kannst dich selbst nicht kicken."));
            return;
        }

        if (!NetworkHelper.isOnline(player)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Spieler \u00A73" + player + " \u00A7cist nicht online."));
            return;
        }

        /*  geht nicht weil, man raenge auf anderen bungees nicht checken kann
        Rank exec = RankManager.getRank(sender.getName());
        Rank victim = RankManager.getRank(player);
        if (victim.getRanking() >= exec.getRanking())
        {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Ein " + exec.getColor() + exec.getName() + " \u00A73( " + sender.getName() + " ) \u00A7ckann keinen " + victim.getColor() + victim.getName() + " \u00A73( " + player + " ) \u00A7ckicken!"));
            return;
        }
        */

        String reason = "kein Grund angegeben";
        if (args.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (String s : args) {
                sb.append(s);
                sb.append(" ");
            }
            reason = sb.toString().replaceFirst(player, "");
        }

        ZNetworkPlugin.getInstance().sendEvent(KickPlayerEvent.EVENT_NAME, new KickPlayerEvent(player, reason, sender.toString()));
        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "Du hast den Spieler \u00A73" + player + " \u00A7amit dem Grund: \u00A76" + reason + "\u00A7avom Server gekickt."));

        MySQLLog.logInfo(sender.getName() + " kicked " + player + " with reason: " + reason);

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
