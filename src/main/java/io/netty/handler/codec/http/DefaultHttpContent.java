package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultHttpContent extends DefaultHttpObject implements HttpContent {
   private final ByteBuf content;

   public DefaultHttpContent(ByteBuf var1) {
      this.content = ObjectUtil.checkNotNull(var1, "content");
   }

   @Override
   public ByteBuf content() {
      return this.content;
   }

   @Override
   public HttpContent copy() {
      return this.replace(this.content.copy());
   }

   @Override
   public HttpContent duplicate() {
      return this.replace(this.content.duplicate());
   }

   @Override
   public HttpContent retainedDuplicate() {
      return this.replace(this.content.retainedDuplicate());
   }

   @Override
   public HttpContent replace(ByteBuf var1) {
      return new DefaultHttpContent(var1);
   }

   @Override
   public int refCnt() {
      return this.content.refCnt();
   }

   @Override
   public HttpContent retain() {
      this.content.retain();
      return this;
   }

   @Override
   public HttpContent retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   @Override
   public HttpContent touch() {
      this.content.touch();
      return this;
   }

   @Override
   public HttpContent touch(Object var1) {
      this.content.touch(var1);
      return this;
   }

   @Override
   public boolean release() {
      return this.content.release();
   }

   @Override
   public boolean release(int var1) {
      return this.content.release(var1);
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(data: " + this.content() + ", decoderResult: " + this.decoderResult() + ')';
   }
}
