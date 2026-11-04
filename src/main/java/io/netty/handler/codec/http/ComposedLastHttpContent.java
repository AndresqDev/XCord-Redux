package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

final class ComposedLastHttpContent implements LastHttpContent {
   private final HttpHeaders trailingHeaders;
   private DecoderResult result;

   ComposedLastHttpContent(HttpHeaders var1) {
      this.trailingHeaders = var1;
   }

   ComposedLastHttpContent(HttpHeaders var1, DecoderResult var2) {
      this(var1);
      this.result = var2;
   }

   @Override
   public HttpHeaders trailingHeaders() {
      return this.trailingHeaders;
   }

   @Override
   public LastHttpContent copy() {
      DefaultLastHttpContent var1 = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
      var1.trailingHeaders().set(this.trailingHeaders());
      return var1;
   }

   @Override
   public LastHttpContent duplicate() {
      return this.copy();
   }

   @Override
   public LastHttpContent retainedDuplicate() {
      return this.copy();
   }

   @Override
   public LastHttpContent replace(ByteBuf var1) {
      DefaultLastHttpContent var2 = new DefaultLastHttpContent(var1);
      var2.trailingHeaders().setAll(this.trailingHeaders());
      return var2;
   }

   @Override
   public LastHttpContent retain(int var1) {
      return this;
   }

   @Override
   public LastHttpContent retain() {
      return this;
   }

   @Override
   public LastHttpContent touch() {
      return this;
   }

   @Override
   public LastHttpContent touch(Object var1) {
      return this;
   }

   @Override
   public ByteBuf content() {
      return Unpooled.EMPTY_BUFFER;
   }

   @Override
   public DecoderResult decoderResult() {
      return this.result;
   }

   @Override
   public DecoderResult getDecoderResult() {
      return this.decoderResult();
   }

   @Override
   public void setDecoderResult(DecoderResult var1) {
      this.result = var1;
   }

   @Override
   public int refCnt() {
      return 1;
   }

   @Override
   public boolean release() {
      return false;
   }

   @Override
   public boolean release(int var1) {
      return false;
   }
}
