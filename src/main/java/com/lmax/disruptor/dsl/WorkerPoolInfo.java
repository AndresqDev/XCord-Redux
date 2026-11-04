package com.lmax.disruptor.dsl;

import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import java.util.concurrent.Executor;

class WorkerPoolInfo<T> implements ConsumerInfo {
   private final WorkerPool<T> workerPool;
   private final SequenceBarrier sequenceBarrier;
   private boolean endOfChain = true;

   WorkerPoolInfo(WorkerPool<T> var1, SequenceBarrier var2) {
      this.workerPool = var1;
      this.sequenceBarrier = var2;
   }

   @Override
   public Sequence[] getSequences() {
      return this.workerPool.getWorkerSequences();
   }

   @Override
   public SequenceBarrier getBarrier() {
      return this.sequenceBarrier;
   }

   @Override
   public boolean isEndOfChain() {
      return this.endOfChain;
   }

   @Override
   public void start(Executor var1) {
      this.workerPool.start(var1);
   }

   @Override
   public void halt() {
      this.workerPool.halt();
   }

   @Override
   public void markAsUsedInBarrier() {
      this.endOfChain = false;
   }

   @Override
   public boolean isRunning() {
      return this.workerPool.isRunning();
   }
}
