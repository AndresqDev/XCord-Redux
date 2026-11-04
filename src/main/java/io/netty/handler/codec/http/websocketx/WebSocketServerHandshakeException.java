package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

public final class WebSocketServerHandshakeException extends WebSocketHandshakeException {
   private static final long serialVersionUID = 1L;
   private final HttpRequest request;

   public WebSocketServerHandshakeException(String var1) {
      this(var1, null);
   }

   public WebSocketServerHandshakeException(String var1, HttpRequest var2) {
      super(var1);
      if (var2 != null) {
         this.request = new DefaultHttpRequest(var2.protocolVersion(), var2.method(), var2.uri(), var2.headers());
      } else {
         this.request = null;
      }
   }

   public HttpRequest request() {
      return this.request;
   }
}
