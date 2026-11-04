package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ProtocolDetectionResult;
import io.netty.util.CharsetUtil;
import java.util.List;

public class HAProxyMessageDecoder extends ByteToMessageDecoder {
   private static final int V1_MAX_LENGTH = 108;
   private static final int V2_MAX_LENGTH = 65551;
   private static final int V2_MIN_LENGTH = 232;
   private static final int V2_MAX_TLV = 65319;
   private static final int BINARY_PREFIX_LENGTH = HAProxyConstants.BINARY_PREFIX.length;
   private static final ProtocolDetectionResult<HAProxyProtocolVersion> DETECTION_RESULT_V1 = ProtocolDetectionResult.detected(HAProxyProtocolVersion.V1);
   private static final ProtocolDetectionResult<HAProxyProtocolVersion> DETECTION_RESULT_V2 = ProtocolDetectionResult.detected(HAProxyProtocolVersion.V2);
   private HAProxyMessageDecoder.HeaderExtractor headerExtractor;
   private boolean discarding;
   private int discardedBytes;
   private final boolean failFast;
   private boolean finished;
   private int version = -1;
   private final int v2MaxHeaderSize;

   public HAProxyMessageDecoder() {
      this(true);
   }

   public HAProxyMessageDecoder(boolean var1) {
      this.v2MaxHeaderSize = 65551;
      this.failFast = var1;
   }

   public HAProxyMessageDecoder(int var1) {
      this(var1, true);
   }

   public HAProxyMessageDecoder(int var1, boolean var2) {
      if (var1 < 1) {
         this.v2MaxHeaderSize = 232;
      } else if (var1 > 65319) {
         this.v2MaxHeaderSize = 65551;
      } else {
         int var3 = var1 + 232;
         if (var3 > 65551) {
            this.v2MaxHeaderSize = 65551;
         } else {
            this.v2MaxHeaderSize = var3;
         }
      }

      this.failFast = var2;
   }

   private static int findVersion(ByteBuf var0) {
      int var1 = var0.readableBytes();
      if (var1 < 13) {
         return -1;
      } else {
         int var2 = var0.readerIndex();
         return match(HAProxyConstants.BINARY_PREFIX, var0, var2) ? var0.getByte(var2 + BINARY_PREFIX_LENGTH) : 1;
      }
   }

   private static int findEndOfHeader(ByteBuf var0) {
      int var1 = var0.readableBytes();
      if (var1 < 16) {
         return -1;
      } else {
         int var2 = var0.readerIndex() + 14;
         int var3 = 16 + var0.getUnsignedShort(var2);
         return var1 >= var3 ? var3 : -1;
      }
   }

   private static int findEndOfLine(ByteBuf var0) {
      int var1 = var0.writerIndex();

      for (int var2 = var0.readerIndex(); var2 < var1; var2++) {
         byte var3 = var0.getByte(var2);
         if (var3 == 13 && var2 < var1 - 1 && var0.getByte(var2 + 1) == 10) {
            return var2;
         }
      }

      return -1;
   }

   @Override
   public boolean isSingleDecode() {
      return true;
   }

   @Override
   public void channelRead(ChannelHandlerContext var1, Object var2) throws Exception {
      super.channelRead(var1, var2);
      if (this.finished) {
         var1.pipeline().remove(this);
      }
   }

   @Override
   protected final void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.version != -1 || (this.version = findVersion(var2)) != -1) {
         ByteBuf var4;
         if (this.version == 1) {
            var4 = this.decodeLine(var1, var2);
         } else {
            var4 = this.decodeStruct(var1, var2);
         }

         if (var4 != null) {
            this.finished = true;

            try {
               if (this.version == 1) {
                  var3.add(HAProxyMessage.decodeHeader(var4.toString(CharsetUtil.US_ASCII)));
               } else {
                  var3.add(HAProxyMessage.decodeHeader(var4));
               }
            } catch (HAProxyProtocolException var6) {
               this.fail(var1, null, var6);
            }
         }
      }
   }

   private ByteBuf decodeStruct(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
      if (this.headerExtractor == null) {
         this.headerExtractor = new HAProxyMessageDecoder.StructHeaderExtractor(this.v2MaxHeaderSize);
      }

      return this.headerExtractor.extract(var1, var2);
   }

   private ByteBuf decodeLine(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
      if (this.headerExtractor == null) {
         this.headerExtractor = new HAProxyMessageDecoder.LineHeaderExtractor(108);
      }

      return this.headerExtractor.extract(var1, var2);
   }

   private void failOverLimit(ChannelHandlerContext var1, int var2) {
      this.failOverLimit(var1, String.valueOf(var2));
   }

   private void failOverLimit(ChannelHandlerContext var1, String var2) {
      int var3 = this.version == 1 ? 108 : this.v2MaxHeaderSize;
      this.fail(var1, "header length (" + var2 + ") exceeds the allowed maximum (" + var3 + ')', null);
   }

   private void fail(ChannelHandlerContext var1, String var2, Exception var3) {
      this.finished = true;
      var1.close();
      HAProxyProtocolException var4;
      if (var2 != null && var3 != null) {
         var4 = new HAProxyProtocolException(var2, var3);
      } else if (var2 != null) {
         var4 = new HAProxyProtocolException(var2);
      } else if (var3 != null) {
         var4 = new HAProxyProtocolException(var3);
      } else {
         var4 = new HAProxyProtocolException();
      }

      throw var4;
   }

   public static ProtocolDetectionResult<HAProxyProtocolVersion> detectProtocol(ByteBuf var0) {
      if (var0.readableBytes() < 12) {
         return ProtocolDetectionResult.needsMoreData();
      } else {
         int var1 = var0.readerIndex();
         if (match(HAProxyConstants.BINARY_PREFIX, var0, var1)) {
            return DETECTION_RESULT_V2;
         } else {
            return match(HAProxyConstants.TEXT_PREFIX, var0, var1) ? DETECTION_RESULT_V1 : ProtocolDetectionResult.invalid();
         }
      }
   }

   private static boolean match(byte[] var0, ByteBuf var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         byte var4 = var1.getByte(var2 + var3);
         if (var4 != var0[var3]) {
            return false;
         }
      }

      return true;
   }

   private abstract class HeaderExtractor {
      private final int maxHeaderSize;

      protected HeaderExtractor(int var2) {
         this.maxHeaderSize = var2;
      }

      public ByteBuf extract(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
         int var3 = this.findEndOfHeader(var2);
         if (!HAProxyMessageDecoder.this.discarding) {
            if (var3 >= 0) {
               int var7 = var3 - var2.readerIndex();
               if (var7 > this.maxHeaderSize) {
                  var2.readerIndex(var3 + this.delimiterLength(var2, var3));
                  HAProxyMessageDecoder.this.failOverLimit(var1, var7);
                  return null;
               } else {
                  ByteBuf var5 = var2.readSlice(var7);
                  var2.skipBytes(this.delimiterLength(var2, var3));
                  return var5;
               }
            } else {
               int var6 = var2.readableBytes();
               if (var6 > this.maxHeaderSize) {
                  HAProxyMessageDecoder.this.discardedBytes = var6;
                  var2.skipBytes(var6);
                  HAProxyMessageDecoder.this.discarding = true;
                  if (HAProxyMessageDecoder.this.failFast) {
                     HAProxyMessageDecoder.this.failOverLimit(var1, "over " + HAProxyMessageDecoder.this.discardedBytes);
                  }
               }

               return null;
            }
         } else {
            if (var3 >= 0) {
               int var4 = HAProxyMessageDecoder.this.discardedBytes + var3 - var2.readerIndex();
               var2.readerIndex(var3 + this.delimiterLength(var2, var3));
               HAProxyMessageDecoder.this.discardedBytes = 0;
               HAProxyMessageDecoder.this.discarding = false;
               if (!HAProxyMessageDecoder.this.failFast) {
                  HAProxyMessageDecoder.this.failOverLimit(var1, "over " + var4);
               }
            } else {
               HAProxyMessageDecoder.this.discardedBytes = HAProxyMessageDecoder.this.discardedBytes + var2.readableBytes();
               var2.skipBytes(var2.readableBytes());
            }

            return null;
         }
      }

      protected abstract int findEndOfHeader(ByteBuf var1);

      protected abstract int delimiterLength(ByteBuf var1, int var2);
   }

   private final class LineHeaderExtractor extends HAProxyMessageDecoder.HeaderExtractor {
      LineHeaderExtractor(int var2) {
         super(var2);
      }

      @Override
      protected int findEndOfHeader(ByteBuf var1) {
         return HAProxyMessageDecoder.findEndOfLine(var1);
      }

      @Override
      protected int delimiterLength(ByteBuf var1, int var2) {
         return var1.getByte(var2) == 13 ? 2 : 1;
      }
   }

   private final class StructHeaderExtractor extends HAProxyMessageDecoder.HeaderExtractor {
      StructHeaderExtractor(int var2) {
         super(var2);
      }

      @Override
      protected int findEndOfHeader(ByteBuf var1) {
         return HAProxyMessageDecoder.findEndOfHeader(var1);
      }

      @Override
      protected int delimiterLength(ByteBuf var1, int var2) {
         return 0;
      }
   }
}
