package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.util.List;

@ChannelHandler.Sharable
public final class HAProxyMessageEncoder extends MessageToByteEncoder<HAProxyMessage> {
   private static final int V2_VERSION_BITMASK = 32;
   static final int UNIX_ADDRESS_BYTES_LENGTH = 108;
   static final int TOTAL_UNIX_ADDRESS_BYTES_LENGTH = 216;
   public static final HAProxyMessageEncoder INSTANCE = new HAProxyMessageEncoder();

   private HAProxyMessageEncoder() {
   }

   protected void encode(ChannelHandlerContext var1, HAProxyMessage var2, ByteBuf var3) throws Exception {
      switch (var2.protocolVersion()) {
         case V1:
            encodeV1(var2, var3);
            break;
         case V2:
            encodeV2(var2, var3);
            break;
         default:
            throw new HAProxyProtocolException("Unsupported version: " + var2.protocolVersion());
      }
   }

   private static void encodeV1(HAProxyMessage var0, ByteBuf var1) {
      var1.writeBytes(HAProxyConstants.TEXT_PREFIX);
      var1.writeByte(32);
      var1.writeCharSequence(var0.proxiedProtocol().name(), CharsetUtil.US_ASCII);
      var1.writeByte(32);
      var1.writeCharSequence(var0.sourceAddress(), CharsetUtil.US_ASCII);
      var1.writeByte(32);
      var1.writeCharSequence(var0.destinationAddress(), CharsetUtil.US_ASCII);
      var1.writeByte(32);
      var1.writeCharSequence(String.valueOf(var0.sourcePort()), CharsetUtil.US_ASCII);
      var1.writeByte(32);
      var1.writeCharSequence(String.valueOf(var0.destinationPort()), CharsetUtil.US_ASCII);
      var1.writeByte(13);
      var1.writeByte(10);
   }

   private static void encodeV2(HAProxyMessage var0, ByteBuf var1) {
      var1.writeBytes(HAProxyConstants.BINARY_PREFIX);
      var1.writeByte(32 | var0.command().byteValue());
      var1.writeByte(var0.proxiedProtocol().byteValue());
      switch (var0.proxiedProtocol().addressFamily()) {
         case AF_IPv4:
         case AF_IPv6:
            byte[] var2 = NetUtil.createByteArrayFromIpAddressString(var0.sourceAddress());
            byte[] var3 = NetUtil.createByteArrayFromIpAddressString(var0.destinationAddress());
            var1.writeShort(var2.length + var3.length + 4 + var0.tlvNumBytes());
            var1.writeBytes(var2);
            var1.writeBytes(var3);
            var1.writeShort(var0.sourcePort());
            var1.writeShort(var0.destinationPort());
            encodeTlvs(var0.tlvs(), var1);
            break;
         case AF_UNIX:
            var1.writeShort(216 + var0.tlvNumBytes());
            int var4 = var1.writeCharSequence(var0.sourceAddress(), CharsetUtil.US_ASCII);
            var1.writeZero(108 - var4);
            int var5 = var1.writeCharSequence(var0.destinationAddress(), CharsetUtil.US_ASCII);
            var1.writeZero(108 - var5);
            encodeTlvs(var0.tlvs(), var1);
            break;
         case AF_UNSPEC:
            var1.writeShort(0);
            break;
         default:
            throw new HAProxyProtocolException("unexpected addrFamily");
      }
   }

   private static void encodeTlv(HAProxyTLV var0, ByteBuf var1) {
      if (var0 instanceof HAProxySSLTLV) {
         HAProxySSLTLV var2 = (HAProxySSLTLV)var0;
         var1.writeByte(var0.typeByteValue());
         var1.writeShort(var2.contentNumBytes());
         var1.writeByte(var2.client());
         var1.writeInt(var2.verify());
         encodeTlvs(var2.encapsulatedTLVs(), var1);
      } else {
         var1.writeByte(var0.typeByteValue());
         ByteBuf var4 = var0.content();
         int var3 = var4.readableBytes();
         var1.writeShort(var3);
         var1.writeBytes(var4.readSlice(var3));
      }
   }

   private static void encodeTlvs(List<HAProxyTLV> var0, ByteBuf var1) {
      for (int var2 = 0; var2 < var0.size(); var2++) {
         encodeTlv((HAProxyTLV)var0.get(var2), var1);
      }
   }
}
