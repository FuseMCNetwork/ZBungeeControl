package net.fusemc.zbungeecontrol.commands;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.utils.MySQLLog;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.packets.BroadcastMessageEvent;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.rank.RankManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by michidk on 03.08.2014.
 */
public class BroadcastCommand extends Command {

    public BroadcastCommand() {
        super("broadcast", null, "say");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Rank.TEAMLEADER.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /say <nachricht>"));
            return;
        }


        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s);
            sb.append(" ");
        }

        String message = sb.toString();   //when message like: "/say me ich bin cool" -> "[i] [michidk] ich bin cool"
        if (args[0].equalsIgnoreCase("me") && sender instanceof ProxiedPlayer) {
            message = message.replaceFirst("me ", "");
            message = "\u00A7f[" + RankManager.getRank(sender.getName()).getColor() + sender.getName() + "\u00A7f] \u00A7a" + message;
        }

        ZNetworkPlugin.getInstance().sendEvent(BroadcastMessageEvent.EVENT_NAME, new BroadcastMessageEvent(Strings.INFO_PREFIX + ChatColor.translateAlternateColorCodes('&', message)));

        MySQLLog.logInfo(sender.getName() + " broadcasted: " + message);

    }

}
