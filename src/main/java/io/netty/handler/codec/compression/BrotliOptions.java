package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.Encoder.Mode;
import com.aayushatharva.brotli4j.encoder.Encoder.Parameters;
import io.netty.util.internal.ObjectUtil;

public final class BrotliOptions implements CompressionOptions {
   private final Parameters parameters;
   static final BrotliOptions DEFAULT = new BrotliOptions(new Parameters().setQuality(4).setMode(Mode.TEXT));

   BrotliOptions(Parameters var1) {
      if (!Brotli.isAvailable()) {
         throw new IllegalStateException("Brotli is not available", Brotli.cause());
      } else {
         this.parameters = ObjectUtil.checkNotNull(var1, "Parameters");
      }
   }

   public Parameters parameters() {
      return this.parameters;
   }
}
