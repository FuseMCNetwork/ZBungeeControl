package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.PacketRegistry;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworkplugin.EventListener;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.commands.MSGCommand;
import net.fusemc.zbungeecontrol.rank.Rank;
import net.fusemc.zbungeecontrol.server.ServerData;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

import java.util.Map;

public class PacketHandler implements Listener {

    public static void registerEvents() {
        Class[] packets = {
                BroadcastMessageEvent.class,
                KickPlayerEvent.class,
                MovePlayerEvent.class,
                SendMessageEvent.class
        };

        PacketRegistry.registerPacket(BroadcastMessageEvent.class, 665340);
        PacketRegistry.registerPacket(KickPlayerEvent.class, 665341);
        PacketRegistry.registerPacket(MovePlayerEvent.class, 665342);
        PacketRegistry.registerPacket(SendMessageEvent.class, 665343);
        PacketRegistry.registerPacket(PlayerMessageEvent.class, 665344);
        PacketRegistry.registerPacket(BroadcastServerMessageEvent.class, 665345);

        //BROADCAST
        ZNetworkPlugin.getInstance().registerEvent(BroadcastMessageEvent.EVENT_NAME, new EventListener() {
            @Override
            public void onEventReceived(String event, String sender, NetworkEvent data) {
                if (!(data instanceof BroadcastMessageEvent)) {
                    return;
                }
                BroadcastMessageEvent broadcastMessageEvent = (BroadcastMessageEvent) data;
                ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(broadcastMessageEvent.getMessage()));
            }
        });
        //SERVERBROADCAST
        ZNetworkPlugin.getInstance().registerEvent(BroadcastServerMessageEvent.EVENT_NAME, new EventListener() {
            @Override
            public void onEventReceived(String event, String sender, NetworkEvent data) {
                if (!(data instanceof BroadcastServerMessageEvent)) {
                    return;
                }
                BroadcastServerMessageEvent broadcastServerMessageEvent = (BroadcastServerMessageEvent) data;
                ServerData server = new ServerData(broadcastServerMessageEvent.getServer());
                if (server.exists() && server.isOnline()) {
                    for (ProxiedPlayer p:ProxyServer.getInstance().getPlayers()) {
                        if (p.getServer().getInfo().getName().equals(server.getName())) {
                            p.sendMessage(TextComponent.fromLegacyText(broadcastServerMessageEvent.getMessage()));
                        }
                    }
                }
            }
        });
        //KICK
        ZNetworkPlugin.getInstance().registerEvent(KickPlayerEvent.EVENT_NAME, new EventListener() {
            @Override
            public void onEventReceived(String event, String sender, NetworkEvent data) {
                if (!(data instanceof KickPlayerEvent)) {
                    return;
                }
                KickPlayerEvent kickPlayerEvent = (KickPlayerEvent) data;
                String playerName = kickPlayerEvent.getPlayer();
                String pSender = kickPlayerEvent.getSender();
                String reason = kickPlayerEvent.getReason();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
                if (player == null) {
                    return;
                }
                player.disconnect(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A74\u2716\u00A78\u00A7l] \u00A7cDu wurdest von \u00A7b" + pSender + " \u00A7cgekickt!\n\u00A7cGrund: \n\u00A76" + reason));
            }
        });
        //MOVE
        ZNetworkPlugin.getInstance().registerEvent(MovePlayerEvent.EVENT_NAME, new EventListener() {
            @Override
            public void onEventReceived(String event, String sender, NetworkEvent data) {
                if (!(data instanceof MovePlayerEvent)) {
                    return;
                }
                MovePlayerEvent movePlayerEvent = (MovePlayerEvent) data;
                String playerName = movePlayerEvent.getPlayer();
                String target = movePlayerEvent.getServer();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
                if (player == null) {
                    return;
                }
                if (player.getServer().getInfo().getName().equals(target)) {
                    //player already on server
                    return;
                }
                for (Map.Entry<String, ServerInfo> entry : ProxyServer.getInstance().getConfig().getServers().entrySet()) {
                    if (entry.getKey().equals(target)) {
                        player.connect(entry.getValue());
                        return;
                    }
                }
            }
        });
        //MESSAGE
        ZNetworkPlugin.getInstance().registerEvent(SendMessageEvent.EVENT_NAME, new EventListener() {
            @Override
            public void onEventReceived(String event, String sender, NetworkEvent data) {
                if (!(data instanceof SendMessageEvent)) {
                    return;
                }
                SendMessageEvent sendMessageEvent = (SendMessageEvent) data;
                String playerName = sendMessageEvent.getPlayer();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
                if (player == null) {
                    return;
                }
                player.sendMessage(TextComponent.fromLegacyText(sendMessageEvent.getMessage()));
            }
        });
        //PLAYERMESSAGE
        ZNetworkPlugin.getInstance().registerEvent(PlayerMessageEvent.EVENT_NAME, new EventListener() {
            @Override
            public void onEventReceived(String event, String sender, NetworkEvent data) {
                if (!(data instanceof PlayerMessageEvent)) {
                    return;
                }
                PlayerMessageEvent playerMessageEvent = (PlayerMessageEvent) data;
                String playerName = playerMessageEvent.getPlayer();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
                if (player == null) {
                    return;
                }
                player.sendMessage(TextComponent.fromLegacyText(playerMessageEvent.getMessage()));
                MSGCommand.lastSent.put(playerName, playerMessageEvent.getSender());
            }
        });
    }

}
