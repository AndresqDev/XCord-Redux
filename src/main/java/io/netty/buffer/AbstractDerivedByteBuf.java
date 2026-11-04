package io.netty.buffer;

import java.nio.ByteBuffer;

@Deprecated
public abstract class AbstractDerivedByteBuf extends AbstractByteBuf {
   protected AbstractDerivedByteBuf(int var1) {
      super(var1);
   }

   @Override
   final boolean isAccessible() {
      return this.isAccessible0();
   }

   boolean isAccessible0() {
      return this.unwrap().isAccessible();
   }

   @Override
   public final int refCnt() {
      return this.refCnt0();
   }

   int refCnt0() {
      return this.unwrap().refCnt();
   }

   @Override
   public final ByteBuf retain() {
      return this.retain0();
   }

   ByteBuf retain0() {
      this.unwrap().retain();
      return this;
   }

   @Override
   public final ByteBuf retain(int var1) {
      return this.retain0(var1);
   }

   ByteBuf retain0(int var1) {
      this.unwrap().retain(var1);
      return this;
   }

   @Override
   public final ByteBuf touch() {
      return this.touch0();
   }

   ByteBuf touch0() {
      this.unwrap().touch();
      return this;
   }

   @Override
   public final ByteBuf touch(Object var1) {
      return this.touch0(var1);
   }

   ByteBuf touch0(Object var1) {
      this.unwrap().touch(var1);
      return this;
   }

   @Override
   public final boolean release() {
      return this.release0();
   }

   boolean release0() {
      return this.unwrap().release();
   }

   @Override
   public final boolean release(int var1) {
      return this.release0(var1);
   }

   boolean release0(int var1) {
      return this.unwrap().release(var1);
   }

   @Override
   public boolean isReadOnly() {
      return this.unwrap().isReadOnly();
   }

   @Override
   public ByteBuffer internalNioBuffer(int var1, int var2) {
      return this.nioBuffer(var1, var2);
   }

   @Override
   public ByteBuffer nioBuffer(int var1, int var2) {
      return this.unwrap().nioBuffer(var1, var2);
   }

   @Override
   public boolean isContiguous() {
      return this.unwrap().isContiguous();
   }
}
