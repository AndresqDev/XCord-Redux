package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.Brotli;
import io.netty.handler.codec.compression.BrotliEncoder;
import io.netty.handler.codec.compression.BrotliOptions;
import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.handler.codec.compression.DeflateOptions;
import io.netty.handler.codec.compression.GzipOptions;
import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.compression.Zstd;
import io.netty.handler.codec.compression.ZstdEncoder;
import io.netty.handler.codec.compression.ZstdOptions;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Map;

public class HttpContentCompressor extends HttpContentEncoder {
   private final boolean supportsCompressionOptions;
   private final BrotliOptions brotliOptions;
   private final GzipOptions gzipOptions;
   private final DeflateOptions deflateOptions;
   private final ZstdOptions zstdOptions;
   private final int compressionLevel;
   private final int windowBits;
   private final int memLevel;
   private final int contentSizeThreshold;
   private ChannelHandlerContext ctx;
   private final Map<String, CompressionEncoderFactory> factories;

   public HttpContentCompressor() {
      this(6);
   }

   @Deprecated
   public HttpContentCompressor(int var1) {
      this(var1, 15, 8, 0);
   }

   @Deprecated
   public HttpContentCompressor(int var1, int var2, int var3) {
      this(var1, var2, var3, 0);
   }

   @Deprecated
   public HttpContentCompressor(int var1, int var2, int var3, int var4) {
      this.compressionLevel = ObjectUtil.checkInRange(var1, 0, 9, "compressionLevel");
      this.windowBits = ObjectUtil.checkInRange(var2, 9, 15, "windowBits");
      this.memLevel = ObjectUtil.checkInRange(var3, 1, 9, "memLevel");
      this.contentSizeThreshold = ObjectUtil.checkPositiveOrZero(var4, "contentSizeThreshold");
      this.brotliOptions = null;
      this.gzipOptions = null;
      this.deflateOptions = null;
      this.zstdOptions = null;
      this.factories = null;
      this.supportsCompressionOptions = false;
   }

   public HttpContentCompressor(CompressionOptions... var1) {
      this(0, var1);
   }

   public HttpContentCompressor(int var1, CompressionOptions... var2) {
      this.contentSizeThreshold = ObjectUtil.checkPositiveOrZero(var1, "contentSizeThreshold");
      BrotliOptions var3 = null;
      GzipOptions var4 = null;
      DeflateOptions var5 = null;
      ZstdOptions var6 = null;
      if (var2 != null && var2.length != 0) {
         ObjectUtil.deepCheckNotNull("compressionOptions", var2);

         for (CompressionOptions var10 : var2) {
            if (Brotli.isAvailable() && var10 instanceof BrotliOptions) {
               var3 = (BrotliOptions)var10;
            } else if (var10 instanceof GzipOptions) {
               var4 = (GzipOptions)var10;
            } else if (var10 instanceof DeflateOptions) {
               var5 = (DeflateOptions)var10;
            } else {
               if (!(var10 instanceof ZstdOptions)) {
                  throw new IllegalArgumentException("Unsupported " + CompressionOptions.class.getSimpleName() + ": " + var10);
               }

               var6 = (ZstdOptions)var10;
            }
         }
      } else {
         var3 = Brotli.isAvailable() ? StandardCompressionOptions.brotli() : null;
         var4 = StandardCompressionOptions.gzip();
         var5 = StandardCompressionOptions.deflate();
         var6 = Zstd.isAvailable() ? StandardCompressionOptions.zstd() : null;
      }

      this.gzipOptions = var4;
      this.deflateOptions = var5;
      this.brotliOptions = var3;
      this.zstdOptions = var6;
      this.factories = new HashMap<>();
      if (this.gzipOptions != null) {
         this.factories.put("gzip", new HttpContentCompressor.GzipEncoderFactory());
      }

      if (this.deflateOptions != null) {
         this.factories.put("deflate", new HttpContentCompressor.DeflateEncoderFactory());
      }

      if (Brotli.isAvailable() && this.brotliOptions != null) {
         this.factories.put("br", new HttpContentCompressor.BrEncoderFactory());
      }

      if (this.zstdOptions != null) {
         this.factories.put("zstd", new HttpContentCompressor.ZstdEncoderFactory());
      }

      this.compressionLevel = -1;
      this.windowBits = -1;
      this.memLevel = -1;
      this.supportsCompressionOptions = true;
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
   }

   @Override
   protected HttpContentEncoder.Result beginEncode(HttpResponse var1, String var2) throws Exception {
      if (this.contentSizeThreshold > 0 && var1 instanceof HttpContent && ((HttpContent)var1).content().readableBytes() < this.contentSizeThreshold) {
         return null;
      } else {
         String var3 = var1.headers().get(HttpHeaderNames.CONTENT_ENCODING);
         if (var3 != null) {
            return null;
         } else if (this.supportsCompressionOptions) {
            String var6 = this.determineEncoding(var2);
            if (var6 == null) {
               return null;
            } else {
               CompressionEncoderFactory var7 = this.factories.get(var6);
               if (var7 == null) {
                  throw new Error();
               } else {
                  return new HttpContentEncoder.Result(
                     var6,
                     new EmbeddedChannel(
                        this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), var7.createEncoder()
                     )
                  );
               }
            }
         } else {
            ZlibWrapper var4 = this.determineWrapper(var2);
            if (var4 == null) {
               return null;
            } else {
               String var5;
               switch (var4) {
                  case GZIP:
                     var5 = "gzip";
                     break;
                  case ZLIB:
                     var5 = "deflate";
                     break;
                  default:
                     throw new Error();
               }

               return new HttpContentEncoder.Result(
                  var5,
                  new EmbeddedChannel(
                     this.ctx.channel().id(),
                     this.ctx.channel().metadata().hasDisconnect(),
                     this.ctx.channel().config(),
                     ZlibCodecFactory.newZlibEncoder(var4, this.compressionLevel, this.windowBits, this.memLevel)
                  )
               );
            }
         }
      }
   }

   protected String determineEncoding(String var1) {
      float var2 = -1.0F;
      float var3 = -1.0F;
      float var4 = -1.0F;
      float var5 = -1.0F;
      float var6 = -1.0F;

      for (String var10 : var1.split(",")) {
         float var11 = 1.0F;
         int var12 = var10.indexOf(61);
         if (var12 != -1) {
            try {
               var11 = Float.parseFloat(var10.substring(var12 + 1));
            } catch (NumberFormatException var14) {
               var11 = 0.0F;
            }
         }

         if (var10.contains("*")) {
            var2 = var11;
         } else if (var10.contains("br") && var11 > var3) {
            var3 = var11;
         } else if (var10.contains("zstd") && var11 > var4) {
            var4 = var11;
         } else if (var10.contains("gzip") && var11 > var5) {
            var5 = var11;
         } else if (var10.contains("deflate") && var11 > var6) {
            var6 = var11;
         }
      }

      if (var3 > 0.0F || var4 > 0.0F || var5 > 0.0F || var6 > 0.0F) {
         if (var3 != -1.0F && var3 >= var4 && this.brotliOptions != null) {
            return "br";
         }

         if (var4 != -1.0F && var4 >= var5 && this.zstdOptions != null) {
            return "zstd";
         }

         if (var5 != -1.0F && var5 >= var6 && this.gzipOptions != null) {
            return "gzip";
         }

         if (var6 != -1.0F && this.deflateOptions != null) {
            return "deflate";
         }
      }

      if (var2 > 0.0F) {
         if (var3 == -1.0F && this.brotliOptions != null) {
            return "br";
         }

         if (var4 == -1.0F && this.zstdOptions != null) {
            return "zstd";
         }

         if (var5 == -1.0F && this.gzipOptions != null) {
            return "gzip";
         }

         if (var6 == -1.0F && this.deflateOptions != null) {
            return "deflate";
         }
      }

      return null;
   }

   @Deprecated
   protected ZlibWrapper determineWrapper(String var1) {
      float var2 = -1.0F;
      float var3 = -1.0F;
      float var4 = -1.0F;

      for (String var8 : var1.split(",")) {
         float var9 = 1.0F;
         int var10 = var8.indexOf(61);
         if (var10 != -1) {
            try {
               var9 = Float.parseFloat(var8.substring(var10 + 1));
            } catch (NumberFormatException var12) {
               var9 = 0.0F;
            }
         }

         if (var8.contains("*")) {
            var2 = var9;
         } else if (var8.contains("gzip") && var9 > var3) {
            var3 = var9;
         } else if (var8.contains("deflate") && var9 > var4) {
            var4 = var9;
         }
      }

      if (!(var3 > 0.0F) && !(var4 > 0.0F)) {
         if (var2 > 0.0F) {
            if (var3 == -1.0F) {
               return ZlibWrapper.GZIP;
            }

            if (var4 == -1.0F) {
               return ZlibWrapper.ZLIB;
            }
         }

         return null;
      } else {
         return var3 >= var4 ? ZlibWrapper.GZIP : ZlibWrapper.ZLIB;
      }
   }

   private final class BrEncoderFactory implements CompressionEncoderFactory {
      private BrEncoderFactory() {
      }

      @Override
      public MessageToByteEncoder<ByteBuf> createEncoder() {
         return new BrotliEncoder(HttpContentCompressor.this.brotliOptions.parameters());
      }
   }

   private final class DeflateEncoderFactory implements CompressionEncoderFactory {
      private DeflateEncoderFactory() {
      }

      @Override
      public MessageToByteEncoder<ByteBuf> createEncoder() {
         return ZlibCodecFactory.newZlibEncoder(
            ZlibWrapper.ZLIB,
            HttpContentCompressor.this.deflateOptions.compressionLevel(),
            HttpContentCompressor.this.deflateOptions.windowBits(),
            HttpContentCompressor.this.deflateOptions.memLevel()
         );
      }
   }

   private final class GzipEncoderFactory implements CompressionEncoderFactory {
      private GzipEncoderFactory() {
      }

      @Override
      public MessageToByteEncoder<ByteBuf> createEncoder() {
         return ZlibCodecFactory.newZlibEncoder(
            ZlibWrapper.GZIP,
            HttpContentCompressor.this.gzipOptions.compressionLevel(),
            HttpContentCompressor.this.gzipOptions.windowBits(),
            HttpContentCompressor.this.gzipOptions.memLevel()
         );
      }
   }

   private final class ZstdEncoderFactory implements CompressionEncoderFactory {
      private ZstdEncoderFactory() {
      }

      @Override
      public MessageToByteEncoder<ByteBuf> createEncoder() {
         return new ZstdEncoder(
            HttpContentCompressor.this.zstdOptions.compressionLevel(),
            HttpContentCompressor.this.zstdOptions.blockSize(),
            HttpContentCompressor.this.zstdOptions.maxEncodeSize()
         );
      }
   }
}
