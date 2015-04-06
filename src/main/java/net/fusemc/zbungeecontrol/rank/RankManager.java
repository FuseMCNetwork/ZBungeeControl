package net.fusemc.zbungeecontrol.rank;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marco on 28.07.2014.
 */
public class RankManager {

    private static final Map<String, Rank> players = new ConcurrentHashMap<String, Rank>();

    private RankListener listener;

    public RankManager() {
        listener = new RankListener(this);
    }

    public static Rank getRank(String player) {
        if (player.equalsIgnoreCase("CONSOLE")) {
            return Rank.CONSOLE;
        }

        return players.get(player);
    }

    public RankListener getListener() {
        return listener;
    }

    public void set(String player, Rank rank) {
        players.put(player, rank);
    }

    public void remove(String player) {
        players.remove(player);
    }
}
