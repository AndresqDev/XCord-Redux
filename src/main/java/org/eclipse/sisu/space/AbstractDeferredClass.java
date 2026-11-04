package org.eclipse.sisu.space;

import com.google.inject.Injector;
import org.eclipse.sisu.inject.DeferredClass;
import org.eclipse.sisu.inject.DeferredProvider;
import org.eclipse.sisu.inject.Logs;
import xcord.Inject;

abstract class AbstractDeferredClass<T> implements DeferredClass<T>, DeferredProvider<T> {
   @Inject
   private Injector injector;

   @Override
   public final DeferredProvider<T> asProvider() {
      return this;
   }

   public final T get() {
      try {
         return (T)this.injector.getInstance(this.load());
      } catch (Throwable var6) {
         Throwable var1 = var6;
         Logs.catchThrowable(var6);

         try {
            Logs.warn("Error injecting: {}", this.getName(), var1);
         } finally {
            Logs.throwUnchecked(var6);
         }

         return null;
      }
   }

   @Override
   public final DeferredClass<T> getImplementationClass() {
      return this;
   }
}
