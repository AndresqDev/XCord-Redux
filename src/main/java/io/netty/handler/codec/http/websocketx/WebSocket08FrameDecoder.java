package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteOrder;
import java.util.List;

public class WebSocket08FrameDecoder extends ByteToMessageDecoder implements WebSocketFrameDecoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocket08FrameDecoder.class);
   private static final byte OPCODE_CONT = 0;
   private static final byte OPCODE_TEXT = 1;
   private static final byte OPCODE_BINARY = 2;
   private static final byte OPCODE_CLOSE = 8;
   private static final byte OPCODE_PING = 9;
   private static final byte OPCODE_PONG = 10;
   private final WebSocketDecoderConfig config;
   private int fragmentedFramesCount;
   private boolean frameFinalFlag;
   private boolean frameMasked;
   private int frameRsv;
   private int frameOpcode;
   private long framePayloadLength;
   private byte[] maskingKey;
   private int framePayloadLen1;
   private boolean receivedClosingHandshake;
   private WebSocket08FrameDecoder.State state = WebSocket08FrameDecoder.State.READING_FIRST;

   public WebSocket08FrameDecoder(boolean var1, boolean var2, int var3) {
      this(var1, var2, var3, false);
   }

   public WebSocket08FrameDecoder(boolean var1, boolean var2, int var3, boolean var4) {
      this(WebSocketDecoderConfig.newBuilder().expectMaskedFrames(var1).allowExtensions(var2).maxFramePayloadLength(var3).allowMaskMismatch(var4).build());
   }

   public WebSocket08FrameDecoder(WebSocketDecoderConfig var1) {
      this.config = ObjectUtil.checkNotNull(var1, "decoderConfig");
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.receivedClosingHandshake) {
         var2.skipBytes(this.actualReadableBytes());
      } else {
         switch (this.state) {
            case READING_FIRST:
               if (!var2.isReadable()) {
                  return;
               }

               this.framePayloadLength = 0L;
               byte var4 = var2.readByte();
               this.frameFinalFlag = (var4 & 128) != 0;
               this.frameRsv = (var4 & 112) >> 4;
               this.frameOpcode = var4 & 15;
               if (logger.isTraceEnabled()) {
                  logger.trace("Decoding WebSocket Frame opCode={}", this.frameOpcode);
               }

               this.state = WebSocket08FrameDecoder.State.READING_SECOND;
            case READING_SECOND:
               if (!var2.isReadable()) {
                  return;
               }

               byte var9 = var2.readByte();
               this.frameMasked = (var9 & 128) != 0;
               this.framePayloadLen1 = var9 & 127;
               if (this.frameRsv != 0 && !this.config.allowExtensions()) {
                  this.protocolViolation(var1, var2, "RSV != 0 and no extension negotiated, RSV:" + this.frameRsv);
                  return;
               }

               if (!this.config.allowMaskMismatch() && this.config.expectMaskedFrames() != this.frameMasked) {
                  this.protocolViolation(var1, var2, "received a frame that is not masked as expected");
                  return;
               }

               if (this.frameOpcode > 7) {
                  if (!this.frameFinalFlag) {
                     this.protocolViolation(var1, var2, "fragmented control frame");
                     return;
                  }

                  if (this.framePayloadLen1 > 125) {
                     this.protocolViolation(var1, var2, "control frame with payload length > 125 octets");
                     return;
                  }

                  if (this.frameOpcode != 8 && this.frameOpcode != 9 && this.frameOpcode != 10) {
                     this.protocolViolation(var1, var2, "control frame using reserved opcode " + this.frameOpcode);
                     return;
                  }

                  if (this.frameOpcode == 8 && this.framePayloadLen1 == 1) {
                     this.protocolViolation(var1, var2, "received close control frame with payload len 1");
                     return;
                  }
               } else {
                  if (this.frameOpcode != 0 && this.frameOpcode != 1 && this.frameOpcode != 2) {
                     this.protocolViolation(var1, var2, "data frame using reserved opcode " + this.frameOpcode);
                     return;
                  }

                  if (this.fragmentedFramesCount == 0 && this.frameOpcode == 0) {
                     this.protocolViolation(var1, var2, "received continuation data frame outside fragmented message");
                     return;
                  }

                  if (this.fragmentedFramesCount != 0 && this.frameOpcode != 0) {
                     this.protocolViolation(var1, var2, "received non-continuation data frame while inside fragmented message");
                     return;
                  }
               }

               this.state = WebSocket08FrameDecoder.State.READING_SIZE;
            case READING_SIZE:
               if (this.framePayloadLen1 == 126) {
                  if (var2.readableBytes() < 2) {
                     return;
                  }

                  this.framePayloadLength = (long)var2.readUnsignedShort();
                  if (this.framePayloadLength < 126L) {
                     this.protocolViolation(var1, var2, "invalid data frame length (not using minimal length encoding)");
                     return;
                  }
               } else if (this.framePayloadLen1 == 127) {
                  if (var2.readableBytes() < 8) {
                     return;
                  }

                  this.framePayloadLength = var2.readLong();
                  if (this.framePayloadLength < 0L) {
                     this.protocolViolation(var1, var2, "invalid data frame length (negative length)");
                     return;
                  }

                  if (this.framePayloadLength < 65536L) {
                     this.protocolViolation(var1, var2, "invalid data frame length (not using minimal length encoding)");
                     return;
                  }
               } else {
                  this.framePayloadLength = (long)this.framePayloadLen1;
               }

               if (this.framePayloadLength > (long)this.config.maxFramePayloadLength()) {
                  this.protocolViolation(
                     var1, var2, WebSocketCloseStatus.MESSAGE_TOO_BIG, "Max frame length of " + this.config.maxFramePayloadLength() + " has been exceeded."
                  );
                  return;
               }

               if (logger.isTraceEnabled()) {
                  logger.trace("Decoding WebSocket Frame length={}", this.framePayloadLength);
               }

               this.state = WebSocket08FrameDecoder.State.MASKING_KEY;
            case MASKING_KEY:
               if (this.frameMasked) {
                  if (var2.readableBytes() < 4) {
                     return;
                  }

                  if (this.maskingKey == null) {
                     this.maskingKey = new byte[4];
                  }

                  var2.readBytes(this.maskingKey);
               }

               this.state = WebSocket08FrameDecoder.State.PAYLOAD;
            case PAYLOAD:
               break;
            case CORRUPT:
               if (var2.isReadable()) {
                  var2.readByte();
               }

               return;
            default:
               throw new Error("Shouldn't reach here.");
         }

         if ((long)var2.readableBytes() >= this.framePayloadLength) {
            ByteBuf var5 = Unpooled.EMPTY_BUFFER;

            try {
               if (this.framePayloadLength > 0L) {
                  var5 = ByteBufUtil.readBytes(var1.alloc(), var2, toFrameLength(this.framePayloadLength));
               }

               this.state = WebSocket08FrameDecoder.State.READING_FIRST;
               if (this.frameMasked & this.framePayloadLength > 0L) {
                  this.unmask(var5);
               }

               if (this.frameOpcode == 9) {
                  var3.add(new PingWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameOpcode == 10) {
                  var3.add(new PongWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameOpcode == 8) {
                  this.receivedClosingHandshake = true;
                  this.checkCloseFrameBody(var1, var5);
                  var3.add(new CloseWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameFinalFlag) {
                  this.fragmentedFramesCount = 0;
               } else {
                  this.fragmentedFramesCount++;
               }

               if (this.frameOpcode == 1) {
                  var3.add(new TextWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               if (this.frameOpcode != 2) {
                  if (this.frameOpcode != 0) {
                     throw new UnsupportedOperationException("Cannot decode web socket frame with opcode: " + this.frameOpcode);
                  }

                  var3.add(new ContinuationWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
                  var5 = null;
                  return;
               }

               var3.add(new BinaryWebSocketFrame(this.frameFinalFlag, this.frameRsv, var5));
               var5 = null;
            } finally {
               if (var5 != null) {
                  var5.release();
               }
            }
         }
      }
   }

   private void unmask(ByteBuf var1) {
      int var2 = var1.readerIndex();
      int var3 = var1.writerIndex();
      ByteOrder var4 = var1.order();
      long var5 = (long)(this.maskingKey[0] & 255) << 24
         | (long)((this.maskingKey[1] & 255) << 16)
         | (long)((this.maskingKey[2] & 255) << 8)
         | (long)(this.maskingKey[3] & 255);
      var5 |= var5 << 32;
      if (var4 == ByteOrder.LITTLE_ENDIAN) {
         var5 = Long.reverseBytes(var5);
      }

      for (int var7 = var3 - 7; var2 < var7; var2 += 8) {
         var1.setLong(var2, var1.getLong(var2) ^ var5);
      }

      if (var2 < var3 - 3) {
         var1.setInt(var2, var1.getInt(var2) ^ (int)var5);
         var2 += 4;
      }

      for (int var9 = 0; var2 < var3; var2++) {
         var1.setByte(var2, var1.getByte(var2) ^ this.maskingKey[var9++ & 3]);
      }
   }

   private void protocolViolation(ChannelHandlerContext var1, ByteBuf var2, String var3) {
      this.protocolViolation(var1, var2, WebSocketCloseStatus.PROTOCOL_ERROR, var3);
   }

   private void protocolViolation(ChannelHandlerContext var1, ByteBuf var2, WebSocketCloseStatus var3, String var4) {
      this.protocolViolation(var1, var2, new CorruptedWebSocketFrameException(var3, var4));
   }

   private void protocolViolation(ChannelHandlerContext var1, ByteBuf var2, CorruptedWebSocketFrameException var3) {
      this.state = WebSocket08FrameDecoder.State.CORRUPT;
      int var4 = var2.readableBytes();
      if (var4 > 0) {
         var2.skipBytes(var4);
      }

      if (var1.channel().isActive() && this.config.closeOnProtocolViolation()) {
         Object var5;
         if (this.receivedClosingHandshake) {
            var5 = Unpooled.EMPTY_BUFFER;
         } else {
            WebSocketCloseStatus var6 = var3.closeStatus();
            String var7 = var3.getMessage();
            if (var7 == null) {
               var7 = var6.reasonText();
            }

            var5 = new CloseWebSocketFrame(var6, var7);
         }

         var1.writeAndFlush(var5).addListener(ChannelFutureListener.CLOSE);
      }

      throw var3;
   }

   private static int toFrameLength(long var0) {
      if (var0 > 2147483647L) {
         throw new TooLongFrameException("Length:" + var0);
      } else {
         return (int)var0;
      }
   }

   protected void checkCloseFrameBody(ChannelHandlerContext var1, ByteBuf var2) {
      if (var2 != null && var2.isReadable()) {
         if (var2.readableBytes() < 2) {
            this.protocolViolation(var1, var2, WebSocketCloseStatus.INVALID_PAYLOAD_DATA, "Invalid close frame body");
         }

         short var3 = var2.getShort(var2.readerIndex());
         if (!WebSocketCloseStatus.isValidStatusCode(var3)) {
            this.protocolViolation(var1, var2, "Invalid close frame getStatus code: " + var3);
         }

         if (var2.readableBytes() > 2) {
            try {
               new Utf8Validator().check(var2, var2.readerIndex() + 2, var2.readableBytes() - 2);
            } catch (CorruptedWebSocketFrameException var5) {
               this.protocolViolation(var1, var2, var5);
            }
         }
      }
   }

   static enum State {
      READING_FIRST,
      READING_SECOND,
      READING_SIZE,
      MASKING_KEY,
      PAYLOAD,
      CORRUPT;
   }
}
