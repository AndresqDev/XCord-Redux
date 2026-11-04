package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.Executor;

@ElementTypesAreNonnullByDefault
@GwtCompatible
enum DirectExecutor implements Executor {
   INSTANCE;

   @Override
   public void execute(Runnable var1) {
      var1.run();
   }

   @Override
   public String toString() {
      return "MoreExecutors.directExecutor()";
   }
}
