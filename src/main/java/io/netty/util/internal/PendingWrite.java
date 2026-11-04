package io.netty.util.internal;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;

public final class PendingWrite {
   private static final ObjectPool<PendingWrite> RECYCLER = ObjectPool.newPool(new ObjectPool.ObjectCreator<PendingWrite>() {
      public PendingWrite newObject(ObjectPool.Handle<PendingWrite> var1) {
         return new PendingWrite(var1);
      }
   });
   private final ObjectPool.Handle<PendingWrite> handle;
   private Object msg;
   private Promise<Void> promise;

   public static PendingWrite newInstance(Object var0, Promise<Void> var1) {
      PendingWrite var2 = RECYCLER.get();
      var2.msg = var0;
      var2.promise = var1;
      return var2;
   }

   private PendingWrite(ObjectPool.Handle<PendingWrite> var1) {
      this.handle = var1;
   }

   public boolean recycle() {
      this.msg = null;
      this.promise = null;
      this.handle.recycle(this);
      return true;
   }

   public boolean failAndRecycle(Throwable var1) {
      ReferenceCountUtil.release(this.msg);
      if (this.promise != null) {
         this.promise.setFailure(var1);
      }

      return this.recycle();
   }

   public boolean successAndRecycle() {
      if (this.promise != null) {
         this.promise.setSuccess(null);
      }

      return this.recycle();
   }

   public Object msg() {
      return this.msg;
   }

   public Promise<Void> promise() {
      return this.promise;
   }

   public Promise<Void> recycleAndGet() {
      Promise var1 = this.promise;
      this.recycle();
      return var1;
   }
}
