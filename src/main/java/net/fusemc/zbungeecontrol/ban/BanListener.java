package net.fusemc.zbungeecontrol.ban;

import com.xxmicloxx.znetworklib.PacketRegistry;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworkplugin.EventListener;
import com.xxmicloxx.znetworkplugin.ZNetworkPlugin;
import net.fusemc.zbungeecontrol.packets.BanPlayerEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Timestamp;

/**
 * Created by Marco on 07.08.2014.
 */
public class BanListener implements Listener, EventListener{

    private BanManager manager;

    public BanListener(BanManager manager) {
        this.manager = manager;
        PacketRegistry.registerPacket(BanPlayerEvent.class, 6977878);

        ZNetworkPlugin.getInstance().registerEvent(BanPlayerEvent.EVENT_NAME, this);
    }

    @Override
    public void onEventReceived(String event, String sender, NetworkEvent data) {
        if (!(data instanceof BanPlayerEvent)) {
            return;
        }
        BanPlayerEvent banData = (BanPlayerEvent) data;
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(banData.getPlayer());
        if (player == null) {
            return;
        }

        Ban ban = new Ban(banData.getReason(), new Timestamp(banData.getTime()));

        player.disconnect(TextComponent.fromLegacyText("\u00A78\u00A7l[\u00A74\u2716\u00A78\u00A7l] \u00A7cDu bist noch \u00A76" + ban.getPrintTime() + " \u00A7cvom Server gebannt: \u00A7a" + ban.getReason()));
    }

    @EventHandler
    public void onConnect(LoginEvent event) {
        Ban ban = manager.isBanned(event.getConnection().getUniqueId().toString());
        if (ban == null) {
            return;
        }
        event.setCancelled(true);
        event.setCancelReason("\u00A78\u00A7l[\u00A74\u2716\u00A78\u00A7l] \u00A7cDu bist noch \u00A76" + ban.getPrintTime() + " \u00A7cvom Server gebannt: \u00A7a" + ban.getReason());
    }

}
