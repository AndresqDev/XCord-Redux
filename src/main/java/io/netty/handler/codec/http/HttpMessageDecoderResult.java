package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;

public final class HttpMessageDecoderResult extends DecoderResult {
   private final int initialLineLength;
   private final int headerSize;

   HttpMessageDecoderResult(int var1, int var2) {
      super(SIGNAL_SUCCESS);
      this.initialLineLength = var1;
      this.headerSize = var2;
   }

   public int initialLineLength() {
      return this.initialLineLength;
   }

   public int headerSize() {
      return this.headerSize;
   }

   public int totalSize() {
      return this.initialLineLength + this.headerSize;
   }
}
