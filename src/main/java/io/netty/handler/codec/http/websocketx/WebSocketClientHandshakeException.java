package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

public final class WebSocketClientHandshakeException extends WebSocketHandshakeException {
   private static final long serialVersionUID = 1L;
   private final HttpResponse response;

   public WebSocketClientHandshakeException(String var1) {
      this(var1, null);
   }

   public WebSocketClientHandshakeException(String var1, HttpResponse var2) {
      super(var1);
      if (var2 != null) {
         this.response = new DefaultHttpResponse(var2.protocolVersion(), var2.status(), var2.headers());
      } else {
         this.response = null;
      }
   }

   public HttpResponse response() {
      return this.response;
   }
}
