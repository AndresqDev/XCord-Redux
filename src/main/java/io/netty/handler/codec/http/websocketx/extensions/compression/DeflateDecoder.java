package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilter;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

abstract class DeflateDecoder extends WebSocketExtensionDecoder {
   static final ByteBuf FRAME_TAIL = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[]{0, 0, -1, -1})).asReadOnly();
   static final ByteBuf EMPTY_DEFLATE_BLOCK = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[]{0})).asReadOnly();
   private final boolean noContext;
   private final WebSocketExtensionFilter extensionDecoderFilter;
   private EmbeddedChannel decoder;

   DeflateDecoder(boolean var1, WebSocketExtensionFilter var2) {
      this.noContext = var1;
      this.extensionDecoderFilter = ObjectUtil.checkNotNull(var2, "extensionDecoderFilter");
   }

   protected WebSocketExtensionFilter extensionDecoderFilter() {
      return this.extensionDecoderFilter;
   }

   protected abstract boolean appendFrameTail(WebSocketFrame var1);

   protected abstract int newRsv(WebSocketFrame var1);

   protected void decode(ChannelHandlerContext var1, WebSocketFrame var2, List<Object> var3) throws Exception {
      ByteBuf var4 = this.decompressContent(var1, var2);
      Object var5;
      if (var2 instanceof TextWebSocketFrame) {
         var5 = new TextWebSocketFrame(var2.isFinalFragment(), this.newRsv(var2), var4);
      } else if (var2 instanceof BinaryWebSocketFrame) {
         var5 = new BinaryWebSocketFrame(var2.isFinalFragment(), this.newRsv(var2), var4);
      } else {
         if (!(var2 instanceof ContinuationWebSocketFrame)) {
            throw new CodecException("unexpected frame type: " + var2.getClass().getName());
         }

         var5 = new ContinuationWebSocketFrame(var2.isFinalFragment(), this.newRsv(var2), var4);
      }

      var3.add(var5);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cleanup();
      super.handlerRemoved(var1);
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.cleanup();
      super.channelInactive(var1);
   }

   private ByteBuf decompressContent(ChannelHandlerContext var1, WebSocketFrame var2) {
      if (this.decoder == null) {
         if (!(var2 instanceof TextWebSocketFrame) && !(var2 instanceof BinaryWebSocketFrame)) {
            throw new CodecException("unexpected initial frame type: " + var2.getClass().getName());
         }

         this.decoder = new EmbeddedChannel(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.NONE));
      }

      boolean var3 = var2.content().isReadable();
      boolean var4 = EMPTY_DEFLATE_BLOCK.equals(var2.content());
      this.decoder.writeInbound(var2.content().retain());
      if (this.appendFrameTail(var2)) {
         this.decoder.writeInbound(FRAME_TAIL.duplicate());
      }

      CompositeByteBuf var5 = var1.alloc().compositeBuffer();

      while (true) {
         ByteBuf var6 = this.decoder.readInbound();
         if (var6 == null) {
            if (!var4 && var3 && var5.numComponents() <= 0 && !(var2 instanceof ContinuationWebSocketFrame)) {
               var5.release();
               throw new CodecException("cannot read uncompressed buffer");
            }

            if (var2.isFinalFragment() && this.noContext) {
               this.cleanup();
            }

            return var5;
         }

         if (!var6.isReadable()) {
            var6.release();
         } else {
            var5.addComponent(true, var6);
         }
      }
   }

   private void cleanup() {
      if (this.decoder != null) {
         this.decoder.finishAndReleaseAll();
         this.decoder = null;
      }
   }
}
