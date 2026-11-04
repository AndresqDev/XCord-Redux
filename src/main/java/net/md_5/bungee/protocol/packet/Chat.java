package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;
import xcord.logging.Log4jFilter;

public class Chat extends MultiVersionPacketV17 {
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);
    private String message;
    private byte position;
    private UUID sender;

    public Chat() {
    }

    public Chat(String message) {
        this(message, (byte) 0);
    }

    public Chat(String message, byte position) {
        this(message, position, EMPTY_UUID);
    }

    public Chat(String message, byte position, UUID sender) {
        this.message = message;
        this.position = position;
        this.sender = sender == null ? EMPTY_UUID : sender;
    }

    @Override
    public void v17Read(ByteBuf in, ProtocolConstants.Direction direction, int protocolVersion) {
        this.message = Log4jFilter.filter(direction, readString(in, 2000));
    }

    @Override
    public void read(ByteBuf in, ProtocolConstants.Direction direction, int protocolVersion) {
        if (direction == ProtocolConstants.Direction.TO_CLIENT) {
            this.message = Log4jFilter.filter(direction, readString(in, 262144));
            this.position = in.readByte();
            if (protocolVersion >= 735) {
                this.sender = readUUID(in);
            }
        } else {
            this.message = Log4jFilter.filter(direction, readString(in, 256));
        }
    }

    @Override
    public void v17Write(ByteBuf out, ProtocolConstants.Direction direction, int protocolVersion) {
        writeString(this.message, out);
    }

    @Override
    public void write(ByteBuf out, ProtocolConstants.Direction direction, int protocolVersion) {
        if (direction == ProtocolConstants.Direction.TO_CLIENT) {
            writeString(this.message, 262144, out);
            out.writeByte(this.position);
            if (protocolVersion >= 735) {
                writeUUID(this.sender, out);
            }
        } else {
            writeString(this.message, out);
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public String getMessage() {
        return this.message;
    }

    public byte getPosition() {
        return this.position;
    }

    public UUID getSender() {
        return this.sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPosition(byte position) {
        this.position = position;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Chat(message=" + this.getMessage() + ", position=" + this.getPosition() + ", sender=" + this.getSender() + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Chat)) {
            return false;
        }
        Chat chat = (Chat) other;
        if (!chat.canEqual(this)) {
            return false;
        }
        if (this.getPosition() != chat.getPosition()) {
            return false;
        }
        String thisMessage = this.getMessage();
        String otherMessage = chat.getMessage();
        if (!(thisMessage == null ? otherMessage == null : thisMessage.equals(otherMessage))) {
            return false;
        }
        UUID thisSender = this.getSender();
        UUID otherSender = chat.getSender();
        return thisSender == null ? otherSender == null : thisSender.equals(otherSender);
    }

    protected boolean canEqual(Object other) {
        return other instanceof Chat;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getPosition();
        String message = this.getMessage();
        result = result * 59 + (message == null ? 43 : message.hashCode());
        UUID sender = this.getSender();
        return result * 59 + (sender == null ? 43 : sender.hashCode());
    }
}
