package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class FastLzFrameDecoder extends ByteToMessageDecoder {
   private FastLzFrameDecoder.State currentState = FastLzFrameDecoder.State.INIT_BLOCK;
   private final ByteBufChecksum checksum;
   private int chunkLength;
   private int originalLength;
   private boolean isCompressed;
   private boolean hasChecksum;
   private int currentChecksum;

   public FastLzFrameDecoder() {
      this(false);
   }

   public FastLzFrameDecoder(boolean var1) {
      this(var1 ? new Adler32() : null);
   }

   public FastLzFrameDecoder(Checksum var1) {
      this.checksum = var1 == null ? null : ByteBufChecksum.wrapChecksum(var1);
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch (this.currentState) {
            case INIT_BLOCK:
               if (var2.readableBytes() < 4) {
                  break;
               }

               int var4 = var2.readUnsignedMedium();
               if (var4 != 4607066) {
                  throw new DecompressionException("unexpected block identifier");
               }

               byte var5 = var2.readByte();
               this.isCompressed = (var5 & 1) == 1;
               this.hasChecksum = (var5 & 16) == 16;
               this.currentState = FastLzFrameDecoder.State.INIT_BLOCK_PARAMS;
            case INIT_BLOCK_PARAMS:
               if (var2.readableBytes() < 2 + (this.isCompressed ? 2 : 0) + (this.hasChecksum ? 4 : 0)) {
                  break;
               }

               this.currentChecksum = this.hasChecksum ? var2.readInt() : 0;
               this.chunkLength = var2.readUnsignedShort();
               this.originalLength = this.isCompressed ? var2.readUnsignedShort() : this.chunkLength;
               this.currentState = FastLzFrameDecoder.State.DECOMPRESS_DATA;
            case DECOMPRESS_DATA:
               int var6 = this.chunkLength;
               if (var2.readableBytes() >= var6) {
                  int var7 = var2.readerIndex();
                  int var8 = this.originalLength;
                  ByteBuf var9 = null;

                  try {
                     if (this.isCompressed) {
                        var9 = var1.alloc().buffer(var8);
                        int var10 = var9.writerIndex();
                        int var11 = FastLz.decompress(var2, var7, var6, var9, var10, var8);
                        if (var8 != var11) {
                           throw new DecompressionException(String.format("stream corrupted: originalLength(%d) and actual length(%d) mismatch", var8, var11));
                        }

                        var9.writerIndex(var9.writerIndex() + var11);
                     } else {
                        var9 = var2.retainedSlice(var7, var6);
                     }

                     ByteBufChecksum var17 = this.checksum;
                     if (this.hasChecksum && var17 != null) {
                        var17.reset();
                        var17.update(var9, var9.readerIndex(), var9.readableBytes());
                        int var18 = (int)var17.getValue();
                        if (var18 != this.currentChecksum) {
                           throw new DecompressionException(
                              String.format("stream corrupted: mismatching checksum: %d (expected: %d)", var18, this.currentChecksum)
                           );
                        }
                     }

                     if (var9.readableBytes() > 0) {
                        var3.add(var9);
                     } else {
                        var9.release();
                     }

                     var9 = null;
                     var2.skipBytes(var6);
                     this.currentState = FastLzFrameDecoder.State.INIT_BLOCK;
                  } finally {
                     if (var9 != null) {
                        var9.release();
                     }
                  }
               }
               break;
            case CORRUPTED:
               var2.skipBytes(var2.readableBytes());
               break;
            default:
               throw new IllegalStateException();
         }
      } catch (Exception var16) {
         this.currentState = FastLzFrameDecoder.State.CORRUPTED;
         throw var16;
      }
   }

   private static enum State {
      INIT_BLOCK,
      INIT_BLOCK_PARAMS,
      DECOMPRESS_DATA,
      CORRUPTED;
   }
}
