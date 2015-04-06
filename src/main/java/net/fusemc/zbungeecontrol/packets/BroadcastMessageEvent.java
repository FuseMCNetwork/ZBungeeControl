package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

public class BroadcastMessageEvent implements NetworkEvent {

    public static final String EVENT_NAME = "bungeecontrol-broadcastmessage";

    private String message;

    public BroadcastMessageEvent() {

    }

    /**
     * broadcast a message above the whole network
     * @param message   the message that should be broadcasted
     */
    public BroadcastMessageEvent(String message) {
        this.message = message;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(message);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        message = reader.readString();
        return CodecResult.OK;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
