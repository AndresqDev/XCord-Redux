package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameEncoder extends MessageToMessageEncoder<WebSocketFrame> implements WebSocketFrameEncoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameEncoder.class);
   private static final byte OPCODE_CONT = 0;
   private static final byte OPCODE_TEXT = 1;
   private static final byte OPCODE_BINARY = 2;
   private static final byte OPCODE_CLOSE = 8;
   private static final byte OPCODE_PING = 9;
   private static final byte OPCODE_PONG = 10;
   private static final int GATHERING_WRITE_THRESHOLD = 1024;
   private final boolean maskPayload;

   public WebSocket08FrameEncoder(boolean var1) {
      this.maskPayload = var1;
   }

   protected void encode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      ByteBuf var4 = var2.content();
      byte var6;
      if (var2 instanceof TextWebSocketFrame) {
         var6 = 1;
      } else if (var2 instanceof PingWebSocketFrame) {
         var6 = 9;
      } else if (var2 instanceof PongWebSocketFrame) {
         var6 = 10;
      } else if (var2 instanceof CloseWebSocketFrame) {
         var6 = 8;
      } else if (var2 instanceof BinaryWebSocketFrame) {
         var6 = 2;
      } else {
         if (!(var2 instanceof ContinuationWebSocketFrame)) {
            throw new UnsupportedOperationException("Cannot encode frame of type: " + var2.getClass().getName());
         }

         var6 = 0;
      }

      int var7 = var4.readableBytes();
      if (logger.isTraceEnabled()) {
         logger.trace("Encoding WebSocket Frame opCode={} length={}", var6, var7);
      }

      int var8 = 0;
      if (var2.isFinalFragment()) {
         var8 |= 128;
      }

      var8 |= var2.rsv() % 8 << 4;
      var8 |= var6 % 128;
      if (var6 == 9 && var7 > 125) {
         throw new TooLongFrameException("invalid payload for PING (payload length must be <= 125, was " + var7);
      } else {
         boolean var9 = true;
         ByteBuf var10 = null;

         try {
            int var11 = this.maskPayload ? 4 : 0;
            if (var7 <= 125) {
               int var26 = 2 + var11 + var7;
               var10 = var1.alloc().buffer(var26);
               var10.writeByte(var8);
               byte var13 = (byte)(this.maskPayload ? 128 | (byte)var7 : (byte)var7);
               var10.writeByte(var13);
            } else if (var7 > 65535) {
               int var25 = 10 + var11;
               if (this.maskPayload) {
                  var25 += var7;
               }

               var10 = var1.alloc().buffer(var25);
               var10.writeByte(var8);
               var10.writeByte(this.maskPayload ? 255 : 127);
               var10.writeLong((long)var7);
            } else {
               int var12 = 4 + var11;
               if (this.maskPayload || var7 <= 1024) {
                  var12 += var7;
               }

               var10 = var1.alloc().buffer(var12);
               var10.writeByte(var8);
               var10.writeByte(this.maskPayload ? 254 : 126);
               var10.writeByte(var7 >>> 8 & 0xFF);
               var10.writeByte(var7 & 0xFF);
            }

            if (!this.maskPayload) {
               if (var10.writableBytes() >= var4.readableBytes()) {
                  var10.writeBytes(var4);
                  var3.add(var10);
               } else {
                  var3.add(var10);
                  var3.add(var4.retain());
               }
            } else {
               int var27 = PlatformDependent.threadLocalRandom().nextInt(Integer.MAX_VALUE);
               byte[] var5 = ByteBuffer.allocate(4).putInt(var27).array();
               var10.writeBytes(var5);
               if (var4.isReadable()) {
                  ByteOrder var28 = var4.order();
                  ByteOrder var14 = var10.order();
                  int var15 = var4.readerIndex();
                  int var16 = var4.writerIndex();
                  if (var28 == var14) {
                     long var17 = (long)(var5[0] & 255) << 24 | (long)((var5[1] & 255) << 16) | (long)((var5[2] & 255) << 8) | (long)(var5[3] & 255);
                     var17 |= var17 << 32;
                     if (var28 == ByteOrder.LITTLE_ENDIAN) {
                        var17 = Long.reverseBytes(var17);
                     }

                     for (int var19 = var16 - 7; var15 < var19; var15 += 8) {
                        var10.writeLong(var4.getLong(var15) ^ var17);
                     }

                     if (var15 < var16 - 3) {
                        var10.writeInt(var4.getInt(var15) ^ (int)var17);
                        var15 += 4;
                     }
                  }

                  for (int var30 = 0; var15 < var16; var15++) {
                     byte var18 = var4.getByte(var15);
                     var10.writeByte(var18 ^ var5[var30++ & 3]);
                  }
               }

               var3.add(var10);
            }

            var9 = false;
         } finally {
            if (var9 && var10 != null) {
               var10.release();
            }
         }
      }
   }
}
