package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.ObjectUtil;

public class DefaultFullHttpRequest extends DefaultHttpRequest implements FullHttpRequest {
   private final ByteBuf content;
   private final HttpHeaders trailingHeader;
   private int hash;

   public DefaultFullHttpRequest(HttpVersion var1, HttpMethod var2, String var3) {
      this(var1, var2, var3, Unpooled.buffer(0));
   }

   public DefaultFullHttpRequest(HttpVersion var1, HttpMethod var2, String var3, ByteBuf var4) {
      this(var1, var2, var3, var4, true);
   }

   public DefaultFullHttpRequest(HttpVersion var1, HttpMethod var2, String var3, boolean var4) {
      this(var1, var2, var3, Unpooled.buffer(0), var4);
   }

   public DefaultFullHttpRequest(HttpVersion var1, HttpMethod var2, String var3, ByteBuf var4, boolean var5) {
      super(var1, var2, var3, var5);
      this.content = ObjectUtil.checkNotNull(var4, "content");
      this.trailingHeader = new DefaultHttpHeaders(var5);
   }

   public DefaultFullHttpRequest(HttpVersion var1, HttpMethod var2, String var3, ByteBuf var4, HttpHeaders var5, HttpHeaders var6) {
      super(var1, var2, var3, var5);
      this.content = ObjectUtil.checkNotNull(var4, "content");
      this.trailingHeader = ObjectUtil.checkNotNull(var6, "trailingHeader");
   }

   @Override
   public HttpHeaders trailingHeaders() {
      return this.trailingHeader;
   }

   @Override
   public ByteBuf content() {
      return this.content;
   }

   @Override
   public int refCnt() {
      return this.content.refCnt();
   }

   @Override
   public FullHttpRequest retain() {
      this.content.retain();
      return this;
   }

   @Override
   public FullHttpRequest retain(int var1) {
      this.content.retain(var1);
      return this;
   }

   @Override
   public FullHttpRequest touch() {
      this.content.touch();
      return this;
   }

   @Override
   public FullHttpRequest touch(Object var1) {
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
   public FullHttpRequest setProtocolVersion(HttpVersion var1) {
      super.setProtocolVersion(var1);
      return this;
   }

   @Override
   public FullHttpRequest setMethod(HttpMethod var1) {
      super.setMethod(var1);
      return this;
   }

   @Override
   public FullHttpRequest setUri(String var1) {
      super.setUri(var1);
      return this;
   }

   @Override
   public FullHttpRequest copy() {
      return this.replace(this.content().copy());
   }

   @Override
   public FullHttpRequest duplicate() {
      return this.replace(this.content().duplicate());
   }

   @Override
   public FullHttpRequest retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   @Override
   public FullHttpRequest replace(ByteBuf var1) {
      DefaultFullHttpRequest var2 = new DefaultFullHttpRequest(
         this.protocolVersion(), this.method(), this.uri(), var1, this.headers().copy(), this.trailingHeaders().copy()
      );
      var2.setDecoderResult(this.decoderResult());
      return var2;
   }

   @Override
   public int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         if (ByteBufUtil.isAccessible(this.content())) {
            try {
               var1 = 31 + this.content().hashCode();
            } catch (IllegalReferenceCountException var3) {
               var1 = 31;
            }
         } else {
            var1 = 31;
         }

         int var5 = 31 * var1 + this.trailingHeaders().hashCode();
         var1 = 31 * var5 + super.hashCode();
         this.hash = var1;
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultFullHttpRequest)) {
         return false;
      } else {
         DefaultFullHttpRequest var2 = (DefaultFullHttpRequest)var1;
         return super.equals(var2) && this.content().equals(var2.content()) && this.trailingHeaders().equals(var2.trailingHeaders());
      }
   }

   @Override
   public String toString() {
      return HttpMessageUtil.appendFullRequest(new StringBuilder(256), this).toString();
   }
}
