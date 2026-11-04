package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicBoolean;

public final class WorkProcessor<T> implements EventProcessor {
   private final AtomicBoolean running = new AtomicBoolean(false);
   private final Sequence sequence = new Sequence(-1L);
   private final RingBuffer<T> ringBuffer;
   private final SequenceBarrier sequenceBarrier;
   private final WorkHandler<? super T> workHandler;
   private final ExceptionHandler<? super T> exceptionHandler;
   private final Sequence workSequence;
   private final EventReleaser eventReleaser = new EventReleaser() {
      @Override
      public void release() {
         WorkProcessor.this.sequence.set(Long.MAX_VALUE);
      }
   };
   private final TimeoutHandler timeoutHandler;

   public WorkProcessor(RingBuffer<T> var1, SequenceBarrier var2, WorkHandler<? super T> var3, ExceptionHandler<? super T> var4, Sequence var5) {
      this.ringBuffer = var1;
      this.sequenceBarrier = var2;
      this.workHandler = var3;
      this.exceptionHandler = var4;
      this.workSequence = var5;
      if (this.workHandler instanceof EventReleaseAware) {
         ((EventReleaseAware)this.workHandler).setEventReleaser(this.eventReleaser);
      }

      this.timeoutHandler = var3 instanceof TimeoutHandler ? (TimeoutHandler)var3 : null;
   }

   @Override
   public Sequence getSequence() {
      return this.sequence;
   }

   @Override
   public void halt() {
      this.running.set(false);
      this.sequenceBarrier.alert();
   }

   @Override
   public boolean isRunning() {
      return this.running.get();
   }

   @Override
   public void run() {
      if (!this.running.compareAndSet(false, true)) {
         throw new IllegalStateException("Thread is already running");
      } else {
         this.sequenceBarrier.clearAlert();
         this.notifyStart();
         boolean var1 = true;
         long var2 = Long.MIN_VALUE;
         long var4 = this.sequence.get();
         Object var6 = null;

         while (true) {
            try {
               if (var1) {
                  var1 = false;

                  do {
                     var4 = this.workSequence.get() + 1L;
                     this.sequence.set(var4 - 1L);
                  } while (!this.workSequence.compareAndSet(var4 - 1L, var4));
               }

               if (var2 >= var4) {
                  var6 = this.ringBuffer.get(var4);
                  this.workHandler.onEvent((T)var6);
                  var1 = true;
               } else {
                  var2 = this.sequenceBarrier.waitFor(var4);
               }
            } catch (TimeoutException var8) {
               this.notifyTimeout(this.sequence.get());
            } catch (AlertException var9) {
               if (!this.running.get()) {
                  this.notifyShutdown();
                  this.running.set(false);
                  return;
               }
            } catch (Throwable var10) {
               this.exceptionHandler.handleEventException(var10, var4, (T)var6);
               var1 = true;
            }
         }
      }
   }

   private void notifyTimeout(long var1) {
      try {
         if (this.timeoutHandler != null) {
            this.timeoutHandler.onTimeout(var1);
         }
      } catch (Throwable var4) {
         this.exceptionHandler.handleEventException(var4, var1, null);
      }
   }

   private void notifyStart() {
      if (this.workHandler instanceof LifecycleAware) {
         try {
            ((LifecycleAware)this.workHandler).onStart();
         } catch (Throwable var2) {
            this.exceptionHandler.handleOnStartException(var2);
         }
      }
   }

   private void notifyShutdown() {
      if (this.workHandler instanceof LifecycleAware) {
         try {
            ((LifecycleAware)this.workHandler).onShutdown();
         } catch (Throwable var2) {
            this.exceptionHandler.handleOnShutdownException(var2);
         }
      }
   }
}
