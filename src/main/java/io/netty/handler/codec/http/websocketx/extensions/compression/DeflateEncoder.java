package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

abstract class DeflateEncoder extends WebSocketExtensionEncoder {
   private final int compressionLevel;
   private final int windowSize;
   private final boolean noContext;
   private final WebSocketExtensionFilter extensionEncoderFilter;
   private EmbeddedChannel encoder;

   DeflateEncoder(int var1, int var2, boolean var3, WebSocketExtensionFilter var4) {
      this.compressionLevel = var1;
      this.windowSize = var2;
      this.noContext = var3;
      this.extensionEncoderFilter = ObjectUtil.checkNotNull(var4, "extensionEncoderFilter");
   }

   protected WebSocketExtensionFilter extensionEncoderFilter() {
      return this.extensionEncoderFilter;
   }

   protected abstract int rsv(WebSocketFrame var1);

   protected abstract boolean removeFrameTail(WebSocketFrame var1);

   protected void encode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      ByteBuf var4;
      if (var2.content().isReadable()) {
         var4 = this.compressContent(var1, var2);
      } else {
         if (!var2.isFinalFragment()) {
            throw new CodecException("cannot compress content buffer");
         }

         var4 = PerMessageDeflateDecoder.EMPTY_DEFLATE_BLOCK.duplicate();
      }

      Object var5;
      if (var2 instanceof TextWebSocketFrame) {
         var5 = new TextWebSocketFrame(var2.isFinalFragment(), this.rsv(var2), var4);
      } else if (var2 instanceof BinaryWebSocketFrame) {
         var5 = new BinaryWebSocketFrame(var2.isFinalFragment(), this.rsv(var2), var4);
      } else {
         if (!(var2 instanceof ContinuationWebSocketFrame)) {
            throw new CodecException("unexpected frame type: " + var2.getClass().getName());
         }

         var5 = new ContinuationWebSocketFrame(var2.isFinalFragment(), this.rsv(var2), var4);
      }

      var3.add(var5);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cleanup();
      super.handlerRemoved(var1);
   }

   private ByteBuf compressContent(ChannelHandlerContext var1, WebSocketFrame var2) {
      if (this.encoder == null) {
         this.encoder = new EmbeddedChannel(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.NONE, this.compressionLevel, this.windowSize, 8));
      }

      this.encoder.writeOutbound(var2.content().retain());
      CompositeByteBuf var3 = var1.alloc().compositeBuffer();

      while (true) {
         ByteBuf var4 = this.encoder.readOutbound();
         if (var4 == null) {
            if (var3.numComponents() <= 0) {
               var3.release();
               throw new CodecException("cannot read compressed buffer");
            }

            if (var2.isFinalFragment() && this.noContext) {
               this.cleanup();
            }

            if (this.removeFrameTail(var2)) {
               int var5 = var3.readableBytes() - PerMessageDeflateDecoder.FRAME_TAIL.readableBytes();
               var4 = var3.slice(0, var5);
            } else {
               var4 = var3;
            }

            return var4;
         }

         if (!var4.isReadable()) {
            var4.release();
         } else {
            var3.addComponent(true, var4);
         }
      }
   }

   private void cleanup() {
      if (this.encoder != null) {
         this.encoder.finishAndReleaseAll();
         this.encoder = null;
      }
   }
}
