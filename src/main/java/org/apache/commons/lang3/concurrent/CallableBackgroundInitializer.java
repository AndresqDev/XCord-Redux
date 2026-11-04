package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;

public class CallableBackgroundInitializer<T> extends BackgroundInitializer<T> {
   private final Callable<T> callable;

   public CallableBackgroundInitializer(Callable<T> var1) {
      this.checkCallable(var1);
      this.callable = var1;
   }

   public CallableBackgroundInitializer(Callable<T> var1, ExecutorService var2) {
      super(var2);
      this.checkCallable(var1);
      this.callable = var1;
   }

   @Override
   protected T initialize() throws Exception {
      return this.callable.call();
   }

   private void checkCallable(Callable<T> var1) {
      Validate.isTrue(var1 != null, "Callable must not be null!");
   }
}
