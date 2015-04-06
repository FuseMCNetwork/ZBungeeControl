package net.fusemc.zbungeecontrol.commands;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.utils.MySQLLog;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.packets.BroadcastServerMessageEvent;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.rank.RankManager;
import net.fusemc.zbungeecontrol.server.ServerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Copyright by michidk
 * Created: 12.10.2014.
 */
public class BroadcastServerCommand extends Command {

    public BroadcastServerCommand() {
        super("broadcastserver", null, "sayserver", "ss");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Rank.TEAMLEADER.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /ss <server/this> <nachricht>"));
            return;
        }

        String server = "NONE";
        boolean thiz = false;
        if (ProxyServer.getInstance().getServers().containsKey(args[0])) {
            server = args[0];
        } else if (args[0].equalsIgnoreCase("this") && sender instanceof ProxiedPlayer) {
            server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
            thiz = true;
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Server \u00A73" + server + " \u00A7cexistiert nicht!"));
            return;
        }

        if (!new ServerData(server).isOnline()) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Server \u00A73" + server + " \u00A7cist nicht Online!"));
            return;
        }

        StringBuilder message = new StringBuilder();
        for (String s : args) {
            if (thiz && s.equalsIgnoreCase("this")) continue;
            message.append(s);
            message.append(" ");
        }

        StringBuilder sb = new StringBuilder();
        if (sender instanceof ProxiedPlayer) {
            sb.append("\u00A7f[");
            sb.append(RankManager.getRank(sender.getName()).getColor() + sender.getName());
            sb.append("\u00A7f] \u00A7a");
        }

        sb.append(message);

        ZNetworkPlugin.getInstance().sendEvent(BroadcastServerMessageEvent.EVENT_NAME, new BroadcastServerMessageEvent(Strings.INFO_PREFIX + ChatColor.translateAlternateColorCodes('&', message.toString()), server));
        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "You broadcasted to " + server + ": " + message));

        MySQLLog.logInfo(sender.getName() + " broadcasted to " + server + ": " + message);

    }

}