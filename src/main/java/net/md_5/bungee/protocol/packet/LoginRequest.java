package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.ProtocolConstants;
import xcord.logging.Log4jFilter;

public class LoginRequest extends DefinedPacket {
    private String data;
    private PlayerPublicKey publicKey;
    private UUID uuid;

    public LoginRequest() {
    }

    public LoginRequest(String data, PlayerPublicKey publicKey, UUID uuid) {
        this.data = data;
        this.publicKey = publicKey;
        this.uuid = uuid;
    }

    @Override
    public void read(ByteBuf in, ProtocolConstants.Direction direction, int protocolVersion) {
        this.data = Log4jFilter.filter(readString(in, 16));
        if (protocolVersion >= 759) {
            this.publicKey = readPublicKey(in);
        }
        if (protocolVersion >= 760 && in.readBoolean()) {
            this.uuid = readUUID(in);
        }
    }

    @Override
    public void write(ByteBuf out, ProtocolConstants.Direction direction, int protocolVersion) {
        writeString(this.data, out);
        if (protocolVersion >= 759) {
            writePublicKey(this.publicKey, out);
        }
        if (protocolVersion >= 760) {
            if (this.uuid != null) {
                out.writeBoolean(true);
                writeUUID(this.uuid, out);
            } else {
                out.writeBoolean(false);
            }
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public int expectedMaxLength(ByteBuf in, ProtocolConstants.Direction direction, int protocolVersion) {
        return protocolVersion >= 759 ? 90000 : 65;
    }

    public String getData() {
        return this.data;
    }

    public PlayerPublicKey getPublicKey() {
        return this.publicKey;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setPublicKey(PlayerPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "LoginRequest(data=" + this.getData() + ", publicKey=" + this.getPublicKey() + ", uuid=" + this.getUuid() + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof LoginRequest)) {
            return false;
        }
        LoginRequest request = (LoginRequest) other;
        if (!request.canEqual(this)) {
            return false;
        }
        String thisData = this.getData();
        String otherData = request.getData();
        if (!(thisData == null ? otherData == null : thisData.equals(otherData))) {
            return false;
        }
        PlayerPublicKey thisPublicKey = this.getPublicKey();
        PlayerPublicKey otherPublicKey = request.getPublicKey();
        if (!(thisPublicKey == null ? otherPublicKey == null : thisPublicKey.equals(otherPublicKey))) {
            return false;
        }
        UUID thisUuid = this.getUuid();
        UUID otherUuid = request.getUuid();
        return thisUuid == null ? otherUuid == null : thisUuid.equals(otherUuid);
    }

    protected boolean canEqual(Object other) {
        return other instanceof LoginRequest;
    }

    @Override
    public int hashCode() {
        int result = 1;
        String data = this.getData();
        result = result * 59 + (data == null ? 43 : data.hashCode());
        PlayerPublicKey publicKey = this.getPublicKey();
        result = result * 59 + (publicKey == null ? 43 : publicKey.hashCode());
        UUID uuid = this.getUuid();
        return result * 59 + (uuid == null ? 43 : uuid.hashCode());
    }
}
