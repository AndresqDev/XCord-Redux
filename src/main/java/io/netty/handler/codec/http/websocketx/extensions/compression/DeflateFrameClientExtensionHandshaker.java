package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketClientExtensionHandshaker;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;

public final class DeflateFrameClientExtensionHandshaker implements WebSocketClientExtensionHandshaker {
   private final int compressionLevel;
   private final boolean useWebkitExtensionName;
   private final WebSocketExtensionFilterProvider extensionFilterProvider;

   public DeflateFrameClientExtensionHandshaker(boolean var1) {
      this(6, var1);
   }

   public DeflateFrameClientExtensionHandshaker(int var1, boolean var2) {
      this(var1, var2, WebSocketExtensionFilterProvider.DEFAULT);
   }

   public DeflateFrameClientExtensionHandshaker(int var1, boolean var2, WebSocketExtensionFilterProvider var3) {
      if (var1 >= 0 && var1 <= 9) {
         this.compressionLevel = var1;
         this.useWebkitExtensionName = var2;
         this.extensionFilterProvider = ObjectUtil.checkNotNull(var3, "extensionFilterProvider");
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
      }
   }

   @Override
   public WebSocketExtensionData newRequestData() {
      return new WebSocketExtensionData(this.useWebkitExtensionName ? "x-webkit-deflate-frame" : "deflate-frame", Collections.emptyMap());
   }

   @Override
   public WebSocketClientExtension handshakeExtension(WebSocketExtensionData var1) {
      if (!"x-webkit-deflate-frame".equals(var1.name()) && !"deflate-frame".equals(var1.name())) {
         return null;
      } else {
         return var1.parameters().isEmpty()
            ? new DeflateFrameClientExtensionHandshaker.DeflateFrameClientExtension(this.compressionLevel, this.extensionFilterProvider)
            : null;
      }
   }

   private static class DeflateFrameClientExtension implements WebSocketClientExtension {
      private final int compressionLevel;
      private final WebSocketExtensionFilterProvider extensionFilterProvider;

      DeflateFrameClientExtension(int var1, WebSocketExtensionFilterProvider var2) {
         this.compressionLevel = var1;
         this.extensionFilterProvider = var2;
      }

      @Override
      public int rsv() {
         return 4;
      }

      @Override
      public WebSocketExtensionEncoder newExtensionEncoder() {
         return new PerFrameDeflateEncoder(this.compressionLevel, 15, false, this.extensionFilterProvider.encoderFilter());
      }

      @Override
      public WebSocketExtensionDecoder newExtensionDecoder() {
         return new PerFrameDeflateDecoder(false, this.extensionFilterProvider.decoderFilter());
      }
   }
}
