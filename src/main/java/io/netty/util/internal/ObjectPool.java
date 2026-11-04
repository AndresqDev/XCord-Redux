package io.netty.util.internal;

import io.netty.util.Recycler;

public abstract class ObjectPool<T> {
   ObjectPool() {
   }

   public abstract T get();

   public static <T> ObjectPool<T> newPool(ObjectPool.ObjectCreator<T> var0) {
      return new ObjectPool.RecyclerObjectPool<>(ObjectUtil.checkNotNull(var0, "creator"));
   }

   public interface Handle<T> {
      void recycle(T var1);
   }

   public interface ObjectCreator<T> {
      T newObject(ObjectPool.Handle<T> var1);
   }

   private static final class RecyclerObjectPool<T> extends ObjectPool<T> {
      private final Recycler<T> recycler;

      RecyclerObjectPool(final ObjectPool.ObjectCreator<T> var1) {
         this.recycler = new Recycler<T>() {
            @Override
            protected T newObject(Recycler.Handle<T> var1x) {
               return var1.newObject(var1x);
            }
         };
      }

      @Override
      public T get() {
         return this.recycler.get();
      }
   }
}
