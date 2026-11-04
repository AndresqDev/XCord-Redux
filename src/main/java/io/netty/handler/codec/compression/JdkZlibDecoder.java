package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class JdkZlibDecoder extends ZlibDecoder {
   private static final int FHCRC = 2;
   private static final int FEXTRA = 4;
   private static final int FNAME = 8;
   private static final int FCOMMENT = 16;
   private static final int FRESERVED = 224;
   private Inflater inflater;
   private final byte[] dictionary;
   private final ByteBufChecksum crc;
   private final boolean decompressConcatenated;
   private JdkZlibDecoder.GzipState gzipState = JdkZlibDecoder.GzipState.HEADER_START;
   private int flags = -1;
   private int xlen = -1;
   private volatile boolean finished;
   private boolean decideZlibOrNone;

   public JdkZlibDecoder() {
      this(ZlibWrapper.ZLIB, null, false, 0);
   }

   public JdkZlibDecoder(int var1) {
      this(ZlibWrapper.ZLIB, null, false, var1);
   }

   public JdkZlibDecoder(byte[] var1) {
      this(ZlibWrapper.ZLIB, var1, false, 0);
   }

   public JdkZlibDecoder(byte[] var1, int var2) {
      this(ZlibWrapper.ZLIB, var1, false, var2);
   }

   public JdkZlibDecoder(ZlibWrapper var1) {
      this(var1, null, false, 0);
   }

   public JdkZlibDecoder(ZlibWrapper var1, int var2) {
      this(var1, null, false, var2);
   }

   public JdkZlibDecoder(ZlibWrapper var1, boolean var2) {
      this(var1, null, var2, 0);
   }

   public JdkZlibDecoder(ZlibWrapper var1, boolean var2, int var3) {
      this(var1, null, var2, var3);
   }

   public JdkZlibDecoder(boolean var1) {
      this(ZlibWrapper.GZIP, null, var1, 0);
   }

   public JdkZlibDecoder(boolean var1, int var2) {
      this(ZlibWrapper.GZIP, null, var1, var2);
   }

   private JdkZlibDecoder(ZlibWrapper var1, byte[] var2, boolean var3, int var4) {
      super(var4);
      ObjectUtil.checkNotNull(var1, "wrapper");
      this.decompressConcatenated = var3;
      switch (var1) {
         case GZIP:
            this.inflater = new Inflater(true);
            this.crc = ByteBufChecksum.wrapChecksum(new CRC32());
            break;
         case NONE:
            this.inflater = new Inflater(true);
            this.crc = null;
            break;
         case ZLIB:
            this.inflater = new Inflater();
            this.crc = null;
            break;
         case ZLIB_OR_NONE:
            this.decideZlibOrNone = true;
            this.crc = null;
            break;
         default:
            throw new IllegalArgumentException("Only GZIP or ZLIB is supported, but you used " + var1);
      }

      this.dictionary = var2;
   }

   @Override
   public boolean isClosed() {
      return this.finished;
   }

   @Override
   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      if (this.finished) {
         var2.skipBytes(var2.readableBytes());
      } else {
         int var4 = var2.readableBytes();
         if (var4 != 0) {
            if (this.decideZlibOrNone) {
               if (var4 < 2) {
                  return;
               }

               boolean var5 = !looksLikeZlib(var2.getShort(var2.readerIndex()));
               this.inflater = new Inflater(var5);
               this.decideZlibOrNone = false;
            }

            if (this.crc != null && this.gzipState != JdkZlibDecoder.GzipState.HEADER_END) {
               if (this.gzipState == JdkZlibDecoder.GzipState.FOOTER_START) {
                  if (!this.handleGzipFooter(var2)) {
                     return;
                  }

                  assert this.gzipState == JdkZlibDecoder.GzipState.HEADER_START;
               }

               if (!this.readGZIPHeader(var2)) {
                  return;
               }

               var4 = var2.readableBytes();
               if (var4 == 0) {
                  return;
               }
            }

            if (this.inflater.needsInput()) {
               if (var2.hasArray()) {
                  this.inflater.setInput(var2.array(), var2.arrayOffset() + var2.readerIndex(), var4);
               } else {
                  byte[] var17 = new byte[var4];
                  var2.getBytes(var2.readerIndex(), var17);
                  this.inflater.setInput(var17);
               }
            }

            ByteBuf var18 = this.prepareDecompressBuffer(var1, null, this.inflater.getRemaining() << 1);

            try {
               boolean var6;
               for (var6 = false; !this.inflater.needsInput(); var18 = this.prepareDecompressBuffer(var1, var18, this.inflater.getRemaining() << 1)) {
                  byte[] var7 = var18.array();
                  int var8 = var18.writerIndex();
                  int var9 = var18.arrayOffset() + var8;
                  int var10 = var18.writableBytes();
                  int var11 = this.inflater.inflate(var7, var9, var10);
                  if (var11 > 0) {
                     var18.writerIndex(var8 + var11);
                     if (this.crc != null) {
                        this.crc.update(var7, var9, var11);
                     }
                  } else if (this.inflater.needsDictionary()) {
                     if (this.dictionary == null) {
                        throw new DecompressionException("decompression failure, unable to set dictionary as non was specified");
                     }

                     this.inflater.setDictionary(this.dictionary);
                  }

                  if (this.inflater.finished()) {
                     if (this.crc == null) {
                        this.finished = true;
                     } else {
                        var6 = true;
                     }
                     break;
                  }
               }

               var2.skipBytes(var4 - this.inflater.getRemaining());
               if (var6) {
                  this.gzipState = JdkZlibDecoder.GzipState.FOOTER_START;
                  this.handleGzipFooter(var2);
               }
            } catch (DataFormatException var15) {
               throw new DecompressionException("decompression failure", var15);
            } finally {
               if (var18.isReadable()) {
                  var3.add(var18);
               } else {
                  var18.release();
               }
            }
         }
      }
   }

   private boolean handleGzipFooter(ByteBuf var1) {
      if (this.readGZIPFooter(var1)) {
         this.finished = !this.decompressConcatenated;
         if (!this.finished) {
            this.inflater.reset();
            this.crc.reset();
            this.gzipState = JdkZlibDecoder.GzipState.HEADER_START;
            return true;
         }
      }

      return false;
   }

   @Override
   protected void decompressionBufferExhausted(ByteBuf var1) {
      this.finished = true;
   }

   @Override
   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      super.handlerRemoved0(var1);
      if (this.inflater != null) {
         this.inflater.end();
      }
   }

   private boolean readGZIPHeader(ByteBuf var1) {
      switch (this.gzipState) {
         case HEADER_START:
            if (var1.readableBytes() < 10) {
               return false;
            } else {
               byte var2 = var1.readByte();
               byte var3 = var1.readByte();
               if (var2 != 31) {
                  throw new DecompressionException("Input is not in the GZIP format");
               } else {
                  this.crc.update(var2);
                  this.crc.update(var3);
                  short var4 = var1.readUnsignedByte();
                  if (var4 != 8) {
                     throw new DecompressionException("Unsupported compression method " + var4 + " in the GZIP header");
                  } else {
                     this.crc.update(var4);
                     this.flags = var1.readUnsignedByte();
                     this.crc.update(this.flags);
                     if ((this.flags & 224) != 0) {
                        throw new DecompressionException("Reserved flags are set in the GZIP header");
                     } else {
                        this.crc.update(var1, var1.readerIndex(), 4);
                        var1.skipBytes(4);
                        this.crc.update(var1.readUnsignedByte());
                        this.crc.update(var1.readUnsignedByte());
                        this.gzipState = JdkZlibDecoder.GzipState.FLG_READ;
                     }
                  }
               }
            }
         case FLG_READ:
            if ((this.flags & 4) != 0) {
               if (var1.readableBytes() < 2) {
                  return false;
               }

               short var5 = var1.readUnsignedByte();
               short var6 = var1.readUnsignedByte();
               this.crc.update(var5);
               this.crc.update(var6);
               this.xlen |= var5 << 8 | var6;
            }

            this.gzipState = JdkZlibDecoder.GzipState.XLEN_READ;
         case XLEN_READ:
            if (this.xlen != -1) {
               if (var1.readableBytes() < this.xlen) {
                  return false;
               }

               this.crc.update(var1, var1.readerIndex(), this.xlen);
               var1.skipBytes(this.xlen);
            }

            this.gzipState = JdkZlibDecoder.GzipState.SKIP_FNAME;
         case SKIP_FNAME:
            if (!this.skipIfNeeded(var1, 8)) {
               return false;
            } else {
               this.gzipState = JdkZlibDecoder.GzipState.SKIP_COMMENT;
            }
         case SKIP_COMMENT:
            if (!this.skipIfNeeded(var1, 16)) {
               return false;
            } else {
               this.gzipState = JdkZlibDecoder.GzipState.PROCESS_FHCRC;
            }
         case PROCESS_FHCRC:
            if ((this.flags & 2) != 0 && !this.verifyCrc16(var1)) {
               return false;
            } else {
               this.crc.reset();
               this.gzipState = JdkZlibDecoder.GzipState.HEADER_END;
            }
         case HEADER_END:
            return true;
         default:
            throw new IllegalStateException();
      }
   }

   private boolean skipIfNeeded(ByteBuf var1, int var2) {
      short var3;
      if ((this.flags & var2) != 0) {
         do {
            if (!var1.isReadable()) {
               return false;
            }

            var3 = var1.readUnsignedByte();
            this.crc.update(var3);
         } while (var3 != 0);
      }

      return true;
   }

   private boolean readGZIPFooter(ByteBuf var1) {
      if (var1.readableBytes() < 8) {
         return false;
      } else {
         boolean var2 = this.verifyCrc(var1);

         assert var2;

         int var3 = 0;

         for (int var4 = 0; var4 < 4; var4++) {
            var3 |= var1.readUnsignedByte() << var4 * 8;
         }

         int var5 = this.inflater.getTotalOut();
         if (var3 != var5) {
            throw new DecompressionException("Number of bytes mismatch. Expected: " + var3 + ", Got: " + var5);
         } else {
            return true;
         }
      }
   }

   private boolean verifyCrc(ByteBuf var1) {
      if (var1.readableBytes() < 4) {
         return false;
      } else {
         long var2 = 0L;

         for (int var4 = 0; var4 < 4; var4++) {
            var2 |= (long)var1.readUnsignedByte() << var4 * 8;
         }

         long var6 = this.crc.getValue();
         if (var2 != var6) {
            throw new DecompressionException("CRC value mismatch. Expected: " + var2 + ", Got: " + var6);
         } else {
            return true;
         }
      }
   }

   private boolean verifyCrc16(ByteBuf var1) {
      if (var1.readableBytes() < 2) {
         return false;
      } else {
         long var2 = this.crc.getValue();
         long var4 = 0L;
         long var6 = 0L;

         for (int var8 = 0; var8 < 2; var8++) {
            var4 |= (long)var1.readUnsignedByte() << var8 * 8;
            var6 |= (var2 >> var8 * 8 & 255L) << var8 * 8;
         }

         if (var4 != var6) {
            throw new DecompressionException("CRC16 value mismatch. Expected: " + var4 + ", Got: " + var6);
         } else {
            return true;
         }
      }
   }

   private static boolean looksLikeZlib(short var0) {
      return (var0 & 30720) == 30720 && var0 % 31 == 0;
   }

   private static enum GzipState {
      HEADER_START,
      HEADER_END,
      FLG_READ,
      XLEN_READ,
      SKIP_FNAME,
      SKIP_COMMENT,
      PROCESS_FHCRC,
      FOOTER_START;
   }
}
