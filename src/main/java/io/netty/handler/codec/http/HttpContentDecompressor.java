package io.netty.handler.codec.http;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.Brotli;
import io.netty.handler.codec.compression.BrotliDecoder;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;

public class HttpContentDecompressor extends HttpContentDecoder {
   private final boolean strict;

   public HttpContentDecompressor() {
      this(false);
   }

   public HttpContentDecompressor(boolean var1) {
      this.strict = var1;
   }

   @Override
   protected EmbeddedChannel newContentDecoder(String var1) throws Exception {
      if (HttpHeaderValues.GZIP.contentEqualsIgnoreCase(var1) || HttpHeaderValues.X_GZIP.contentEqualsIgnoreCase(var1)) {
         return new EmbeddedChannel(
            this.ctx.channel().id(),
            this.ctx.channel().metadata().hasDisconnect(),
            this.ctx.channel().config(),
            ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP)
         );
      } else if (HttpHeaderValues.DEFLATE.contentEqualsIgnoreCase(var1) || HttpHeaderValues.X_DEFLATE.contentEqualsIgnoreCase(var1)) {
         ZlibWrapper var2 = this.strict ? ZlibWrapper.ZLIB : ZlibWrapper.ZLIB_OR_NONE;
         return new EmbeddedChannel(
            this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), ZlibCodecFactory.newZlibDecoder(var2)
         );
      } else {
         return Brotli.isAvailable() && HttpHeaderValues.BR.contentEqualsIgnoreCase(var1)
            ? new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new BrotliDecoder())
            : null;
      }
   }
}
