package com.google.common.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.j2objc.annotations.Weak;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
class Subscriber {
   @Weak
   private EventBus bus;
   @VisibleForTesting
   final Object target;
   private final Method method;
   private final Executor executor;

   static Subscriber create(EventBus var0, Object var1, Method var2) {
      return (Subscriber)(isDeclaredThreadSafe(var2) ? new Subscriber(var0, var1, var2) : new Subscriber.SynchronizedSubscriber(var0, var1, var2));
   }

   private Subscriber(EventBus var1, Object var2, Method var3) {
      this.bus = var1;
      this.target = Preconditions.checkNotNull(var2);
      this.method = var3;
      var3.setAccessible(true);
      this.executor = var1.executor();
   }

   final void dispatchEvent(final Object var1) {
      this.executor.execute(new Runnable() {
         @Override
         public void run() {
            try {
               Subscriber.this.invokeSubscriberMethod(var1);
            } catch (InvocationTargetException var2) {
               Subscriber.this.bus.handleSubscriberException(var2.getCause(), Subscriber.this.context(var1));
            }
         }
      });
   }

   @VisibleForTesting
   void invokeSubscriberMethod(Object var1) throws InvocationTargetException {
      try {
         this.method.invoke(this.target, Preconditions.checkNotNull(var1));
      } catch (IllegalArgumentException var4) {
         String var7 = String.valueOf(var1);
         throw new Error(new StringBuilder(33 + String.valueOf(var7).length()).append("Method rejected target/argument: ").append(var7).toString(), var4);
      } catch (IllegalAccessException var5) {
         String var3 = String.valueOf(var1);
         throw new Error(new StringBuilder(28 + String.valueOf(var3).length()).append("Method became inaccessible: ").append(var3).toString(), var5);
      } catch (InvocationTargetException var6) {
         if (var6.getCause() instanceof Error) {
            throw (Error)var6.getCause();
         } else {
            throw var6;
         }
      }
   }

   private SubscriberExceptionContext context(Object var1) {
      return new SubscriberExceptionContext(this.bus, var1, this.target, this.method);
   }

   @Override
   public final int hashCode() {
      return (31 + this.method.hashCode()) * 31 + System.identityHashCode(this.target);
   }

   @Override
   public final boolean equals(Object var1) {
      if (!(var1 instanceof Subscriber)) {
         return false;
      } else {
         Subscriber var2 = (Subscriber)var1;
         return this.target == var2.target && this.method.equals(var2.method);
      }
   }

   private static boolean isDeclaredThreadSafe(Method var0) {
      return var0.getAnnotation(AllowConcurrentEvents.class) != null;
   }

   @VisibleForTesting
   static final class SynchronizedSubscriber extends Subscriber {
      private SynchronizedSubscriber(EventBus var1, Object var2, Method var3) {
         super(var1, var2, var3);
      }

      @Override
      void invokeSubscriberMethod(Object var1) throws InvocationTargetException {
         synchronized (this) {
            super.invokeSubscriberMethod(var1);
         }
      }
   }
}
