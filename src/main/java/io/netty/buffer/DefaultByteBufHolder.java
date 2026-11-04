package io.netty.buffer;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultByteBufHolder implements ByteBufHolder {
   private final ByteBuf data;

   public DefaultByteBufHolder(ByteBuf var1) {
      this.data = ObjectUtil.checkNotNull(var1, "data");
   }

   @Override
   public ByteBuf content() {
      return ByteBufUtil.ensureAccessible(this.data);
   }

   @Override
   public ByteBufHolder copy() {
      return this.replace(this.data.copy());
   }

   @Override
   public ByteBufHolder duplicate() {
      return this.replace(this.data.duplicate());
   }

   @Override
   public ByteBufHolder retainedDuplicate() {
      return this.replace(this.data.retainedDuplicate());
   }

   @Override
   public ByteBufHolder replace(ByteBuf var1) {
      return new DefaultByteBufHolder(var1);
   }

   @Override
   public int refCnt() {
      return this.data.refCnt();
   }

   @Override
   public ByteBufHolder retain() {
      this.data.retain();
      return this;
   }

   @Override
   public ByteBufHolder retain(int var1) {
      this.data.retain(var1);
      return this;
   }

   @Override
   public ByteBufHolder touch() {
      this.data.touch();
      return this;
   }

   @Override
   public ByteBufHolder touch(Object var1) {
      this.data.touch(var1);
      return this;
   }

   @Override
   public boolean release() {
      return this.data.release();
   }

   @Override
   public boolean release(int var1) {
      return this.data.release(var1);
   }

   protected final String contentToString() {
      return this.data.toString();
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + '(' + this.contentToString() + ')';
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? this.data.equals(((DefaultByteBufHolder)var1).data) : false;
      }
   }

   @Override
   public int hashCode() {
      return this.data.hashCode();
   }
}
