package io.netty.util;

import io.netty.util.internal.ReferenceCountUpdater;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class AbstractReferenceCounted implements ReferenceCounted {
   private static final long REFCNT_FIELD_OFFSET = ReferenceCountUpdater.getUnsafeOffset(AbstractReferenceCounted.class, "refCnt");
   private static final AtomicIntegerFieldUpdater<AbstractReferenceCounted> AIF_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      AbstractReferenceCounted.class, "refCnt"
   );
   private static final ReferenceCountUpdater<AbstractReferenceCounted> updater = new ReferenceCountUpdater<AbstractReferenceCounted>() {
      @Override
      protected AtomicIntegerFieldUpdater<AbstractReferenceCounted> updater() {
         return AbstractReferenceCounted.AIF_UPDATER;
      }

      @Override
      protected long unsafeOffset() {
         return AbstractReferenceCounted.REFCNT_FIELD_OFFSET;
      }
   };
   private volatile int refCnt = updater.initialValue();

   @Override
   public int refCnt() {
      return updater.refCnt(this);
   }

   protected final void setRefCnt(int var1) {
      updater.setRefCnt(this, var1);
   }

   @Override
   public ReferenceCounted retain() {
      return updater.retain(this);
   }

   @Override
   public ReferenceCounted retain(int var1) {
      return updater.retain(this, var1);
   }

   @Override
   public ReferenceCounted touch() {
      return this.touch(null);
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
