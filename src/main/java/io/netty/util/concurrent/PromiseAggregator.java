package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.LinkedHashSet;
import java.util.Set;

@Deprecated
public class PromiseAggregator<V, F extends Future<V>> implements GenericFutureListener<F> {
   private final Promise<?> aggregatePromise;
   private final boolean failPending;
   private Set<Promise<V>> pendingPromises;

   public PromiseAggregator(Promise<Void> var1, boolean var2) {
      this.aggregatePromise = ObjectUtil.checkNotNull(var1, "aggregatePromise");
      this.failPending = var2;
   }

   public PromiseAggregator(Promise<Void> var1) {
      this(var1, true);
   }

   @SafeVarargs
   public final PromiseAggregator<V, F> add(Promise<V>... var1) {
      ObjectUtil.checkNotNull(var1, "promises");
      if (var1.length == 0) {
         return this;
      } else {
         synchronized (this) {
            if (this.pendingPromises == null) {
               int var3;
               if (var1.length > 1) {
                  var3 = var1.length;
               } else {
                  var3 = 2;
               }

               this.pendingPromises = new LinkedHashSet<>(var3);
            }

            for (Promise var6 : var1) {
               if (var6 != null) {
                  this.pendingPromises.add(var6);
                  var6.addListener(this);
               }
            }

            return this;
         }
      }
   }

   @Override
   public synchronized void operationComplete(F var1) throws Exception {
      if (this.pendingPromises == null) {
         this.aggregatePromise.setSuccess(null);
      } else {
         this.pendingPromises.remove(var1);
         if (!var1.isSuccess()) {
            Throwable var2 = var1.cause();
            this.aggregatePromise.setFailure(var2);
            if (this.failPending) {
               for (Promise var4 : this.pendingPromises) {
                  var4.setFailure(var2);
               }
            }
         } else if (this.pendingPromises.isEmpty()) {
            this.aggregatePromise.setSuccess(null);
         }
      }
   }
}
