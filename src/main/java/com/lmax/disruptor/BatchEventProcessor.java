package com.lmax.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

public final class BatchEventProcessor<T> implements EventProcessor {
   private static final int IDLE = 0;
   private static final int HALTED = 1;
   private static final int RUNNING = 2;
   private final AtomicInteger running = new AtomicInteger(0);
   private ExceptionHandler<? super T> exceptionHandler = new FatalExceptionHandler();
   private final DataProvider<T> dataProvider;
   private final SequenceBarrier sequenceBarrier;
   private final EventHandler<? super T> eventHandler;
   private final Sequence sequence = new Sequence(-1L);
   private final TimeoutHandler timeoutHandler;
   private final BatchStartAware batchStartAware;

   public BatchEventProcessor(DataProvider<T> var1, SequenceBarrier var2, EventHandler<? super T> var3) {
      this.dataProvider = var1;
      this.sequenceBarrier = var2;
      this.eventHandler = var3;
      if (var3 instanceof SequenceReportingEventHandler) {
         ((SequenceReportingEventHandler)var3).setSequenceCallback(this.sequence);
      }

      this.batchStartAware = var3 instanceof BatchStartAware ? (BatchStartAware)var3 : null;
      this.timeoutHandler = var3 instanceof TimeoutHandler ? (TimeoutHandler)var3 : null;
   }

   @Override
   public Sequence getSequence() {
      return this.sequence;
   }

   @Override
   public void halt() {
      this.running.set(1);
      this.sequenceBarrier.alert();
   }

   @Override
   public boolean isRunning() {
      return this.running.get() != 0;
   }

   public void setExceptionHandler(ExceptionHandler<? super T> var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         this.exceptionHandler = var1;
      }
   }

   @Override
   public void run() {
      if (this.running.compareAndSet(0, 2)) {
         this.sequenceBarrier.clearAlert();
         this.notifyStart();

         try {
            if (this.running.get() == 2) {
               this.processEvents();
            }
         } finally {
            this.notifyShutdown();
            this.running.set(0);
         }
      } else {
         if (this.running.get() == 2) {
            throw new IllegalStateException("Thread is already running");
         }

         this.earlyExit();
      }
   }

   private void processEvents() {
      Object var1 = null;
      long var2 = this.sequence.get() + 1L;

      while (true) {
         try {
            long var4 = this.sequenceBarrier.waitFor(var2);
            if (this.batchStartAware != null) {
               this.batchStartAware.onBatchStart(var4 - var2 + 1L);
            }

            while (var2 <= var4) {
               var1 = this.dataProvider.get(var2);
               this.eventHandler.onEvent((T)var1, var2, var2 == var4);
               var2++;
            }

            this.sequence.set(var4);
         } catch (TimeoutException var6) {
            this.notifyTimeout(this.sequence.get());
         } catch (AlertException var7) {
            if (this.running.get() != 2) {
               return;
            }
         } catch (Throwable var8) {
            this.exceptionHandler.handleEventException(var8, var2, (T)var1);
            this.sequence.set(var2);
            var2++;
         }
      }
   }

   private void earlyExit() {
      this.notifyStart();
      this.notifyShutdown();
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
      if (this.eventHandler instanceof LifecycleAware) {
         try {
            ((LifecycleAware)this.eventHandler).onStart();
         } catch (Throwable var2) {
            this.exceptionHandler.handleOnStartException(var2);
         }
      }
   }

   private void notifyShutdown() {
      if (this.eventHandler instanceof LifecycleAware) {
         try {
            ((LifecycleAware)this.eventHandler).onShutdown();
         } catch (Throwable var2) {
            this.exceptionHandler.handleOnShutdownException(var2);
         }
      }
   }
}
