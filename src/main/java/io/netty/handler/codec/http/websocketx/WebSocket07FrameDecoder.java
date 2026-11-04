package io.netty.handler.codec.http.websocketx;

public class WebSocket07FrameDecoder extends WebSocket08FrameDecoder {
   public WebSocket07FrameDecoder(boolean var1, boolean var2, int var3) {
      this(WebSocketDecoderConfig.newBuilder().expectMaskedFrames(var1).allowExtensions(var2).maxFramePayloadLength(var3).build());
   }

   public WebSocket07FrameDecoder(boolean var1, boolean var2, int var3, boolean var4) {
      this(WebSocketDecoderConfig.newBuilder().expectMaskedFrames(var1).allowExtensions(var2).maxFramePayloadLength(var3).allowMaskMismatch(var4).build());
   }

   public WebSocket07FrameDecoder(WebSocketDecoderConfig var1) {
      super(var1);
   }
}
