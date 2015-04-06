package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

public class SendMessageEvent implements NetworkEvent {

    public static final String EVENT_NAME = "bungeecontrol-chatmessage";

    private String player;
    private String message;

    public SendMessageEvent() {

    }

    /**
     * sends a raw message to a player
     * (you cant use /reply)
     * @param player
     * @param message
     */
    public SendMessageEvent(String player, String message) {
        this.player = player;
        this.message = message;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(player);
        writer.writeString(message);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        player = reader.readString();
        message = reader.readString();
        return CodecResult.OK;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
