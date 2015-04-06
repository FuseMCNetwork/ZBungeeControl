package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

/**
 * Copyright by michidk
 * Created: 12.10.2014.
 */
public class BroadcastServerMessageEvent implements NetworkEvent {

    public static final String EVENT_NAME = "bungeecontrol-broadcastservermessage";

    private String message;
    private String server;

    public BroadcastServerMessageEvent() {

    }

    /**
     * broadcast a message to a certain server
     * @param message   the message that should be broadcasted to
     * @param server    the server that should recieve the broadcast
     */
    public BroadcastServerMessageEvent(String message, String server) {
        this.message = message;
        this.server = server;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(message);
        writer.writeString(server);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        message = reader.readString();
        server = reader.readString();
        return CodecResult.OK;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
