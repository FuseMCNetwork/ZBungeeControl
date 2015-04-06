package net.fusemc.zbungeecontrol.commands;

import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.utils.NetworkHelper;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.packets.PlayerMessageEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by michidk on 04.08.2014.
 */
public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", null, "r", "answer");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Verwendung: /reply <nachricht>"));
            return;
        }

        if (!MSGCommand.lastSent.containsKey(sender.getName())) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Du hast noch keine Nachricht bekommen, auf die du Antworten kannst."));
            return;
        }

        String reciever = MSGCommand.lastSent.get(sender.getName());

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

        String message = sb.toString();

        //sender.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l<\u00A7a\u2709\u00A76\u27B5\u00A78\u00A7l> \u00A7aNachricht an\u00A73 " + player + " \u00A7a gesendet!");
        //NetworkHelper.sendMessage(player, "\u00A78\u00A7l<\u00A76\u27B5\u00A7a\u2709\u00A78\u00A7l> \u00A73" + player + "\u00A78\u00A7l\u00BB \u00A7a" + message);
        //sender.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7a\u2709\u00A76\u27B5 \u00A73" +  RankManager.getRank(player).getColor() + player + "\u00A78\u00A7l] \u00BB \u00A7r\u00A7a" + message));
        //NetworkHelper.sendMessage(player, "\u00A78\u00A7l[\u00A76\u2709\u00A78\u00A7l] \u00A73" + RankManager.getRank(sender.getName()).getColor() + sender.getName() + " \u00A73" + player + "\u00A78\u00A7l\u00BB \u00A7a" + message);

        sender.sendMessage(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A7a\u2709\u00A76\u27B5\u00A78\u00A7l] \u00A73" + reciever + " \u00A78\u00A7l\u00BB \u00A7a" + message));
        ZNetworkPlugin.getInstance().sendEvent(PlayerMessageEvent.EVENT_NAME, new PlayerMessageEvent(reciever, "\u00A78\u00A7l[\u00A76\u27B5\u00A7a\u2709\u00A78\u00A7l] \u00A73" + sender.getName() + " \u00A78\u00A7l\u00BB \u00A7a" + message, sender.getName()));

        MSGCommand.lastSent.put(reciever, sender.getName());
    }

}