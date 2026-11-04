package com.lmax.disruptor;

import com.lmax.disruptor.util.Util;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WorkerPool<T> {
   private final AtomicBoolean started = new AtomicBoolean(false);
   private final Sequence workSequence = new Sequence(-1L);
   private final RingBuffer<T> ringBuffer;
   private final WorkProcessor<?>[] workProcessors;

   @SafeVarargs
   public WorkerPool(RingBuffer<T> var1, SequenceBarrier var2, ExceptionHandler<? super T> var3, WorkHandler<? super T>... var4) {
      this.ringBuffer = var1;
      int var5 = var4.length;
      this.workProcessors = new WorkProcessor[var5];

      for (int var6 = 0; var6 < var5; var6++) {
         this.workProcessors[var6] = new WorkProcessor(var1, var2, var4[var6], var3, this.workSequence);
      }
   }

   @SafeVarargs
   public WorkerPool(EventFactory<T> var1, ExceptionHandler<? super T> var2, WorkHandler<? super T>... var3) {
      this.ringBuffer = RingBuffer.createMultiProducer(var1, 1024, new BlockingWaitStrategy());
      SequenceBarrier var4 = this.ringBuffer.newBarrier();
      int var5 = var3.length;
      this.workProcessors = new WorkProcessor[var5];

      for (int var6 = 0; var6 < var5; var6++) {
         this.workProcessors[var6] = new WorkProcessor<>(this.ringBuffer, var4, var3[var6], var2, this.workSequence);
      }

      this.ringBuffer.addGatingSequences(this.getWorkerSequences());
   }

   public Sequence[] getWorkerSequences() {
      Sequence[] var1 = new Sequence[this.workProcessors.length + 1];
      int var2 = 0;

      for (int var3 = this.workProcessors.length; var2 < var3; var2++) {
         var1[var2] = this.workProcessors[var2].getSequence();
      }

      var1[var1.length - 1] = this.workSequence;
      return var1;
   }

   public RingBuffer<T> start(Executor var1) {
      if (!this.started.compareAndSet(false, true)) {
         throw new IllegalStateException("WorkerPool has already been started and cannot be restarted until halted.");
      } else {
         long var2 = this.ringBuffer.getCursor();
         this.workSequence.set(var2);

         for (WorkProcessor var7 : this.workProcessors) {
            var7.getSequence().set(var2);
            var1.execute(var7);
         }

         return this.ringBuffer;
      }
   }

   public void drainAndHalt() {
      Sequence[] var1 = this.getWorkerSequences();

      while (this.ringBuffer.getCursor() > Util.getMinimumSequence(var1)) {
         Thread.yield();
      }

      for (WorkProcessor var5 : this.workProcessors) {
         var5.halt();
      }

      this.started.set(false);
   }

   public void halt() {
      for (WorkProcessor var4 : this.workProcessors) {
         var4.halt();
      }

      this.started.set(false);
   }

   public boolean isRunning() {
      return this.started.get();
   }
}
