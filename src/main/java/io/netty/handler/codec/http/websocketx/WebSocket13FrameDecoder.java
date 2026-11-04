package io.netty.handler.codec.http.websocketx;

public class WebSocket13FrameDecoder extends WebSocket08FrameDecoder {
   public WebSocket13FrameDecoder(boolean var1, boolean var2, int var3) {
      this(var1, var2, var3, false);
   }

   public WebSocket13FrameDecoder(boolean var1, boolean var2, int var3, boolean var4) {
      this(WebSocketDecoderConfig.newBuilder().expectMaskedFrames(var1).allowExtensions(var2).maxFramePayloadLength(var3).allowMaskMismatch(var4).build());
   }

   public WebSocket13FrameDecoder(WebSocketDecoderConfig var1) {
      super(var1);
   }
}
