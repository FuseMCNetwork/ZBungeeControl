package net.fusemc.zbungeecontrol.commands;

import net.fusemc.zbungeecontrol.LobbyListener;
import net.fusemc.zbungeecontrol.utils.NetworkHelper;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by michidk on 05.08.2014.
 */
public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", null, "exit", "leave", "hub");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Nicht von der Console ausf\u00FChrbar!"));
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (NetworkHelper.isOnLobbyServer(player)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Du befindest dich bereits in einer Lobby."));
            return;
        }
        player.connect(LobbyListener.getTargetServer());
    }
}
