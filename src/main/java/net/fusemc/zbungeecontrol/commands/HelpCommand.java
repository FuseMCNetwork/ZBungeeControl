package net.fusemc.zbungeecontrol.commands;

import net.fusemc.zbungeecontrol.utils.Strings;
import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by michidk on 03.08.2014.
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", null, "commands", "cmds", "command", "cmd", "commandlist");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!Rank.DEVELOPER.isRank(sender)) {
            sender.sendMessage(TextComponent.fromLegacyText(Strings.UNKNOWNCOMMAND_COMMAND));
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Command command : ZBungeeControl.getInstance().commands) {
            sb.append("/\u00A76");
            sb.append(command.getName());
            sb.append("\u00A7f, ");
        }

        sender.sendMessage(TextComponent.fromLegacyText(Strings.INFO_PREFIX + "\u00A76Commands: \u00A7f" + sb.toString()));

    }

}
