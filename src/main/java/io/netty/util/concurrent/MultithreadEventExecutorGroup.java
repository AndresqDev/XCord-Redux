package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MultithreadEventExecutorGroup extends AbstractEventExecutorGroup {
   private final EventExecutor[] children;
   private final Set<EventExecutor> readonlyChildren;
   private final AtomicInteger terminatedChildren = new AtomicInteger();
   private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private final EventExecutorChooserFactory.EventExecutorChooser chooser;

   protected MultithreadEventExecutorGroup(int var1, ThreadFactory var2, Object... var3) {
      this(var1, var2 == null ? null : new ThreadPerTaskExecutor(var2), var3);
   }

   protected MultithreadEventExecutorGroup(int var1, Executor var2, Object... var3) {
      this(var1, var2, DefaultEventExecutorChooserFactory.INSTANCE, var3);
   }

   protected MultithreadEventExecutorGroup(int var1, Executor var2, EventExecutorChooserFactory var3, Object... var4) {
      ObjectUtil.checkPositive(var1, "nThreads");
      if (var2 == null) {
         var2 = new ThreadPerTaskExecutor(this.newDefaultThreadFactory());
      }

      this.children = new EventExecutor[var1];

      for (int var5 = 0; var5 < var1; var5++) {
         boolean var6 = false;
         boolean var18 = false /* VF: Semaphore variable */;

         try {
            var18 = true;
            this.children[var5] = this.newChild((Executor)var2, var4);
            var6 = true;
            var18 = false;
         } catch (Exception var19) {
            throw new IllegalStateException("failed to create a child event loop", var19);
         } finally {
            if (var18) {
               if (!var6) {
                  for (int var11 = 0; var11 < var5; var11++) {
                     this.children[var11].shutdownGracefully();
                  }

                  for (int var29 = 0; var29 < var5; var29++) {
                     EventExecutor var12 = this.children[var29];

                     try {
                        while (!var12.isTerminated()) {
                           var12.awaitTermination(2147483647L, TimeUnit.SECONDS);
                        }
                     } catch (InterruptedException var20) {
                        Thread.currentThread().interrupt();
                        break;
                     }
                  }
               }
            }
         }

         if (!var6) {
            for (int var7 = 0; var7 < var5; var7++) {
               this.children[var7].shutdownGracefully();
            }

            for (int var26 = 0; var26 < var5; var26++) {
               EventExecutor var8 = this.children[var26];

               try {
                  while (!var8.isTerminated()) {
                     var8.awaitTermination(2147483647L, TimeUnit.SECONDS);
                  }
               } catch (InterruptedException var22) {
                  Thread.currentThread().interrupt();
                  break;
               }
            }
         }
      }

      this.chooser = var3.newChooser(this.children);
      FutureListener var23 = new FutureListener<Object>() {
         @Override
         public void operationComplete(Future<Object> var1) throws Exception {
            if (MultithreadEventExecutorGroup.this.terminatedChildren.incrementAndGet() == MultithreadEventExecutorGroup.this.children.length) {
               MultithreadEventExecutorGroup.this.terminationFuture.setSuccess(null);
            }
         }
      };

      for (EventExecutor var9 : this.children) {
         var9.terminationFuture().addListener(var23);
      }

      LinkedHashSet var25 = new LinkedHashSet(this.children.length);
      Collections.addAll(var25, this.children);
      this.readonlyChildren = Collections.unmodifiableSet(var25);
   }

   protected ThreadFactory newDefaultThreadFactory() {
      return new DefaultThreadFactory(this.getClass());
   }

   @Override
   public EventExecutor next() {
      return this.chooser.next();
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return this.readonlyChildren.iterator();
   }

   public final int executorCount() {
      return this.children.length;
   }

   protected abstract EventExecutor newChild(Executor var1, Object... var2) throws Exception;

   @Override
   public Future<?> shutdownGracefully(long var1, long var3, TimeUnit var5) {
      for (EventExecutor var9 : this.children) {
         var9.shutdownGracefully(var1, var3, var5);
      }

      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
      for (EventExecutor var4 : this.children) {
         var4.shutdown();
      }
   }

   @Override
   public boolean isShuttingDown() {
      for (EventExecutor var4 : this.children) {
         if (!var4.isShuttingDown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isShutdown() {
      for (EventExecutor var4 : this.children) {
         if (!var4.isShutdown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isTerminated() {
      for (EventExecutor var4 : this.children) {
         if (!var4.isTerminated()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean awaitTermination(long var1, TimeUnit var3) throws InterruptedException {
      long var4 = System.nanoTime() + var3.toNanos(var1);

      long var10;
      for (EventExecutor var9 : this.children) {
         do {
            var10 = var4 - System.nanoTime();
            if (var10 <= 0L) {
               return this.isTerminated();
            }
         } while (var9.awaitTermination(var10, TimeUnit.NANOSECONDS));
      }

      return this.isTerminated();
   }
}
