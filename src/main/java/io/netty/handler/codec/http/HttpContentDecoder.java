package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public abstract class HttpContentDecoder extends MessageToMessageDecoder<HttpObject> {
   static final String IDENTITY = HttpHeaderValues.IDENTITY.toString();
   protected ChannelHandlerContext ctx;
   private EmbeddedChannel decoder;
   private boolean continueResponse;
   private boolean needRead = true;

   protected void decode(ChannelHandlerContext var1, HttpObject var2, List<Object> var3) throws Exception {
      try {
         if (var2 instanceof HttpResponse && ((HttpResponse)var2).status().code() == 100) {
            if (!(var2 instanceof LastHttpContent)) {
               this.continueResponse = true;
            }

            var3.add(ReferenceCountUtil.retain(var2));
            return;
         }

         if (this.continueResponse) {
            if (var2 instanceof LastHttpContent) {
               this.continueResponse = false;
            }

            var3.add(ReferenceCountUtil.retain(var2));
            return;
         }

         if (var2 instanceof HttpMessage) {
            this.cleanup();
            HttpMessage var4 = (HttpMessage)var2;
            HttpHeaders var5 = var4.headers();
            String var6 = var5.get(HttpHeaderNames.CONTENT_ENCODING);
            if (var6 != null) {
               var6 = var6.trim();
            } else {
               String var7 = var5.get(HttpHeaderNames.TRANSFER_ENCODING);
               if (var7 != null) {
                  int var8 = var7.indexOf(",");
                  if (var8 != -1) {
                     var6 = var7.substring(0, var8).trim();
                  } else {
                     var6 = var7.trim();
                  }
               } else {
                  var6 = IDENTITY;
               }
            }

            this.decoder = this.newContentDecoder(var6);
            if (this.decoder == null) {
               if (var4 instanceof HttpContent) {
                  ((HttpContent)var4).retain();
               }

               var3.add(var4);
               return;
            }

            if (var5.contains(HttpHeaderNames.CONTENT_LENGTH)) {
               var5.remove(HttpHeaderNames.CONTENT_LENGTH);
               var5.set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            }

            String var15 = this.getTargetContentEncoding(var6);
            if (HttpHeaderValues.IDENTITY.contentEquals(var15)) {
               var5.remove(HttpHeaderNames.CONTENT_ENCODING);
            } else {
               var5.set(HttpHeaderNames.CONTENT_ENCODING, var15);
            }

            if (var4 instanceof HttpContent) {
               Object var16;
               if (var4 instanceof HttpRequest) {
                  HttpRequest var9 = (HttpRequest)var4;
                  var16 = new DefaultHttpRequest(var9.protocolVersion(), var9.method(), var9.uri());
               } else {
                  if (!(var4 instanceof HttpResponse)) {
                     throw new CodecException("Object of class " + var4.getClass().getName() + " is not an HttpRequest or HttpResponse");
                  }

                  HttpResponse var17 = (HttpResponse)var4;
                  var16 = new DefaultHttpResponse(var17.protocolVersion(), var17.status());
               }

               ((HttpMessage)var16).headers().set(var4.headers());
               ((HttpMessage)var16).setDecoderResult(var4.decoderResult());
               var3.add(var16);
            } else {
               var3.add(var4);
            }
         }

         if (!(var2 instanceof HttpContent)) {
            return;
         }

         HttpContent var13 = (HttpContent)var2;
         if (this.decoder == null) {
            var3.add(var13.retain());
         } else {
            this.decodeContent(var13, var3);
         }
      } finally {
         this.needRead = var3.isEmpty();
      }
   }

   private void decodeContent(HttpContent var1, List<Object> var2) {
      ByteBuf var3 = var1.content();
      this.decode(var3, var2);
      if (var1 instanceof LastHttpContent) {
         this.finishDecode(var2);
         LastHttpContent var4 = (LastHttpContent)var1;
         HttpHeaders var5 = var4.trailingHeaders();
         if (var5.isEmpty()) {
            var2.add(LastHttpContent.EMPTY_LAST_CONTENT);
         } else {
            var2.add(new ComposedLastHttpContent(var5, DecoderResult.SUCCESS));
         }
      }
   }

   @Override
   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      boolean var2 = this.needRead;
      this.needRead = true;

      try {
         var1.fireChannelReadComplete();
      } finally {
         if (var2 && !var1.channel().config().isAutoRead()) {
            var1.read();
         }
      }
   }

   protected abstract EmbeddedChannel newContentDecoder(String var1) throws Exception;

   protected String getTargetContentEncoding(String var1) throws Exception {
      return IDENTITY;
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      this.cleanupSafely(var1);
      super.handlerRemoved(var1);
   }

   @Override
   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.cleanupSafely(var1);
      super.channelInactive(var1);
   }

   @Override
   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      super.handlerAdded(var1);
   }

   private void cleanup() {
      if (this.decoder != null) {
         this.decoder.finishAndReleaseAll();
         this.decoder = null;
      }
   }

   private void cleanupSafely(ChannelHandlerContext var1) {
      try {
         this.cleanup();
      } catch (Throwable var3) {
         var1.fireExceptionCaught(var3);
      }
   }

   private void decode(ByteBuf var1, List<Object> var2) {
      this.decoder.writeInbound(var1.retain());
      this.fetchDecoderOutput(var2);
   }

   private void finishDecode(List<Object> var1) {
      if (this.decoder.finish()) {
         this.fetchDecoderOutput(var1);
      }

      this.decoder = null;
   }

   private void fetchDecoderOutput(List<Object> var1) {
      while (true) {
         ByteBuf var2 = this.decoder.readInbound();
         if (var2 == null) {
            return;
         }

         if (!var2.isReadable()) {
            var2.release();
         } else {
            var1.add(new DefaultHttpContent(var2));
         }
      }
   }
}
