package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

public class KickPlayerEvent implements NetworkEvent {

    public static final String EVENT_NAME = "bungeecontrol-kickplayer";

    private String player;
    private String reason;
    private String sender;

    public KickPlayerEvent() {

    }

    /**
     * kicks a palyer from the network
     * @param player    the player that should be kicked
     * @param reason    the reason
     * @param sender    the sender
     */
    public KickPlayerEvent(String player, String reason, String sender) {
        this.player = player;
        this.reason = reason;
        this.sender = sender;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(player);
        writer.writeString(reason);
        writer.writeString(sender);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        player = reader.readString();
        reason = reader.readString();
        sender = reader.readString();
        return CodecResult.OK;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

}
