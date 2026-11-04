package io.netty.buffer;

import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf {
   private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCountedByteBuf.class, "refCnt");
   private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      AbstractReferenceCountedByteBuf.class, "refCnt"
   );
   private static final ReferenceCountUpdater<AbstractReferenceCountedByteBuf> updater = new ReferenceCountUpdater<AbstractReferenceCountedByteBuf>() {
      @Override
      protected AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> updater() {
         return AbstractReferenceCountedByteBuf.AIF_UPDATER;
      }

      @Override
      protected long unsafeOffset() {
         return AbstractReferenceCountedByteBuf.REFCNT_FIELD_OFFSET;
      }
   };
   private volatile int refCnt;

   protected AbstractReferenceCountedByteBuf(int var1) {
      super(var1);
      updater.setInitialValue(this);
   }

   @Override
   boolean isAccessible() {
      return updater.isLiveNonVolatile(this);
   }

   @Override
   public int refCnt() {
      return updater.refCnt(this);
   }

   protected final void setRefCnt(int var1) {
      updater.setRefCnt(this, var1);
   }

   protected final void resetRefCnt() {
      updater.resetRefCnt(this);
   }

   @Override
   public ByteBuf retain() {
      return updater.retain(this);
   }

   @Override
   public ByteBuf retain(int var1) {
      return updater.retain(this, var1);
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
      return this.handleRelease(updater.release(this));
   }

   @Override
   public boolean release(int var1) {
      return this.handleRelease(updater.release(this, var1));
   }

   private boolean handleRelease(boolean var1) {
      if (var1) {
         this.deallocate();
      }

      return var1;
   }

   protected abstract void deallocate();
}
