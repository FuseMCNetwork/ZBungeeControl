package net.fusemc.zbungeecontrol;

import net.md_5.bungee.api.event.BungeeShutdownEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Copyright by michidk
 * Created: 12.10.2014.
 */
public class BungeeShutdownListener implements Listener {

    @EventHandler
    public void onBungeeShutdown(BungeeShutdownEvent event) {
        ZBungeeControl.getInstance().onDisable();
    }

}
