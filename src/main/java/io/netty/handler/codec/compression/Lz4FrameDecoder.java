package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import java.util.zip.Checksum;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

public class Lz4FrameDecoder extends ByteToMessageDecoder {
   private Lz4FrameDecoder.State currentState = Lz4FrameDecoder.State.INIT_BLOCK;
   private LZ4FastDecompressor decompressor;
   private ByteBufChecksum checksum;
   private int blockType;
   private int compressedLength;
   private int decompressedLength;
   private int currentChecksum;

   public Lz4FrameDecoder() {
      this(false);
   }

   public Lz4FrameDecoder(boolean var1) {
      this(LZ4Factory.fastestInstance(), var1);
   }

   public Lz4FrameDecoder(LZ4Factory var1, boolean var2) {
      this(var1, var2 ? new Lz4XXHash32(-1756908916) : null);
   }

   public Lz4FrameDecoder(LZ4Factory var1, Checksum var2) {
      this.decompressor = ObjectUtil.checkNotNull(var1, "factory").fastDecompressor();
      this.checksum = var2 == null ? null : ByteBufChecksum.wrapChecksum(var2);
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      try {
         switch (this.currentState) {
            case INIT_BLOCK:
               if (var2.readableBytes() < 21) {
                  break;
               }

               long var4 = var2.readLong();
               if (var4 != 5501767354678207339L) {
                  throw new DecompressionException("unexpected block identifier");
               }

               byte var6 = var2.readByte();
               int var7 = (var6 & 15) + 10;
               int var8 = var6 & 240;
               int var9 = Integer.reverseBytes(var2.readInt());
               if (var9 < 0 || var9 > 33554432) {
                  throw new DecompressionException(String.format("invalid compressedLength: %d (expected: 0-%d)", var9, 33554432));
               }

               int var10 = Integer.reverseBytes(var2.readInt());
               int var11 = 1 << var7;
               if (var10 < 0 || var10 > var11) {
                  throw new DecompressionException(String.format("invalid decompressedLength: %d (expected: 0-%d)", var10, var11));
               }

               if (var10 == 0 && var9 != 0 || var10 != 0 && var9 == 0 || var8 == 16 && var10 != var9) {
                  throw new DecompressionException(String.format("stream corrupted: compressedLength(%d) and decompressedLength(%d) mismatch", var9, var10));
               }

               int var12 = Integer.reverseBytes(var2.readInt());
               if (var10 == 0 && var9 == 0) {
                  if (var12 != 0) {
                     throw new DecompressionException("stream corrupted: checksum error");
                  }

                  this.currentState = Lz4FrameDecoder.State.FINISHED;
                  this.decompressor = null;
                  this.checksum = null;
                  break;
               } else {
                  this.blockType = var8;
                  this.compressedLength = var9;
                  this.decompressedLength = var10;
                  this.currentChecksum = var12;
                  this.currentState = Lz4FrameDecoder.State.DECOMPRESS_DATA;
               }
            case DECOMPRESS_DATA:
               int var23 = this.blockType;
               int var24 = this.compressedLength;
               int var25 = this.decompressedLength;
               int var26 = this.currentChecksum;
               if (var2.readableBytes() >= var24) {
                  ByteBufChecksum var13 = this.checksum;
                  ByteBuf var14 = null;

                  try {
                     switch (var23) {
                        case 16:
                           var14 = var2.retainedSlice(var2.readerIndex(), var25);
                           break;
                        case 32:
                           var14 = var1.alloc().buffer(var25, var25);
                           this.decompressor.decompress(CompressionUtil.safeNioBuffer(var2), var14.internalNioBuffer(var14.writerIndex(), var25));
                           var14.writerIndex(var14.writerIndex() + var25);
                           break;
                        default:
                           throw new DecompressionException(String.format("unexpected blockType: %d (expected: %d or %d)", var23, 16, 32));
                     }

                     var2.skipBytes(var24);
                     if (var13 != null) {
                        CompressionUtil.checkChecksum(var13, var14, var26);
                     }

                     var3.add(var14);
                     var14 = null;
                     this.currentState = Lz4FrameDecoder.State.INIT_BLOCK;
                  } catch (LZ4Exception var20) {
                     throw new DecompressionException(var20);
                  } finally {
                     if (var14 != null) {
                        var14.release();
                     }
                  }
               }
               break;
            case FINISHED:
            case CORRUPTED:
               var2.skipBytes(var2.readableBytes());
               break;
            default:
               throw new IllegalStateException();
         }
      } catch (Exception var22) {
         this.currentState = Lz4FrameDecoder.State.CORRUPTED;
         throw var22;
      }
   }

   public boolean isClosed() {
      return this.currentState == Lz4FrameDecoder.State.FINISHED;
   }

   private static enum State {
      INIT_BLOCK,
      DECOMPRESS_DATA,
      FINISHED,
      CORRUPTED;
   }
}
