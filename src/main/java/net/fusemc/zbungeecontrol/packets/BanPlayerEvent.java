package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

public class BanPlayerEvent implements NetworkEvent {

    public static final String EVENT_NAME = "bungeecontrol-banplayer";

    private String player;
    private String reason;
    private long millis;
    private String sender;

    public BanPlayerEvent() {

    }

    /**
     * bans a player for a certain time
     * @param player    the player
     * @param reason    the reason
     * @param millis    the time the player is unbanned in millis
     * @param sender    the sender of the ban
     */
    public BanPlayerEvent(String player, String reason, long millis, String sender) {
        this.player = player;
        this.reason = reason;
        this.millis = millis;
        this.sender = sender;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(player);
        writer.writeString(reason);
        writer.writeLong(millis);
        writer.writeString(sender);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        player = reader.readString();
        reason = reader.readString();
        millis = reader.readLong();
        sender = reader.readString();
        return CodecResult.OK;
    }

    public String getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

    public long getTime() {
        return millis;
    }

    public String getSender() {
        return sender;
    }

}
