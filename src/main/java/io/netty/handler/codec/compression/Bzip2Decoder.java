package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

public class Bzip2Decoder extends ByteToMessageDecoder {
   private Bzip2Decoder.State currentState = Bzip2Decoder.State.INIT;
   private final Bzip2BitReader reader = new Bzip2BitReader();
   private Bzip2BlockDecompressor blockDecompressor;
   private Bzip2HuffmanStageDecoder huffmanStageDecoder;
   private int blockSize;
   private int blockCRC;
   private int streamCRC;

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (var2.isReadable()) {
         Bzip2BitReader var4 = this.reader;
         var4.setByteBuf(var2);

         label403:
         while (true) {
            switch (this.currentState) {
               case INIT:
                  if (var2.readableBytes() < 4) {
                     return;
                  }

                  int var5 = var2.readUnsignedMedium();
                  if (var5 != 4348520) {
                     throw new DecompressionException("Unexpected stream identifier contents. Mismatched bzip2 protocol version?");
                  }

                  int var6 = var2.readByte() - 48;
                  if (var6 < 1 || var6 > 9) {
                     throw new DecompressionException("block size is invalid");
                  }

                  this.blockSize = var6 * 100000;
                  this.streamCRC = 0;
                  this.currentState = Bzip2Decoder.State.INIT_BLOCK;
               case INIT_BLOCK:
                  if (!var4.hasReadableBytes(10)) {
                     return;
                  }

                  int var7 = var4.readBits(24);
                  int var8 = var4.readBits(24);
                  if (var7 == 1536581 && var8 == 3690640) {
                     int var38 = var4.readInt();
                     if (var38 != this.streamCRC) {
                        throw new DecompressionException("stream CRC error");
                     }

                     this.currentState = Bzip2Decoder.State.EOF;
                     break;
                  } else {
                     if (var7 != 3227993 || var8 != 2511705) {
                        throw new DecompressionException("bad block header");
                     }

                     this.blockCRC = var4.readInt();
                     this.currentState = Bzip2Decoder.State.INIT_BLOCK_PARAMS;
                  }
               case INIT_BLOCK_PARAMS:
                  if (!var4.hasReadableBits(25)) {
                     return;
                  }

                  boolean var9 = var4.readBoolean();
                  int var10 = var4.readBits(24);
                  this.blockDecompressor = new Bzip2BlockDecompressor(this.blockSize, this.blockCRC, var9, var10, var4);
                  this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_USED_MAP;
               case RECEIVE_HUFFMAN_USED_MAP:
                  if (!var4.hasReadableBits(16)) {
                     return;
                  }

                  this.blockDecompressor.huffmanInUse16 = var4.readBits(16);
                  this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_USED_BITMAPS;
               case RECEIVE_HUFFMAN_USED_BITMAPS:
                  Bzip2BlockDecompressor var11 = this.blockDecompressor;
                  int var12 = var11.huffmanInUse16;
                  int var13 = Integer.bitCount(var12);
                  byte[] var14 = var11.huffmanSymbolMap;
                  if (!var4.hasReadableBits(var13 * 16 + 3)) {
                     return;
                  }

                  int var15 = 0;
                  if (var13 > 0) {
                     for (int var16 = 0; var16 < 16; var16++) {
                        if ((var12 & 32768 >>> var16) != 0) {
                           int var17 = 0;

                           for (int var18 = var16 << 4; var17 < 16; var18++) {
                              if (var4.readBoolean()) {
                                 var14[var15++] = (byte)var18;
                              }

                              var17++;
                           }
                        }
                     }
                  }

                  var11.huffmanEndOfBlockSymbol = var15 + 1;
                  int var40 = var4.readBits(3);
                  if (var40 < 2 || var40 > 6) {
                     throw new DecompressionException("incorrect huffman groups number");
                  }

                  int var42 = var15 + 2;
                  if (var42 > 258) {
                     throw new DecompressionException("incorrect alphabet size");
                  }

                  this.huffmanStageDecoder = new Bzip2HuffmanStageDecoder(var4, var40, var42);
                  this.currentState = Bzip2Decoder.State.RECEIVE_SELECTORS_NUMBER;
               case RECEIVE_SELECTORS_NUMBER:
                  if (!var4.hasReadableBits(15)) {
                     return;
                  }

                  int var44 = var4.readBits(15);
                  if (var44 < 1 || var44 > 18002) {
                     throw new DecompressionException("incorrect selectors number");
                  }

                  this.huffmanStageDecoder.selectors = new byte[var44];
                  this.currentState = Bzip2Decoder.State.RECEIVE_SELECTORS;
               case RECEIVE_SELECTORS:
                  Bzip2HuffmanStageDecoder var19 = this.huffmanStageDecoder;
                  byte[] var20 = var19.selectors;
                  int var45 = var20.length;
                  Bzip2MoveToFrontTable var21 = var19.tableMTF;

                  for (int var22 = var19.currentSelector; var22 < var45; var22++) {
                     if (!var4.hasReadableBits(6)) {
                        var19.currentSelector = var22;
                        return;
                     }

                     int var23 = 0;

                     while (var4.readBoolean()) {
                        var23++;
                     }

                     var20[var22] = var21.indexToFront(var23);
                  }

                  this.currentState = Bzip2Decoder.State.RECEIVE_HUFFMAN_LENGTH;
               case RECEIVE_HUFFMAN_LENGTH:
                  Bzip2HuffmanStageDecoder var46 = this.huffmanStageDecoder;
                  int var41 = var46.totalTables;
                  byte[][] var47 = var46.tableCodeLengths;
                  int var43 = var46.alphabetSize;
                  int var25 = var46.currentLength;
                  int var26 = 0;
                  boolean var27 = var46.modifyLength;
                  boolean var28 = false;

                  int var24;
                  label336:
                  for (var24 = var46.currentGroup; var24 < var41; var24++) {
                     if (!var4.hasReadableBits(5)) {
                        var28 = true;
                        break;
                     }

                     if (var25 < 0) {
                        var25 = var4.readBits(5);
                     }

                     for (var26 = var46.currentAlpha; var26 < var43; var26++) {
                        if (!var4.isReadable()) {
                           var28 = true;
                           break label336;
                        }

                        while (var27 || var4.readBoolean()) {
                           if (!var4.isReadable()) {
                              var27 = true;
                              var28 = true;
                              break label336;
                           }

                           var25 += var4.readBoolean() ? -1 : 1;
                           var27 = false;
                           if (!var4.isReadable()) {
                              var28 = true;
                              break label336;
                           }
                        }

                        var47[var24][var26] = (byte)var25;
                     }

                     var25 = -1;
                     var26 = var46.currentAlpha = 0;
                     var27 = false;
                  }

                  if (var28) {
                     var46.currentGroup = var24;
                     var46.currentLength = var25;
                     var46.currentAlpha = var26;
                     var46.modifyLength = var27;
                     return;
                  }

                  var46.createHuffmanDecodingTables();
                  this.currentState = Bzip2Decoder.State.DECODE_HUFFMAN_DATA;
               case DECODE_HUFFMAN_DATA:
                  break label403;
               case EOF:
                  var2.skipBytes(var2.readableBytes());
                  return;
               default:
                  throw new IllegalStateException();
            }
         }

         Bzip2BlockDecompressor var39 = this.blockDecompressor;
         int var29 = var2.readerIndex();
         boolean var30 = var39.decodeHuffmanData(this.huffmanStageDecoder);
         if (var30) {
            if (var2.readerIndex() == var29 && var2.isReadable()) {
               var4.refill();
            }

            int var31 = var39.blockLength();
            ByteBuf var32 = var1.alloc().buffer(var31);

            try {
               int var33;
               while ((var33 = var39.read()) >= 0) {
                  var32.writeByte(var33);
               }

               this.currentState = Bzip2Decoder.State.INIT_BLOCK;
               int var34 = var39.checkCRC();
               this.streamCRC = (this.streamCRC << 1 | this.streamCRC >>> 31) ^ var34;
               var3.add(var32);
               var32 = null;
            } finally {
               if (var32 != null) {
                  var32.release();
               }
            }
         }
      }
   }

   public boolean isClosed() {
      return this.currentState == Bzip2Decoder.State.EOF;
   }

   private static enum State {
      INIT,
      INIT_BLOCK,
      INIT_BLOCK_PARAMS,
      RECEIVE_HUFFMAN_USED_MAP,
      RECEIVE_HUFFMAN_USED_BITMAPS,
      RECEIVE_SELECTORS_NUMBER,
      RECEIVE_SELECTORS,
      RECEIVE_HUFFMAN_LENGTH,
      DECODE_HUFFMAN_DATA,
      EOF;
   }
}
