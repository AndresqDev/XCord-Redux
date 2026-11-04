package org.apache.commons.lang3.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer<I, O> implements Computable<I, O> {
   private final ConcurrentMap<I, Future<O>> cache = new ConcurrentHashMap<>();
   private final Computable<I, O> computable;
   private final boolean recalculate;

   public Memoizer(Computable<I, O> var1) {
      this(var1, false);
   }

   public Memoizer(Computable<I, O> var1, boolean var2) {
      this.computable = var1;
      this.recalculate = var2;
   }

   @Override
   public O compute(final I var1) throws InterruptedException {
      while (true) {
         Object var2 = this.cache.get(var1);
         if (var2 == null) {
            Callable var3 = new Callable<O>() {
               @Override
               public O call() throws InterruptedException {
                  return Memoizer.this.computable.compute((I)var1);
               }
            };
            FutureTask var4 = new FutureTask(var3);
            var2 = this.cache.putIfAbsent((I)var1, var4);
            if (var2 == null) {
               var2 = var4;
               var4.run();
            }
         }

         try {
            return (O)var2.get();
         } catch (CancellationException var5) {
            this.cache.remove(var1, var2);
         } catch (ExecutionException var6) {
            if (this.recalculate) {
               this.cache.remove(var1, var2);
            }

            throw this.launderException(var6.getCause());
         }
      }
   }

   private RuntimeException launderException(Throwable var1) {
      if (var1 instanceof RuntimeException) {
         return (RuntimeException)var1;
      } else if (var1 instanceof Error) {
         throw (Error)var1;
      } else {
         throw new IllegalStateException("Unchecked exception", var1);
      }
   }
}
