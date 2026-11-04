package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketServerProtocolConfig {
   static final long DEFAULT_HANDSHAKE_TIMEOUT_MILLIS = 10000L;
   private final String websocketPath;
   private final String subprotocols;
   private final boolean checkStartsWith;
   private final long handshakeTimeoutMillis;
   private final long forceCloseTimeoutMillis;
   private final boolean handleCloseFrames;
   private final WebSocketCloseStatus sendCloseFrame;
   private final boolean dropPongFrames;
   private final WebSocketDecoderConfig decoderConfig;

   private WebSocketServerProtocolConfig(
      String var1, String var2, boolean var3, long var4, long var6, boolean var8, WebSocketCloseStatus var9, boolean var10, WebSocketDecoderConfig var11
   ) {
      this.websocketPath = var1;
      this.subprotocols = var2;
      this.checkStartsWith = var3;
      this.handshakeTimeoutMillis = ObjectUtil.checkPositive(var4, "handshakeTimeoutMillis");
      this.forceCloseTimeoutMillis = var6;
      this.handleCloseFrames = var8;
      this.sendCloseFrame = var9;
      this.dropPongFrames = var10;
      this.decoderConfig = var11 == null ? WebSocketDecoderConfig.DEFAULT : var11;
   }

   public String websocketPath() {
      return this.websocketPath;
   }

   public String subprotocols() {
      return this.subprotocols;
   }

   public boolean checkStartsWith() {
      return this.checkStartsWith;
   }

   public long handshakeTimeoutMillis() {
      return this.handshakeTimeoutMillis;
   }

   public long forceCloseTimeoutMillis() {
      return this.forceCloseTimeoutMillis;
   }

   public boolean handleCloseFrames() {
      return this.handleCloseFrames;
   }

   public WebSocketCloseStatus sendCloseFrame() {
      return this.sendCloseFrame;
   }

   public boolean dropPongFrames() {
      return this.dropPongFrames;
   }

   public WebSocketDecoderConfig decoderConfig() {
      return this.decoderConfig;
   }

   @Override
   public String toString() {
      return "WebSocketServerProtocolConfig {websocketPath="
         + this.websocketPath
         + ", subprotocols="
         + this.subprotocols
         + ", checkStartsWith="
         + this.checkStartsWith
         + ", handshakeTimeoutMillis="
         + this.handshakeTimeoutMillis
         + ", forceCloseTimeoutMillis="
         + this.forceCloseTimeoutMillis
         + ", handleCloseFrames="
         + this.handleCloseFrames
         + ", sendCloseFrame="
         + this.sendCloseFrame
         + ", dropPongFrames="
         + this.dropPongFrames
         + ", decoderConfig="
         + this.decoderConfig
         + "}";
   }

   public WebSocketServerProtocolConfig.Builder toBuilder() {
      return new WebSocketServerProtocolConfig.Builder(this);
   }

   public static WebSocketServerProtocolConfig.Builder newBuilder() {
      return new WebSocketServerProtocolConfig.Builder(
         "/", null, false, 10000L, 0L, true, WebSocketCloseStatus.NORMAL_CLOSURE, true, WebSocketDecoderConfig.DEFAULT
      );
   }

   public static final class Builder {
      private String websocketPath;
      private String subprotocols;
      private boolean checkStartsWith;
      private long handshakeTimeoutMillis;
      private long forceCloseTimeoutMillis;
      private boolean handleCloseFrames;
      private WebSocketCloseStatus sendCloseFrame;
      private boolean dropPongFrames;
      private WebSocketDecoderConfig decoderConfig;
      private WebSocketDecoderConfig.Builder decoderConfigBuilder;

      private Builder(WebSocketServerProtocolConfig var1) {
         this(
            ObjectUtil.checkNotNull(var1, "serverConfig").websocketPath(),
            var1.subprotocols(),
            var1.checkStartsWith(),
            var1.handshakeTimeoutMillis(),
            var1.forceCloseTimeoutMillis(),
            var1.handleCloseFrames(),
            var1.sendCloseFrame(),
            var1.dropPongFrames(),
            var1.decoderConfig()
         );
      }

      private Builder(
         String var1, String var2, boolean var3, long var4, long var6, boolean var8, WebSocketCloseStatus var9, boolean var10, WebSocketDecoderConfig var11
      ) {
         this.websocketPath = var1;
         this.subprotocols = var2;
         this.checkStartsWith = var3;
         this.handshakeTimeoutMillis = var4;
         this.forceCloseTimeoutMillis = var6;
         this.handleCloseFrames = var8;
         this.sendCloseFrame = var9;
         this.dropPongFrames = var10;
         this.decoderConfig = var11;
      }

      public WebSocketServerProtocolConfig.Builder websocketPath(String var1) {
         this.websocketPath = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder subprotocols(String var1) {
         this.subprotocols = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder checkStartsWith(boolean var1) {
         this.checkStartsWith = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder handshakeTimeoutMillis(long var1) {
         this.handshakeTimeoutMillis = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder forceCloseTimeoutMillis(long var1) {
         this.forceCloseTimeoutMillis = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder handleCloseFrames(boolean var1) {
         this.handleCloseFrames = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder sendCloseFrame(WebSocketCloseStatus var1) {
         this.sendCloseFrame = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder dropPongFrames(boolean var1) {
         this.dropPongFrames = var1;
         return this;
      }

      public WebSocketServerProtocolConfig.Builder decoderConfig(WebSocketDecoderConfig var1) {
         this.decoderConfig = var1 == null ? WebSocketDecoderConfig.DEFAULT : var1;
         this.decoderConfigBuilder = null;
         return this;
      }

      private WebSocketDecoderConfig.Builder decoderConfigBuilder() {
         if (this.decoderConfigBuilder == null) {
            this.decoderConfigBuilder = this.decoderConfig.toBuilder();
         }

         return this.decoderConfigBuilder;
      }

      public WebSocketServerProtocolConfig.Builder maxFramePayloadLength(int var1) {
         this.decoderConfigBuilder().maxFramePayloadLength(var1);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder expectMaskedFrames(boolean var1) {
         this.decoderConfigBuilder().expectMaskedFrames(var1);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder allowMaskMismatch(boolean var1) {
         this.decoderConfigBuilder().allowMaskMismatch(var1);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder allowExtensions(boolean var1) {
         this.decoderConfigBuilder().allowExtensions(var1);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder closeOnProtocolViolation(boolean var1) {
         this.decoderConfigBuilder().closeOnProtocolViolation(var1);
         return this;
      }

      public WebSocketServerProtocolConfig.Builder withUTF8Validator(boolean var1) {
         this.decoderConfigBuilder().withUTF8Validator(var1);
         return this;
      }

      public WebSocketServerProtocolConfig build() {
         return new WebSocketServerProtocolConfig(
            this.websocketPath,
            this.subprotocols,
            this.checkStartsWith,
            this.handshakeTimeoutMillis,
            this.forceCloseTimeoutMillis,
            this.handleCloseFrames,
            this.sendCloseFrame,
            this.dropPongFrames,
            this.decoderConfigBuilder == null ? this.decoderConfig : this.decoderConfigBuilder.build()
         );
      }
   }
}
