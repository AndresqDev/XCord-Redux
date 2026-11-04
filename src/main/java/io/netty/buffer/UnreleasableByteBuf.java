package io.netty.buffer;

import io.netty.util.internal.ObjectUtil;
import java.nio.ByteOrder;

final class UnreleasableByteBuf extends WrappedByteBuf {
   private SwappedByteBuf swappedBuf;

   UnreleasableByteBuf(ByteBuf var1) {
      super(var1 instanceof UnreleasableByteBuf ? var1.unwrap() : var1);
   }

   @Override
   public ByteBuf order(ByteOrder var1) {
      if (ObjectUtil.checkNotNull(var1, "endianness") == this.order()) {
         return this;
      } else {
         SwappedByteBuf var2 = this.swappedBuf;
         if (var2 == null) {
            this.swappedBuf = var2 = new SwappedByteBuf(this);
         }

         return var2;
      }
   }

   @Override
   public ByteBuf asReadOnly() {
      return this.buf.isReadOnly() ? this : new UnreleasableByteBuf(this.buf.asReadOnly());
   }

   @Override
   public ByteBuf readSlice(int var1) {
      return new UnreleasableByteBuf(this.buf.readSlice(var1));
   }

   @Override
   public ByteBuf readRetainedSlice(int var1) {
      return this.readSlice(var1);
   }

   @Override
   public ByteBuf slice() {
      return new UnreleasableByteBuf(this.buf.slice());
   }

   @Override
   public ByteBuf retainedSlice() {
      return this.slice();
   }

   @Override
   public ByteBuf slice(int var1, int var2) {
      return new UnreleasableByteBuf(this.buf.slice(var1, var2));
   }

   @Override
   public ByteBuf retainedSlice(int var1, int var2) {
      return this.slice(var1, var2);
   }

   @Override
   public ByteBuf duplicate() {
      return new UnreleasableByteBuf(this.buf.duplicate());
   }

   @Override
   public ByteBuf retainedDuplicate() {
      return this.duplicate();
   }

   @Override
   public ByteBuf retain(int var1) {
      return this;
   }

   @Override
   public ByteBuf retain() {
      return this;
   }

   @Override
   public ByteBuf touch() {
      return this;
   }

   @Override
   public ByteBuf touch(Object var1) {
      return this;
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
