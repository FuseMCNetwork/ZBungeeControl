package net.fusemc.zbungeecontrol.utils;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by michidk on 04.08.2014.
 */
public class Strings {

    public final static String COLOR_CHAR = "\u00A7";

    public final static String INFO_PREFIX = "\u00A78\u00A7l[\u00A79\u00A7li\u00A78\u00A7l] \u00A7a";
    public final static String ERROR_PREFIX = "\u00A78\u00A7l[\u00A74\u2716\u00A78\u00A7l] \u00A7c";

    public final static String UNKNOWNCOMMAND_COMMAND = "\u00A78\u00A7l[\u00A74\u2716\u00A78\u00A7l] \u00A7cUnbekannter Befehl!";


    public static void send(CommandSender sender, String message) {
        sender.sendMessage(TextComponent.fromLegacyText(message));
    }

    public static void send(ProxiedPlayer player, String message) {
        player.sendMessage(TextComponent.fromLegacyText(message));
    }

}
