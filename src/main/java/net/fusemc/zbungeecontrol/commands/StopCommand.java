package net.fusemc.zbungeecontrol.commands;

import net.fusemc.zbungeecontrol.MySQLHandler;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Copyright by michidk
 * Created: 24.10.2014.
 */
public class StopCommand extends Command implements Listener {

    public StopCommand() {
        super("stop", null, "end", "exit");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if (Rank.DEVELOPER.isRank(sender)) {
                sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Can only be executed from console"));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            }
            return;
        }

        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "you stopped the bungeecord " + ZBungeeControl.getInstance().getProxy().getName()));

        ZBungeeControl.shutdown = true;

        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            p.disconnect(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Das Netzwerk wurde herruntergefahren und sollte in wenigen Minuten wieder Erreichbar sein."));
        }

        ProxyServer.getInstance().getScheduler().schedule(ZBungeeControl.getInstance(), new Runnable() {
            @Override
            public void run() {
                MySQLHandler.handleShutdown();
            }
        }, 1, TimeUnit.SECONDS);
        ProxyServer.getInstance().getScheduler().schedule(ZBungeeControl.getInstance(), new Runnable() {
            @Override
            public void run() {
                ProxyServer.getInstance().stop();
            }
        }, 2, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onJoin(ServerConnectEvent event) {
        if (ZBungeeControl.shutdown) {
            event.getPlayer().disconnect(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Das Netzwerk wurde heruntergefahren und sollte in wenigen Minuten wieder erreichbar sein."));
        }
    }
}
