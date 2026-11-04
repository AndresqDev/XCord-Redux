package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.util.internal.ObjectUtil;
import java.net.URI;

public final class WebSocketClientProtocolConfig {
   static final boolean DEFAULT_PERFORM_MASKING = true;
   static final boolean DEFAULT_ALLOW_MASK_MISMATCH = false;
   static final boolean DEFAULT_HANDLE_CLOSE_FRAMES = true;
   static final boolean DEFAULT_DROP_PONG_FRAMES = true;
   private final URI webSocketUri;
   private final String subprotocol;
   private final WebSocketVersion version;
   private final boolean allowExtensions;
   private final HttpHeaders customHeaders;
   private final int maxFramePayloadLength;
   private final boolean performMasking;
   private final boolean allowMaskMismatch;
   private final boolean handleCloseFrames;
   private final WebSocketCloseStatus sendCloseFrame;
   private final boolean dropPongFrames;
   private final long handshakeTimeoutMillis;
   private final long forceCloseTimeoutMillis;
   private final boolean absoluteUpgradeUrl;

   private WebSocketClientProtocolConfig(
      URI var1,
      String var2,
      WebSocketVersion var3,
      boolean var4,
      HttpHeaders var5,
      int var6,
      boolean var7,
      boolean var8,
      boolean var9,
      WebSocketCloseStatus var10,
      boolean var11,
      long var12,
      long var14,
      boolean var16
   ) {
      this.webSocketUri = var1;
      this.subprotocol = var2;
      this.version = var3;
      this.allowExtensions = var4;
      this.customHeaders = var5;
      this.maxFramePayloadLength = var6;
      this.performMasking = var7;
      this.allowMaskMismatch = var8;
      this.forceCloseTimeoutMillis = var14;
      this.handleCloseFrames = var9;
      this.sendCloseFrame = var10;
      this.dropPongFrames = var11;
      this.handshakeTimeoutMillis = ObjectUtil.checkPositive(var12, "handshakeTimeoutMillis");
      this.absoluteUpgradeUrl = var16;
   }

   public URI webSocketUri() {
      return this.webSocketUri;
   }

   public String subprotocol() {
      return this.subprotocol;
   }

   public WebSocketVersion version() {
      return this.version;
   }

   public boolean allowExtensions() {
      return this.allowExtensions;
   }

   public HttpHeaders customHeaders() {
      return this.customHeaders;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public boolean performMasking() {
      return this.performMasking;
   }

   public boolean allowMaskMismatch() {
      return this.allowMaskMismatch;
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

   public long handshakeTimeoutMillis() {
      return this.handshakeTimeoutMillis;
   }

   public long forceCloseTimeoutMillis() {
      return this.forceCloseTimeoutMillis;
   }

   public boolean absoluteUpgradeUrl() {
      return this.absoluteUpgradeUrl;
   }

   @Override
   public String toString() {
      return "WebSocketClientProtocolConfig {webSocketUri="
         + this.webSocketUri
         + ", subprotocol="
         + this.subprotocol
         + ", version="
         + this.version
         + ", allowExtensions="
         + this.allowExtensions
         + ", customHeaders="
         + this.customHeaders
         + ", maxFramePayloadLength="
         + this.maxFramePayloadLength
         + ", performMasking="
         + this.performMasking
         + ", allowMaskMismatch="
         + this.allowMaskMismatch
         + ", handleCloseFrames="
         + this.handleCloseFrames
         + ", sendCloseFrame="
         + this.sendCloseFrame
         + ", dropPongFrames="
         + this.dropPongFrames
         + ", handshakeTimeoutMillis="
         + this.handshakeTimeoutMillis
         + ", forceCloseTimeoutMillis="
         + this.forceCloseTimeoutMillis
         + ", absoluteUpgradeUrl="
         + this.absoluteUpgradeUrl
         + "}";
   }

   public WebSocketClientProtocolConfig.Builder toBuilder() {
      return new WebSocketClientProtocolConfig.Builder(this);
   }

   public static WebSocketClientProtocolConfig.Builder newBuilder() {
      return new WebSocketClientProtocolConfig.Builder(
         URI.create("https://localhost/"),
         null,
         WebSocketVersion.V13,
         false,
         EmptyHttpHeaders.INSTANCE,
         65536,
         true,
         false,
         true,
         WebSocketCloseStatus.NORMAL_CLOSURE,
         true,
         10000L,
         -1L,
         false
      );
   }

   public static final class Builder {
      private URI webSocketUri;
      private String subprotocol;
      private WebSocketVersion version;
      private boolean allowExtensions;
      private HttpHeaders customHeaders;
      private int maxFramePayloadLength;
      private boolean performMasking;
      private boolean allowMaskMismatch;
      private boolean handleCloseFrames;
      private WebSocketCloseStatus sendCloseFrame;
      private boolean dropPongFrames;
      private long handshakeTimeoutMillis;
      private long forceCloseTimeoutMillis;
      private boolean absoluteUpgradeUrl;

      private Builder(WebSocketClientProtocolConfig var1) {
         this(
            ObjectUtil.checkNotNull(var1, "clientConfig").webSocketUri(),
            var1.subprotocol(),
            var1.version(),
            var1.allowExtensions(),
            var1.customHeaders(),
            var1.maxFramePayloadLength(),
            var1.performMasking(),
            var1.allowMaskMismatch(),
            var1.handleCloseFrames(),
            var1.sendCloseFrame(),
            var1.dropPongFrames(),
            var1.handshakeTimeoutMillis(),
            var1.forceCloseTimeoutMillis(),
            var1.absoluteUpgradeUrl()
         );
      }

      private Builder(
         URI var1,
         String var2,
         WebSocketVersion var3,
         boolean var4,
         HttpHeaders var5,
         int var6,
         boolean var7,
         boolean var8,
         boolean var9,
         WebSocketCloseStatus var10,
         boolean var11,
         long var12,
         long var14,
         boolean var16
      ) {
         this.webSocketUri = var1;
         this.subprotocol = var2;
         this.version = var3;
         this.allowExtensions = var4;
         this.customHeaders = var5;
         this.maxFramePayloadLength = var6;
         this.performMasking = var7;
         this.allowMaskMismatch = var8;
         this.handleCloseFrames = var9;
         this.sendCloseFrame = var10;
         this.dropPongFrames = var11;
         this.handshakeTimeoutMillis = var12;
         this.forceCloseTimeoutMillis = var14;
         this.absoluteUpgradeUrl = var16;
      }

      public WebSocketClientProtocolConfig.Builder webSocketUri(String var1) {
         return this.webSocketUri(URI.create(var1));
      }

      public WebSocketClientProtocolConfig.Builder webSocketUri(URI var1) {
         this.webSocketUri = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder subprotocol(String var1) {
         this.subprotocol = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder version(WebSocketVersion var1) {
         this.version = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder allowExtensions(boolean var1) {
         this.allowExtensions = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder customHeaders(HttpHeaders var1) {
         this.customHeaders = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder maxFramePayloadLength(int var1) {
         this.maxFramePayloadLength = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder performMasking(boolean var1) {
         this.performMasking = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder allowMaskMismatch(boolean var1) {
         this.allowMaskMismatch = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder handleCloseFrames(boolean var1) {
         this.handleCloseFrames = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder sendCloseFrame(WebSocketCloseStatus var1) {
         this.sendCloseFrame = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder dropPongFrames(boolean var1) {
         this.dropPongFrames = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder handshakeTimeoutMillis(long var1) {
         this.handshakeTimeoutMillis = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder forceCloseTimeoutMillis(long var1) {
         this.forceCloseTimeoutMillis = var1;
         return this;
      }

      public WebSocketClientProtocolConfig.Builder absoluteUpgradeUrl(boolean var1) {
         this.absoluteUpgradeUrl = var1;
         return this;
      }

      public WebSocketClientProtocolConfig build() {
         return new WebSocketClientProtocolConfig(
            this.webSocketUri,
            this.subprotocol,
            this.version,
            this.allowExtensions,
            this.customHeaders,
            this.maxFramePayloadLength,
            this.performMasking,
            this.allowMaskMismatch,
            this.handleCloseFrames,
            this.sendCloseFrame,
            this.dropPongFrames,
            this.handshakeTimeoutMillis,
            this.forceCloseTimeoutMillis,
            this.absoluteUpgradeUrl
         );
      }
   }
}
