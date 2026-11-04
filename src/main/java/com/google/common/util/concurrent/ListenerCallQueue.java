package com.google.common.util.concurrent;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class ListenerCallQueue<L> {
   private static final Logger logger = Logger.getLogger(ListenerCallQueue.class.getName());
   private final List<ListenerCallQueue.PerListenerQueue<L>> listeners = Collections.synchronizedList(new ArrayList<>());

   public void addListener(L var1, Executor var2) {
      Preconditions.checkNotNull(var1, "listener");
      Preconditions.checkNotNull(var2, "executor");
      this.listeners.add(new ListenerCallQueue.PerListenerQueue<>((L)var1, var2));
   }

   public void enqueue(ListenerCallQueue.Event<L> var1) {
      this.enqueueHelper(var1, var1);
   }

   public void enqueue(ListenerCallQueue.Event<L> var1, String var2) {
      this.enqueueHelper(var1, var2);
   }

   private void enqueueHelper(ListenerCallQueue.Event<L> var1, Object var2) {
      Preconditions.checkNotNull(var1, "event");
      Preconditions.checkNotNull(var2, "label");
      synchronized (this.listeners) {
         for (ListenerCallQueue.PerListenerQueue var5 : this.listeners) {
            var5.add(var1, var2);
         }
      }
   }

   public void dispatch() {
      for (int var1 = 0; var1 < this.listeners.size(); var1++) {
         this.listeners.get(var1).dispatch();
      }
   }

   interface Event<L> {
      void call(L var1);
   }

   private static final class PerListenerQueue<L> implements Runnable {
      final L listener;
      final Executor executor;
      @GuardedBy("this")
      final Queue<ListenerCallQueue.Event<L>> waitQueue = Queues.newArrayDeque();
      @GuardedBy("this")
      final Queue<Object> labelQueue = Queues.newArrayDeque();
      @GuardedBy("this")
      boolean isThreadScheduled;

      PerListenerQueue(L var1, Executor var2) {
         this.listener = Preconditions.checkNotNull((L)var1);
         this.executor = Preconditions.checkNotNull(var2);
      }

      synchronized void add(ListenerCallQueue.Event<L> var1, Object var2) {
         this.waitQueue.add(var1);
         this.labelQueue.add(var2);
      }

      void dispatch() {
         boolean var1 = false;
         synchronized (this) {
            if (!this.isThreadScheduled) {
               this.isThreadScheduled = true;
               var1 = true;
            }
         }

         if (var1) {
            try {
               this.executor.execute(this);
            } catch (RuntimeException var6) {
               synchronized (this) {
                  this.isThreadScheduled = false;
               }

               Logger var10000 = ListenerCallQueue.logger;
               Level var10001 = Level.SEVERE;
               String var3 = String.valueOf(this.listener);
               String var4 = String.valueOf(this.executor);
               var10000.log(
                  var10001,
                  new StringBuilder(42 + String.valueOf(var3).length() + String.valueOf(var4).length())
                     .append("Exception while running callbacks for ")
                     .append(var3)
                     .append(" on ")
                     .append(var4)
                     .toString(),
                  (Throwable)var6
               );
               throw var6;
            }
         }
      }

      @Override
      public void run() {
         boolean var1 = true;

         try {
            while (true) {
               ListenerCallQueue.Event var2;
               Object var3;
               synchronized (this) {
                  Preconditions.checkState(this.isThreadScheduled);
                  var2 = this.waitQueue.poll();
                  var3 = this.labelQueue.poll();
                  if (var2 == null) {
                     this.isThreadScheduled = false;
                     var1 = false;
                     return;
                  }
               }

               try {
                  var2.call(this.listener);
               } catch (RuntimeException var17) {
                  Logger var10000 = ListenerCallQueue.logger;
                  Level var10001 = Level.SEVERE;
                  String var5 = String.valueOf(this.listener);
                  String var6 = String.valueOf(var3);
                  var10000.log(
                     var10001,
                     new StringBuilder(37 + String.valueOf(var5).length() + String.valueOf(var6).length())
                        .append("Exception while executing callback: ")
                        .append(var5)
                        .append(" ")
                        .append(var6)
                        .toString(),
                     (Throwable)var17
                  );
               }
            }
         } finally {
            if (var1) {
               synchronized (this) {
                  this.isThreadScheduled = false;
               }
            }
         }
      }
   }
}
