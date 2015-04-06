package net.fusemc.zbungeecontrol.packets;

import com.xxmicloxx.znetworklib.codec.CodecResult;
import com.xxmicloxx.znetworklib.codec.NetworkEvent;
import com.xxmicloxx.znetworklib.codec.PacketReader;
import com.xxmicloxx.znetworklib.codec.PacketWriter;

/**
 * Copyright by michidk
 * Created: 11.10.2014.
 */
public class PlayerMessageEvent implements NetworkEvent {

    public static final String EVENT_NAME = "bungeecontrol-playermessage";

    private String player;
    private String message;
    private String sender;

    public PlayerMessageEvent() {
    }

    /**
     * a message that is send to the player as message from another player
     * you can reply with /reply
     * @param player    the player that recieves the message
     * @param message   the already formatted message
     * @param sender    the player that send the message
     */
    public PlayerMessageEvent(String player, String message, String sender) {
        this.player = player;
        this.message = message;
        this.sender = sender;
    }

    @Override
    public CodecResult write(PacketWriter writer) {
        writer.writeString(player);
        writer.writeString(message);
        writer.writeString(sender);
        return CodecResult.OK;
    }

    @Override
    public CodecResult read(PacketReader reader) {
        player = reader.readString();
        message = reader.readString();
        sender = reader.readString();
        return CodecResult.OK;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

}
