package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import xcord.VarInts;

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

   public MinecraftDecoder(Protocol var1, boolean var2, int var3, boolean var4) {
      this.protocol = var1;
      this.server = var2;
      this.protocolVersion = var3;
      this.sliceByteBuf = var4;
   }

   public MinecraftDecoder(Protocol var1, boolean var2, int var3) {
      this(var1, var2, var3, false);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var1.channel().isActive() && (var2.readableBytes() != 0 || this.server)) {
         Protocol.DirectionData var4 = this.server ? this.protocol.TO_SERVER : this.protocol.TO_CLIENT;
         ByteBuf var5 = this.sliceByteBuf ? var2.retainedSlice() : var2.copy();
         Object var6 = null;

         try {
            int var7 = VarInts.readVarInt(var2);
            var6 = var7;
            DefinedPacket var14 = var4.createPacket(var7, this.protocolVersion, this.supportsForge);
            if (var14 != null) {
               var6 = var14.getClass();
               this.doLengthSanityChecks(var2, var14, var4.getDirection(), var7);
               var14.read0(var2, var4.getDirection(), this.protocolVersion);
               if (var2.isReadable()) {
                  if (!DEBUG) {
                     throw PACKET_NOT_READ_TO_END;
                  }

                  throw new BadPacketException(
                     "Packet "
                        + this.protocol
                        + ":"
                        + var4.getDirection()
                        + "/"
                        + var7
                        + " ("
                        + var14.getClass().getSimpleName()
                        + ") larger than expected, extra bytes: "
                        + var2.readableBytes()
                  );
               }
            } else {
               var2.skipBytes(var2.readableBytes());
            }

            var3.add(new PacketWrapper(var14, var5));
            var5 = null;
         } catch (IndexOutOfBoundsException | BadPacketException var12) {
            if (!DEBUG) {
               throw var12;
            }

            String var8;
            if (var6 instanceof Integer) {
               var8 = "id " + Integer.toHexString((Integer)var6);
            } else if (var6 instanceof Class) {
               var8 = "class " + ((Class)var6).getSimpleName();
            } else {
               var8 = "unknown";
            }

            throw new FastDecoderException("Error decoding packet " + var8, var12);
         } finally {
            if (var5 != null) {
               var5.release();
            }
         }
      }
   }

   private void doLengthSanityChecks(ByteBuf var1, DefinedPacket var2, ProtocolConstants.Direction var3, int var4) throws Exception {
      int var5 = var2.expectedMinLength(var1, var3, this.protocolVersion);
      int var6 = var2.expectedMaxLength(var1, var3, this.protocolVersion);
      if (var6 != -1 && var1.readableBytes() > var6) {
         throw this.handleOverflow(var2, var6, var1.readableBytes(), var4);
      } else if (var1.readableBytes() < var5) {
         throw this.handleUnderflow(var2, var6, var1.readableBytes(), var4);
      }
   }

   private Exception handleOverflow(DefinedPacket var1, int var2, int var3, int var4) {
      if (DEBUG) {
         throw new CorruptedFrameException(
            "Packet "
               + var1.getClass()
               + " "
               + var4
               + " Protocol "
               + this.protocolVersion
               + " was too big (expected "
               + var2
               + " bytes, got "
               + var3
               + " bytes)"
         );
      } else {
         return PACKET_LENGTH_OVERSIZED;
      }
   }

   private Exception handleUnderflow(DefinedPacket var1, int var2, int var3, int var4) {
      if (DEBUG) {
         throw new CorruptedFrameException(
            "Packet "
               + var1.getClass()
               + " "
               + var4
               + " Protocol "
               + this.protocolVersion
               + " was too small (expected "
               + var2
               + " bytes, got "
               + var3
               + " bytes)"
         );
      } else {
         return PACKET_LENGTH_UNDERSIZED;
      }
   }

   public MinecraftDecoder(Protocol var1, boolean var2, int var3, boolean var4, boolean var5) {
      this.protocol = var1;
      this.server = var2;
      this.protocolVersion = var3;
      this.sliceByteBuf = var4;
      this.supportsForge = var5;
   }

   public void setProtocol(Protocol var1) {
      this.protocol = var1;
   }

   public void setProtocolVersion(int var1) {
      this.protocolVersion = var1;
   }

   public void setSupportsForge(boolean var1) {
      this.supportsForge = var1;
   }
}
