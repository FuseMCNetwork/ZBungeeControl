package net.fusemc.zbungeecontrol;

import net.fusemc.zbungeecontrol.ban.BanManager;
import net.fusemc.zbungeecontrol.commands.*;
import net.fusemc.zbungeecontrol.mysql.MySQL;
import net.fusemc.zbungeecontrol.mysql.MySQLDBType;
import net.fusemc.zbungeecontrol.packets.PacketHandler;
import net.fusemc.zbungeecontrol.rank.RankManager;
import net.fusemc.zbungeecontrol.utils.Strings;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

/**
 * Created by michidk on 28.07.2014.
 */
public class ZBungeeControl extends Plugin {

    private static ZBungeeControl instance;
    public MySQL networkMysql;
    public MySQL playerMysql;
    private RankManager rankManager;
    private BanManager banManager;

    public Command[] commands;
    public Listener[] listeners;

    public static boolean shutdown = false;

    @Override
    public void onEnable() {
        instance = this;

        rankManager = new RankManager();
        banManager = new BanManager();

        networkMysql = new MySQL(MySQLDBType.NETWORK);
        playerMysql = new MySQL(MySQLDBType.PLAYER);

        PacketHandler.registerEvents();

        PluginManager pm = getProxy().getPluginManager();

        ServerStatusListener.register();

        StopCommand stopCommand = new StopCommand();    //listener & command

        //Listener
        listeners = new Listener[] {
                new MySQLHandler(),
                rankManager.getListener(),
                banManager.getListener(),
                new LobbyListener(),
                new PlayerCountHandler(),
                stopCommand
        };
        for (Listener listener : listeners) pm.registerListener(this, listener);

        //Commands
        commands = new Command[] {
                new BanCommand(),
                new UnbanCommand(),
                new BroadcastCommand(),
                new HelpCommand(),
                new KickCommand(),
                new LobbyCommand(),
                new MSGCommand(),
                new PlayerDataCommand(),
                new ReplyCommand(),
                new SendCommand(),
                new ServerDataCommand(),
                new ServerlistCommand(),
                new BroadcastServerCommand(),
                stopCommand

        };
        for (Command command : commands) pm.registerCommand(this, command);

        ProxyServer.getInstance().setReconnectHandler(new LobbyListener.MySQLReconnectHandler());

        getProxy().getPluginManager().registerListener(this, new BungeeShutdownListener());
    }

    @Override
    public void onDisable() {
        for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
            p.disconnect(TextComponent.fromLegacyText(Strings.ERROR_PREFIX + "Das Netzwerk wurde deaktiviert und sollte in wenigen Minuten wieder Online sein."));
        }
        MySQLHandler.handleShutdown();
    }

    public static RankManager getRankManager() {
        return instance.rankManager;
    }

    public static MySQL getNetworkMysql() {
        return instance.networkMysql;
    }

    public static MySQL getPlayerMysql() {
        return instance.playerMysql;
    }

    public static ZBungeeControl getInstance() {
        return instance;
    }

    public static BanManager getBanManager(){
        return instance.banManager;
    }
}
