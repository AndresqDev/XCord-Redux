package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.CorruptedFrameException;

public final class CorruptedWebSocketFrameException extends CorruptedFrameException {
   private static final long serialVersionUID = 3918055132492988338L;
   private final WebSocketCloseStatus closeStatus;

   public CorruptedWebSocketFrameException() {
      this(WebSocketCloseStatus.PROTOCOL_ERROR, null, null);
   }

   public CorruptedWebSocketFrameException(WebSocketCloseStatus var1, String var2, Throwable var3) {
      super(var2 == null ? var1.reasonText() : var2, var3);
      this.closeStatus = var1;
   }

   public CorruptedWebSocketFrameException(WebSocketCloseStatus var1, String var2) {
      this(var1, var2, null);
   }

   public CorruptedWebSocketFrameException(WebSocketCloseStatus var1, Throwable var2) {
      this(var1, null, var2);
   }

   public WebSocketCloseStatus closeStatus() {
      return this.closeStatus;
   }
}
