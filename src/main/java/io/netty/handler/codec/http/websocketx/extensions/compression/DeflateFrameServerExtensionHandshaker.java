package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;

public final class DeflateFrameServerExtensionHandshaker implements WebSocketServerExtensionHandshaker {
   static final String X_WEBKIT_DEFLATE_FRAME_EXTENSION = "x-webkit-deflate-frame";
   static final String DEFLATE_FRAME_EXTENSION = "deflate-frame";
   private final int compressionLevel;
   private final WebSocketExtensionFilterProvider extensionFilterProvider;

   public DeflateFrameServerExtensionHandshaker() {
      this(6);
   }

   public DeflateFrameServerExtensionHandshaker(int var1) {
      this(var1, WebSocketExtensionFilterProvider.DEFAULT);
   }

   public DeflateFrameServerExtensionHandshaker(int var1, WebSocketExtensionFilterProvider var2) {
      if (var1 >= 0 && var1 <= 9) {
         this.compressionLevel = var1;
         this.extensionFilterProvider = ObjectUtil.checkNotNull(var2, "extensionFilterProvider");
      } else {
         throw new IllegalArgumentException("compressionLevel: " + var1 + " (expected: 0-9)");
      }
   }

   @Override
   public WebSocketServerExtension handshakeExtension(WebSocketExtensionData var1) {
      if (!"x-webkit-deflate-frame".equals(var1.name()) && !"deflate-frame".equals(var1.name())) {
         return null;
      } else {
         return var1.parameters().isEmpty()
            ? new DeflateFrameServerExtensionHandshaker.DeflateFrameServerExtension(this.compressionLevel, var1.name(), this.extensionFilterProvider)
            : null;
      }
   }

   private static class DeflateFrameServerExtension implements WebSocketServerExtension {
      private final String extensionName;
      private final int compressionLevel;
      private final WebSocketExtensionFilterProvider extensionFilterProvider;

      DeflateFrameServerExtension(int var1, String var2, WebSocketExtensionFilterProvider var3) {
         this.extensionName = var2;
         this.compressionLevel = var1;
         this.extensionFilterProvider = var3;
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

      @Override
      public WebSocketExtensionData newReponseData() {
         return new WebSocketExtensionData(this.extensionName, Collections.emptyMap());
      }
   }
}
