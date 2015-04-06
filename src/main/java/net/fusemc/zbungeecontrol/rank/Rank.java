package net.fusemc.zbungeecontrol.rank;

import net.fusemc.zbungeecontrol.ZBungeeControl;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public enum Rank {

    CONSOLE(999, ChatColor.DARK_RED, "console", "\u00A7f[\u00A74Console\u00A7f] ", " \u00A78\u00BB\u00A7f "),
    ADMIN(100, ChatColor.DARK_RED, "admin", "\u00A7f[\u00A74Admin\u00A7f] ", " \u00A78\u00BB\u00A7f "),
    DEVELOPER(90, ChatColor.GRAY, "developer", "\u00A7f[\u00A77Dev\u00A7f] ", " \u00A78\u00BB\u00A7f "),
    TEAMLEADER(75, ChatColor.DARK_AQUA, "teamleader", "\u00A7f[\u00A73Teamleiter\u00A7f] ", " \u00A78\u00BB\u00A7f "),
    SUPPORTER(65, ChatColor.AQUA, "supporter", "\u00A7f[\u00A7bSupporter\u00A7f] ", " \u00A78\u00BB\u00A7f "),
    BUILDER(50, ChatColor.BLUE, "builder", "\u00A7f[\u00A79Architekt\u00A7f] ", " \u00A78\u00BB\u00A7f "),
    YOUTUBER(30, ChatColor.DARK_PURPLE, "youtuber", "\u00A7f[\u00A75Youtuber\u00A7f] ", " \u00A78\u00BB\u00A7f "),
    USER(1, ChatColor.GREEN, "user", "\u00A7a", " \u00A78\u00BB\u00A77 ");

    //to compare different ranks
    private int ranking;
    //for the tablist and website
    private ChatColor color;
    //for the DB
    private String name;
    //for the chat (name color)
    private String prefix;
    //for the chat (message color)
    private String suffix;


    Rank(int ranking, ChatColor color, String name, String prefix, String suffix) {
        this.ranking = ranking;
        this.color = color;
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public static Rank getRankFromString(String s) {
        for (Rank r : Rank.values()) {
            if (r.name().equalsIgnoreCase(s))
                return r;
        }
        return Rank.USER;
    }

    public static boolean isTeam(String p) {
        Rank rank = ZBungeeControl.getRankManager().getRank(p);
        return (rank.getRanking() >= 50);
    }

    public static Rank getDefault() {
        return Rank.USER;
    }

    public static Rank from(String name) {
        for (Rank rank : values()) {
            if (rank.getName().equalsIgnoreCase(name)) {
                return rank;
            }
        }

        return getDefault();
    }

    public static Rank[] from(Rank... values) {
        Rank[] ranks = new Rank[values.length];
        for (int i = 0; i < values.length; i++) {
            ranks[i] = values[i];
        }

        return ranks;
    }


    public boolean isRank(String p) {
        return ZBungeeControl.getRankManager().getRank(p).getRanking() >= this.getRanking();
    }

    public boolean isRank(CommandSender commandSender) {
        if (!(commandSender instanceof ProxiedPlayer)) return true;
        ProxiedPlayer p = (ProxiedPlayer) commandSender;
        return isRank(p.getName());
    }

    public boolean isExactRank(String p) {
        return ZBungeeControl.getRankManager().getRank(p) == this;
    }

    public int getRanking() {
        return this.ranking;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getName() {
        return this.name;
    }

    public ChatColor getColor() {
        return color;
    }
}