package io.netty.handler.codec.http.websocketx;

import io.netty.util.internal.ObjectUtil;

public final class WebSocketDecoderConfig {
   static final WebSocketDecoderConfig DEFAULT = new WebSocketDecoderConfig(65536, true, false, false, true, true);
   private final int maxFramePayloadLength;
   private final boolean expectMaskedFrames;
   private final boolean allowMaskMismatch;
   private final boolean allowExtensions;
   private final boolean closeOnProtocolViolation;
   private final boolean withUTF8Validator;

   private WebSocketDecoderConfig(int var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      this.maxFramePayloadLength = var1;
      this.expectMaskedFrames = var2;
      this.allowMaskMismatch = var3;
      this.allowExtensions = var4;
      this.closeOnProtocolViolation = var5;
      this.withUTF8Validator = var6;
   }

   public int maxFramePayloadLength() {
      return this.maxFramePayloadLength;
   }

   public boolean expectMaskedFrames() {
      return this.expectMaskedFrames;
   }

   public boolean allowMaskMismatch() {
      return this.allowMaskMismatch;
   }

   public boolean allowExtensions() {
      return this.allowExtensions;
   }

   public boolean closeOnProtocolViolation() {
      return this.closeOnProtocolViolation;
   }

   public boolean withUTF8Validator() {
      return this.withUTF8Validator;
   }

   @Override
   public String toString() {
      return "WebSocketDecoderConfig [maxFramePayloadLength="
         + this.maxFramePayloadLength
         + ", expectMaskedFrames="
         + this.expectMaskedFrames
         + ", allowMaskMismatch="
         + this.allowMaskMismatch
         + ", allowExtensions="
         + this.allowExtensions
         + ", closeOnProtocolViolation="
         + this.closeOnProtocolViolation
         + ", withUTF8Validator="
         + this.withUTF8Validator
         + "]";
   }

   public WebSocketDecoderConfig.Builder toBuilder() {
      return new WebSocketDecoderConfig.Builder(this);
   }

   public static WebSocketDecoderConfig.Builder newBuilder() {
      return new WebSocketDecoderConfig.Builder(DEFAULT);
   }

   public static final class Builder {
      private int maxFramePayloadLength;
      private boolean expectMaskedFrames;
      private boolean allowMaskMismatch;
      private boolean allowExtensions;
      private boolean closeOnProtocolViolation;
      private boolean withUTF8Validator;

      private Builder(WebSocketDecoderConfig var1) {
         ObjectUtil.checkNotNull(var1, "decoderConfig");
         this.maxFramePayloadLength = var1.maxFramePayloadLength();
         this.expectMaskedFrames = var1.expectMaskedFrames();
         this.allowMaskMismatch = var1.allowMaskMismatch();
         this.allowExtensions = var1.allowExtensions();
         this.closeOnProtocolViolation = var1.closeOnProtocolViolation();
         this.withUTF8Validator = var1.withUTF8Validator();
      }

      public WebSocketDecoderConfig.Builder maxFramePayloadLength(int var1) {
         this.maxFramePayloadLength = var1;
         return this;
      }

      public WebSocketDecoderConfig.Builder expectMaskedFrames(boolean var1) {
         this.expectMaskedFrames = var1;
         return this;
      }

      public WebSocketDecoderConfig.Builder allowMaskMismatch(boolean var1) {
         this.allowMaskMismatch = var1;
         return this;
      }

      public WebSocketDecoderConfig.Builder allowExtensions(boolean var1) {
         this.allowExtensions = var1;
         return this;
      }

      public WebSocketDecoderConfig.Builder closeOnProtocolViolation(boolean var1) {
         this.closeOnProtocolViolation = var1;
         return this;
      }

      public WebSocketDecoderConfig.Builder withUTF8Validator(boolean var1) {
         this.withUTF8Validator = var1;
         return this;
      }

      public WebSocketDecoderConfig build() {
         return new WebSocketDecoderConfig(
            this.maxFramePayloadLength,
            this.expectMaskedFrames,
            this.allowMaskMismatch,
            this.allowExtensions,
            this.closeOnProtocolViolation,
            this.withUTF8Validator
         );
      }
   }
}
