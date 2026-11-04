package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import xcord.logging.Log4jFilter;

public class ClientSettings extends MultiVersionPacketV17 {
    private String locale;
    private byte viewDistance;
    private int chatFlags;
    private boolean chatColours;
    private byte difficulty;
    private byte skinParts;
    private int mainHand;
    private boolean disableTextFiltering;
    private boolean allowServerListing;

    public ClientSettings() {
    }

    public ClientSettings(
        String locale,
        byte viewDistance,
        int chatFlags,
        boolean chatColours,
        byte difficulty,
        byte skinParts,
        int mainHand,
        boolean disableTextFiltering,
        boolean allowServerListing
    ) {
        this.locale = locale;
        this.viewDistance = viewDistance;
        this.chatFlags = chatFlags;
        this.chatColours = chatColours;
        this.difficulty = difficulty;
        this.skinParts = skinParts;
        this.mainHand = mainHand;
        this.disableTextFiltering = disableTextFiltering;
        this.allowServerListing = allowServerListing;
    }

    @Override
    public void v17Read(ByteBuf in, ProtocolConstants.Direction direction, int protocolVersion) {
        this.locale = Log4jFilter.filter(direction, readString(in));
        this.viewDistance = in.readByte();
        this.chatFlags = in.readUnsignedByte();
        this.chatColours = in.readBoolean();
        this.skinParts = in.readByte();
        this.difficulty = in.readByte();
    }

    @Override
    public void read(ByteBuf in, ProtocolConstants.Direction direction, int protocolVersion) {
        this.locale = Log4jFilter.filter(direction, readString(in, 16));
        this.viewDistance = in.readByte();
        this.chatFlags = protocolVersion >= 107 ? DefinedPacket.readVarInt(in) : in.readUnsignedByte();
        this.chatColours = in.readBoolean();
        this.skinParts = in.readByte();
        if (protocolVersion >= 107) {
            this.mainHand = DefinedPacket.readVarInt(in);
        }
        if (protocolVersion >= 755) {
            this.disableTextFiltering = in.readBoolean();
        }
        if (protocolVersion >= 757) {
            this.allowServerListing = in.readBoolean();
        }
    }

    @Override
    public void write(ByteBuf out, ProtocolConstants.Direction direction, int protocolVersion) {
        writeString(this.locale, out);
        out.writeByte(this.viewDistance);
        if (protocolVersion >= 107) {
            DefinedPacket.writeVarInt(this.chatFlags, out);
        } else {
            out.writeByte(this.chatFlags);
        }
        out.writeBoolean(this.chatColours);
        out.writeByte(this.skinParts);
        if (protocolVersion >= 107) {
            DefinedPacket.writeVarInt(this.mainHand, out);
        }
        if (protocolVersion >= 755) {
            out.writeBoolean(this.disableTextFiltering);
        }
        if (protocolVersion >= 757) {
            out.writeBoolean(this.allowServerListing);
        }
    }

    @Override
    public void v17Write(ByteBuf out, ProtocolConstants.Direction direction, int protocolVersion) {
        writeString(this.locale, out);
        out.writeByte(this.viewDistance);
        out.writeByte(this.chatFlags);
        out.writeBoolean(this.chatColours);
        out.writeByte(this.skinParts);
        out.writeByte(this.difficulty);
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public String getLocale() {
        return this.locale;
    }

    public byte getViewDistance() {
        return this.viewDistance;
    }

    public int getChatFlags() {
        return this.chatFlags;
    }

    public boolean isChatColours() {
        return this.chatColours;
    }

    public byte getDifficulty() {
        return this.difficulty;
    }

    public byte getSkinParts() {
        return this.skinParts;
    }

    public int getMainHand() {
        return this.mainHand;
    }

    public boolean isDisableTextFiltering() {
        return this.disableTextFiltering;
    }

    public boolean isAllowServerListing() {
        return this.allowServerListing;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setViewDistance(byte viewDistance) {
        this.viewDistance = viewDistance;
    }

    public void setChatFlags(int chatFlags) {
        this.chatFlags = chatFlags;
    }

    public void setChatColours(boolean chatColours) {
        this.chatColours = chatColours;
    }

    public void setDifficulty(byte difficulty) {
        this.difficulty = difficulty;
    }

    public void setSkinParts(byte skinParts) {
        this.skinParts = skinParts;
    }

    public void setMainHand(int mainHand) {
        this.mainHand = mainHand;
    }

    public void setDisableTextFiltering(boolean disableTextFiltering) {
        this.disableTextFiltering = disableTextFiltering;
    }

    public void setAllowServerListing(boolean allowServerListing) {
        this.allowServerListing = allowServerListing;
    }

    @Override
    public String toString() {
        return "ClientSettings(locale="
            + this.getLocale()
            + ", viewDistance="
            + this.getViewDistance()
            + ", chatFlags="
            + this.getChatFlags()
            + ", chatColours="
            + this.isChatColours()
            + ", difficulty="
            + this.getDifficulty()
            + ", skinParts="
            + this.getSkinParts()
            + ", mainHand="
            + this.getMainHand()
            + ", disableTextFiltering="
            + this.isDisableTextFiltering()
            + ", allowServerListing="
            + this.isAllowServerListing()
            + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ClientSettings)) {
            return false;
        }
        ClientSettings settings = (ClientSettings) other;
        if (!settings.canEqual(this)) {
            return false;
        }
        if (this.getViewDistance() != settings.getViewDistance()) {
            return false;
        }
        if (this.getChatFlags() != settings.getChatFlags()) {
            return false;
        }
        if (this.isChatColours() != settings.isChatColours()) {
            return false;
        }
        if (this.getDifficulty() != settings.getDifficulty()) {
            return false;
        }
        if (this.getSkinParts() != settings.getSkinParts()) {
            return false;
        }
        if (this.getMainHand() != settings.getMainHand()) {
            return false;
        }
        if (this.isDisableTextFiltering() != settings.isDisableTextFiltering()) {
            return false;
        }
        if (this.isAllowServerListing() != settings.isAllowServerListing()) {
            return false;
        }
        String thisLocale = this.getLocale();
        String otherLocale = settings.getLocale();
        return thisLocale == null ? otherLocale == null : thisLocale.equals(otherLocale);
    }

    protected boolean canEqual(Object other) {
        return other instanceof ClientSettings;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getViewDistance();
        result = result * 59 + this.getChatFlags();
        result = result * 59 + (this.isChatColours() ? 79 : 97);
        result = result * 59 + this.getDifficulty();
        result = result * 59 + this.getSkinParts();
        result = result * 59 + this.getMainHand();
        result = result * 59 + (this.isDisableTextFiltering() ? 79 : 97);
        result = result * 59 + (this.isAllowServerListing() ? 79 : 97);
        String locale = this.getLocale();
        return result * 59 + (locale == null ? 43 : locale.hashCode());
    }
}
