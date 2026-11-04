package io.netty.handler.ssl.ocsp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.ReferenceCountedOpenSslEngine;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.internal.ObjectUtil;
import javax.net.ssl.SSLHandshakeException;

public abstract class OcspClientHandler extends ChannelInboundHandlerAdapter {
   private final ReferenceCountedOpenSslEngine engine;

   protected OcspClientHandler(ReferenceCountedOpenSslEngine var1) {
      this.engine = ObjectUtil.checkNotNull(var1, "engine");
   }

   protected abstract boolean verify(ChannelHandlerContext var1, ReferenceCountedOpenSslEngine var2) throws Exception;

   @Override
   public void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 instanceof SslHandshakeCompletionEvent) {
         var1.pipeline().remove(this);
         SslHandshakeCompletionEvent var3 = (SslHandshakeCompletionEvent)var2;
         if (var3.isSuccess() && !this.verify(var1, this.engine)) {
            throw new SSLHandshakeException("Bad OCSP response");
         }
      }

      var1.fireUserEventTriggered(var2);
   }
}
