package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class PromiseNotifier<V, F extends Future<V>> implements GenericFutureListener<F> {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PromiseNotifier.class);
   private final Promise<? super V>[] promises;
   private final boolean logNotifyFailure;

   @SafeVarargs
   public PromiseNotifier(Promise<? super V>... var1) {
      this(true, var1);
   }

   @SafeVarargs
   public PromiseNotifier(boolean var1, Promise<? super V>... var2) {
      ObjectUtil.checkNotNull(var2, "promises");

      for (Promise var6 : var2) {
         ObjectUtil.checkNotNullWithIAE(var6, "promise");
      }

      this.promises = (Promise<? super V>[])var2.clone();
      this.logNotifyFailure = var1;
   }

   public static <V, F extends Future<V>> F cascade(F var0, Promise<? super V> var1) {
      return cascade(true, (F)var0, var1);
   }

   public static <V, F extends Future<V>> F cascade(boolean var0, final F var1, final Promise<? super V> var2) {
      var2.addListener(new FutureListener() {
         @Override
         public void operationComplete(Future var1x) {
            if (var1x.isCancelled()) {
               var1.cancel(false);
            }
         }
      });
      var1.addListener(new PromiseNotifier(var0, new Promise[]{var2}) {
         @Override
         public void operationComplete(Future var1x) throws Exception {
            if (!var2.isCancelled() || !var1x.isCancelled()) {
               super.operationComplete((F)var1);
            }
         }
      });
      return (F)var1;
   }

   @Override
   public void operationComplete(F var1) throws Exception {
      InternalLogger var2 = this.logNotifyFailure ? logger : null;
      if (var1.isSuccess()) {
         Object var3 = var1.get();

         for (Promise var7 : this.promises) {
            PromiseNotificationUtil.trySuccess(var7, var3, var2);
         }
      } else if (var1.isCancelled()) {
         for (Promise var14 : this.promises) {
            PromiseNotificationUtil.tryCancel(var14, var2);
         }
      } else {
         Throwable var9 = var1.cause();

         for (Promise var16 : this.promises) {
            PromiseNotificationUtil.tryFailure(var16, var9, var2);
         }
      }
   }
}
