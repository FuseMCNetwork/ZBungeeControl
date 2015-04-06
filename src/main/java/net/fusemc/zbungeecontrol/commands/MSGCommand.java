package net.fusemc.zbungeecontrol.commands;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.utils.NetworkHelper;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.packets.PlayerMessageEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by michidk on 04.08.2014.
 */
public class MSGCommand extends Command implements Listener, TabExecutor {

    public static Map<String, String> lastSent = new HashMap<>();   //reciever, sender

    public MSGCommand() {
        super("msg", null, "m", "message");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /msg <spieler> <nachricht>"));
            return;
        }

        String reciever = args[0];

        if (reciever.equals(sender.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Du kannst dir selbst keine Nachricht senden."));
            return;
        }

        if (!NetworkHelper.isOnline(reciever)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Der Spieler \u00A73" + reciever + " \u00A7cist nicht online."));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String s : args) {
            sb.append(s);
            sb.append(" ");
        }

        String message = sb.toString().replaceFirst(reciever + " ", "");

        sender.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7a\u2709\u00A76\u27B5\u00A78\u00A7l] \u00A73" + reciever + " \u00A78\u00A7l\u00BB \u00A7a" + message));
        ZNetworkPlugin.getInstance().sendEvent(PlayerMessageEvent.EVENT_NAME, new PlayerMessageEvent(reciever, "\u00A78\u00A7l[\u00A76\u27B5\u00A7a\u2709\u00A78\u00A7l] \u00A73" + sender.getName() + " \u00A78\u00A7l\u00BB \u00A7a" + message, sender.getName()));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ArrayList<String> result = new ArrayList<>();

        if (!(args.length == 0 || args.length == 1)) return result;

        String begin = "";
        if (args.length == 1) {
            begin = args[0];
        }

        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            if (player.getName().toLowerCase().startsWith(begin.toLowerCase()))
                result.add(player.getName());
        }

        return result;
    }

}