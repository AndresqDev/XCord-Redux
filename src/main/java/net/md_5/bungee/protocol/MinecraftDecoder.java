package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import xcord.protocol.VarInts;

public class MinecraftDecoder extends MessageToMessageDecoder<ByteBuf> {
    private Protocol protocol;
    private final boolean server;
    private int protocolVersion;
    private final boolean sliceByteBuf;
    private boolean supportsForge = false;
    public static final boolean DEBUG = Boolean.getBoolean("waterfall.packet-decode-logging");
    private static final CorruptedFrameException PACKET_LENGTH_OVERSIZED = new MutedCorruptedFrameException(
        "A packet could not be decoded because it was too large. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true"
    );
    private static final MutedCorruptedFrameException PACKET_LENGTH_UNDERSIZED = new MutedCorruptedFrameException(
        "A packet could not be decoded because it was smaller than allowed. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true"
    );
    private static final BadPacketException PACKET_NOT_READ_TO_END = new BadPacketException(
        "Couldn't read all bytes from a packet. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true"
    );

    public MinecraftDecoder(Protocol protocol, boolean server, int protocolVersion) {
        this(protocol, server, protocolVersion, false);
    }

    public MinecraftDecoder(Protocol protocol, boolean server, int protocolVersion, boolean sliceByteBuf) {
        this(protocol, server, protocolVersion, sliceByteBuf, false);
    }

    public MinecraftDecoder(Protocol protocol, boolean server, int protocolVersion, boolean sliceByteBuf, boolean supportsForge) {
        this.protocol = protocol;
        this.server = server;
        this.protocolVersion = protocolVersion;
        this.sliceByteBuf = sliceByteBuf;
        this.supportsForge = supportsForge;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (ctx.channel().isActive() && (in.readableBytes() != 0 || this.server)) {
            Protocol.DirectionData directionData = this.server ? this.protocol.TO_SERVER : this.protocol.TO_CLIENT;
            ByteBuf packetBuffer = this.sliceByteBuf ? in.retainedSlice() : in.copy();
            Object packetType = null;

            try {
                int packetId = VarInts.readVarInt(in);
                packetType = packetId;
                DefinedPacket packet = directionData.createPacket(packetId, this.protocolVersion, this.supportsForge);
                if (packet != null) {
                    packetType = packet.getClass();
                    this.doLengthSanityChecks(in, packet, directionData.getDirection(), packetId);
                    packet.read0(in, directionData.getDirection(), this.protocolVersion);
                    if (in.isReadable()) {
                        if (!DEBUG) {
                            throw PACKET_NOT_READ_TO_END;
                        }
                        throw new BadPacketException(
                            "Packet "
                                + this.protocol
                                + ":"
                                + directionData.getDirection()
                                + "/"
                                + packetId
                                + " ("
                                + packet.getClass().getSimpleName()
                                + ") larger than expected, extra bytes: "
                                + in.readableBytes()
                        );
                    }
                } else {
                    in.skipBytes(in.readableBytes());
                }

                out.add(new PacketWrapper(packet, packetBuffer));
                packetBuffer = null;
            } catch (IndexOutOfBoundsException | BadPacketException ex) {
                if (!DEBUG) {
                    throw ex;
                }

                String packetDescription;
                if (packetType instanceof Integer) {
                    packetDescription = "id " + Integer.toHexString((Integer) packetType);
                } else if (packetType instanceof Class) {
                    packetDescription = "class " + ((Class<?>) packetType).getSimpleName();
                } else {
                    packetDescription = "unknown";
                }

                throw new FastDecoderException("Error decoding packet " + packetDescription, ex);
            } finally {
                if (packetBuffer != null) {
                    packetBuffer.release();
                }
            }
        }
    }

    private void doLengthSanityChecks(ByteBuf in, DefinedPacket packet, ProtocolConstants.Direction direction, int packetId) throws Exception {
        int minLength = packet.expectedMinLength(in, direction, this.protocolVersion);
        int maxLength = packet.expectedMaxLength(in, direction, this.protocolVersion);
        if (maxLength != -1 && in.readableBytes() > maxLength) {
            throw this.handleOverflow(packet, maxLength, in.readableBytes(), packetId);
        } else if (in.readableBytes() < minLength) {
            throw this.handleUnderflow(packet, maxLength, in.readableBytes(), packetId);
        }
    }

    private Exception handleOverflow(DefinedPacket packet, int expectedLength, int actualLength, int packetId) {
        if (DEBUG) {
            throw new CorruptedFrameException(
                "Packet "
                    + packet.getClass()
                    + " "
                    + packetId
                    + " Protocol "
                    + this.protocolVersion
                    + " was too big (expected "
                    + expectedLength
                    + " bytes, got "
                    + actualLength
                    + " bytes)"
            );
        }
        return PACKET_LENGTH_OVERSIZED;
    }

    private Exception handleUnderflow(DefinedPacket packet, int expectedLength, int actualLength, int packetId) {
        if (DEBUG) {
            throw new CorruptedFrameException(
                "Packet "
                    + packet.getClass()
                    + " "
                    + packetId
                    + " Protocol "
                    + this.protocolVersion
                    + " was too small (expected "
                    + expectedLength
                    + " bytes, got "
                    + actualLength
                    + " bytes)"
            );
        }
        return PACKET_LENGTH_UNDERSIZED;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setSupportsForge(boolean supportsForge) {
        this.supportsForge = supportsForge;
    }
}
