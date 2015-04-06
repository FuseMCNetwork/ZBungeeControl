package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

public class MovePlayerEvent implements NetworkEvent {

    public static final String EVENT_NAME = "bungeecontrol-moveplayer";

    private String player;
    private String server;

    public MovePlayerEvent() {

    }

    /**
     * moves a player from a server to another server
     * @param player    the player
     * @param server    the server the player will be moved to
     */
    public MovePlayerEvent(String player, String server) {
        this.player = player;
        this.server = server;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(player);
        writer.writeString(server);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        player = reader.readString();
        server = reader.readString();
        return CodecResult.OK;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

}
