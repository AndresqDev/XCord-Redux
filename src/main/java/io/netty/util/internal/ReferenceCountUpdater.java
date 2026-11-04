package io.netty.util.internal;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public abstract class ReferenceCountUpdater<T extends ReferenceCounted> {
   protected ReferenceCountUpdater() {
   }

   public static long getUnsafeOffset(Class<? extends ReferenceCounted> var0, String var1) {
      try {
         if (PlatformDependent.hasUnsafe()) {
            return PlatformDependent.objectFieldOffset(var0.getDeclaredField(var1));
         }
      } catch (Throwable var3) {
      }

      return -1L;
   }

   protected abstract AtomicIntegerFieldUpdater<T> updater();

   protected abstract long unsafeOffset();

   public final int initialValue() {
      return 2;
   }

   public void setInitialValue(T var1) {
      long var2 = this.unsafeOffset();
      if (var2 == -1L) {
         this.updater().set((T)var1, this.initialValue());
      } else {
         PlatformDependent.safeConstructPutInt(var1, var2, this.initialValue());
      }
   }

   private static int realRefCnt(int var0) {
      return var0 != 2 && var0 != 4 && (var0 & 1) != 0 ? 0 : var0 >>> 1;
   }

   private static int toLiveRealRefCnt(int var0, int var1) {
      if (var0 != 2 && var0 != 4 && (var0 & 1) != 0) {
         throw new IllegalReferenceCountException(0, -var1);
      } else {
         return var0 >>> 1;
      }
   }

   private int nonVolatileRawCnt(T var1) {
      long var2 = this.unsafeOffset();
      return var2 != -1L ? PlatformDependent.getInt(var1, var2) : this.updater().get((T)var1);
   }

   public final int refCnt(T var1) {
      return realRefCnt(this.updater().get((T)var1));
   }

   public final boolean isLiveNonVolatile(T var1) {
      long var2 = this.unsafeOffset();
      int var4 = var2 != -1L ? PlatformDependent.getInt(var1, var2) : this.updater().get((T)var1);
      return var4 == 2 || var4 == 4 || var4 == 6 || var4 == 8 || (var4 & 1) == 0;
   }

   public final void setRefCnt(T var1, int var2) {
      this.updater().set((T)var1, var2 > 0 ? var2 << 1 : 1);
   }

   public final void resetRefCnt(T var1) {
      this.updater().set((T)var1, this.initialValue());
   }

   public final T retain(T var1) {
      return this.retain0((T)var1, 1, 2);
   }

   public final T retain(T var1, int var2) {
      int var3 = ObjectUtil.checkPositive(var2, "increment") << 1;
      return this.retain0((T)var1, var2, var3);
   }

   private T retain0(T var1, int var2, int var3) {
      int var4 = this.updater().getAndAdd((T)var1, var3);
      if (var4 != 2 && var4 != 4 && (var4 & 1) != 0) {
         throw new IllegalReferenceCountException(0, var2);
      } else if ((var4 > 0 || var4 + var3 < 0) && (var4 < 0 || var4 + var3 >= var4)) {
         return (T)var1;
      } else {
         this.updater().getAndAdd((T)var1, -var3);
         throw new IllegalReferenceCountException(realRefCnt(var4), var2);
      }
   }

   public final boolean release(T var1) {
      int var2 = this.nonVolatileRawCnt((T)var1);
      return var2 == 2
         ? this.tryFinalRelease0((T)var1, 2) || this.retryRelease0((T)var1, 1)
         : this.nonFinalRelease0((T)var1, 1, var2, toLiveRealRefCnt(var2, 1));
   }

   public final boolean release(T var1, int var2) {
      int var3 = this.nonVolatileRawCnt((T)var1);
      int var4 = toLiveRealRefCnt(var3, ObjectUtil.checkPositive(var2, "decrement"));
      return var2 == var4 ? this.tryFinalRelease0((T)var1, var3) || this.retryRelease0((T)var1, var2) : this.nonFinalRelease0((T)var1, var2, var3, var4);
   }

   private boolean tryFinalRelease0(T var1, int var2) {
      return this.updater().compareAndSet((T)var1, var2, 1);
   }

   private boolean nonFinalRelease0(T var1, int var2, int var3, int var4) {
      return var2 < var4 && this.updater().compareAndSet((T)var1, var3, var3 - (var2 << 1)) ? false : this.retryRelease0((T)var1, var2);
   }

   private boolean retryRelease0(T var1, int var2) {
      while (true) {
         int var3 = this.updater().get((T)var1);
         int var4 = toLiveRealRefCnt(var3, var2);
         if (var2 == var4) {
            if (this.tryFinalRelease0((T)var1, var3)) {
               return true;
            }
         } else {
            if (var2 >= var4) {
               throw new IllegalReferenceCountException(var4, -var2);
            }

            if (this.updater().compareAndSet((T)var1, var3, var3 - (var2 << 1))) {
               return false;
            }
         }

         Thread.yield();
      }
   }
}
