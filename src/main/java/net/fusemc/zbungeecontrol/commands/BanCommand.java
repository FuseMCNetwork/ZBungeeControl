package net.fusemc.zbungeecontrol.commands;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.utils.MySQLLog;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.ban.Ban;
import net.fusemc.zbungeecontrol.packets.BanPlayerEvent;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.*;
import java.util.ArrayList;

public class BanCommand extends Command implements TabExecutor {

    private static final Rank RANK = Rank.SUPPORTER;

    public BanCommand() {
        super("ban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {  //player will be banned (mysql) with command, not packet
        if (!RANK.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /ban <player> <zeit: d, h, m, s> <grund>"));
            return;
        }
        String uuid = getUuid(args[0]);
        if(uuid == null){
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Dieser Spieler ist nicht in der Datenbank verzeichnet!"));
            return;
        }
        long d = 0;
        long h = 0;
        long m = 0;
        long s = 0;
        try {
            String arg = args[1];
            if(arg.endsWith("d")){
                d = Integer.parseInt(arg.replaceFirst("d", ""));
            } else if (arg.endsWith("h")){
                h = Integer.parseInt(arg.replaceFirst("h", ""));
            } else if (arg.endsWith("m")){
                m = Integer.parseInt(arg.replaceFirst("m", ""));
            } else if (arg.endsWith("s")){
                s = Integer.parseInt(arg.replaceFirst("s", ""));
            }
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        if (d == 0 && h == 0 && m == 0 && s == 0){
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /ban <player> <zeit: d, h, m, s> <grund>"));
            return;
        }

        long time = ((d * 86400) + (h * 3600) + (m * 60) + s) * 1000;

        if(!Rank.TEAMLEADER.isRank(sender)){
            //trim ban for support only to max 3 days
            if (time > 259200000) {
                time = 259200000;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int x = 2; x < args.length; x++) {
            sb.append(args[x]);
            sb.append(" ");
        }
        String result = sb.toString();
        Ban ban = new Ban(result, new Timestamp(System.currentTimeMillis() + time));

        ZBungeeControl.getBanManager().ban(uuid, result, ban.getTime(), sender.getName());
        MySQLLog.logInfo(sender.getName() + " banned " + args[0] + " for " + ban.getPrintTime() + ": " + result);

        sender.sendMessage(TextComponent.fromLegacyText("\u00A7fDu hast \u00A73" + args[0] + "\u00a7f für \u00a76 " + ban.getPrintTime() + "\u00A7f vom Server gebannt: \u00A7a" + result));
        ZNetworkPlugin.getInstance().sendEvent(BanPlayerEvent.EVENT_NAME, new BanPlayerEvent(args[0], result, ban.getTime().getTime(), sender.getName()));
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
