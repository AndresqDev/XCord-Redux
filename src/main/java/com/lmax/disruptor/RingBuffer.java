package com.lmax.disruptor;

import com.lmax.disruptor.dsl.ProducerType;

public final class RingBuffer<E> extends RingBufferFields<E> implements Cursored, EventSequencer<E>, EventSink<E> {
   public static final long INITIAL_CURSOR_VALUE = -1L;
   protected long p1;
   protected long p2;
   protected long p3;
   protected long p4;
   protected long p5;
   protected long p6;
   protected long p7;

   RingBuffer(EventFactory<E> var1, Sequencer var2) {
      super(var1, var2);
   }

   public static <E> RingBuffer<E> createMultiProducer(EventFactory<E> var0, int var1, WaitStrategy var2) {
      MultiProducerSequencer var3 = new MultiProducerSequencer(var1, var2);
      return new RingBuffer<>(var0, var3);
   }

   public static <E> RingBuffer<E> createMultiProducer(EventFactory<E> var0, int var1) {
      return createMultiProducer(var0, var1, new BlockingWaitStrategy());
   }

   public static <E> RingBuffer<E> createSingleProducer(EventFactory<E> var0, int var1, WaitStrategy var2) {
      SingleProducerSequencer var3 = new SingleProducerSequencer(var1, var2);
      return new RingBuffer<>(var0, var3);
   }

   public static <E> RingBuffer<E> createSingleProducer(EventFactory<E> var0, int var1) {
      return createSingleProducer(var0, var1, new BlockingWaitStrategy());
   }

   public static <E> RingBuffer<E> create(ProducerType var0, EventFactory<E> var1, int var2, WaitStrategy var3) {
      switch (var0) {
         case SINGLE:
            return createSingleProducer(var1, var2, var3);
         case MULTI:
            return createMultiProducer(var1, var2, var3);
         default:
            throw new IllegalStateException(var0.toString());
      }
   }

   @Override
   public E get(long var1) {
      return this.elementAt(var1);
   }

   @Override
   public long next() {
      return this.sequencer.next();
   }

   @Override
   public long next(int var1) {
      return this.sequencer.next(var1);
   }

   @Override
   public long tryNext() throws InsufficientCapacityException {
      return this.sequencer.tryNext();
   }

   @Override
   public long tryNext(int var1) throws InsufficientCapacityException {
      return this.sequencer.tryNext(var1);
   }

   @Deprecated
   public void resetTo(long var1) {
      this.sequencer.claim(var1);
      this.sequencer.publish(var1);
   }

   public E claimAndGetPreallocated(long var1) {
      this.sequencer.claim(var1);
      return this.get(var1);
   }

   @Deprecated
   public boolean isPublished(long var1) {
      return this.sequencer.isAvailable(var1);
   }

   public void addGatingSequences(Sequence... var1) {
      this.sequencer.addGatingSequences(var1);
   }

   public long getMinimumGatingSequence() {
      return this.sequencer.getMinimumSequence();
   }

   public boolean removeGatingSequence(Sequence var1) {
      return this.sequencer.removeGatingSequence(var1);
   }

   public SequenceBarrier newBarrier(Sequence... var1) {
      return this.sequencer.newBarrier(var1);
   }

   public EventPoller<E> newPoller(Sequence... var1) {
      return this.sequencer.newPoller(this, var1);
   }

   @Override
   public long getCursor() {
      return this.sequencer.getCursor();
   }

   @Override
   public int getBufferSize() {
      return this.bufferSize;
   }

   @Override
   public boolean hasAvailableCapacity(int var1) {
      return this.sequencer.hasAvailableCapacity(var1);
   }

   @Override
   public void publishEvent(EventTranslator<E> var1) {
      long var2 = this.sequencer.next();
      this.translateAndPublish(var1, var2);
   }

   @Override
   public boolean tryPublishEvent(EventTranslator<E> var1) {
      try {
         long var2 = this.sequencer.tryNext();
         this.translateAndPublish(var1, var2);
         return true;
      } catch (InsufficientCapacityException var4) {
         return false;
      }
   }

   @Override
   public <A> void publishEvent(EventTranslatorOneArg<E, A> var1, A var2) {
      long var3 = this.sequencer.next();
      this.translateAndPublish(var1, var3, var2);
   }

   @Override
   public <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> var1, A var2) {
      try {
         long var3 = this.sequencer.tryNext();
         this.translateAndPublish(var1, var3, var2);
         return true;
      } catch (InsufficientCapacityException var5) {
         return false;
      }
   }

   @Override
   public <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> var1, A var2, B var3) {
      long var4 = this.sequencer.next();
      this.translateAndPublish(var1, var4, var2, var3);
   }

   @Override
   public <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> var1, A var2, B var3) {
      try {
         long var4 = this.sequencer.tryNext();
         this.translateAndPublish(var1, var4, var2, var3);
         return true;
      } catch (InsufficientCapacityException var6) {
         return false;
      }
   }

   @Override
   public <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> var1, A var2, B var3, C var4) {
      long var5 = this.sequencer.next();
      this.translateAndPublish(var1, var5, var2, var3, var4);
   }

   @Override
   public <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> var1, A var2, B var3, C var4) {
      try {
         long var5 = this.sequencer.tryNext();
         this.translateAndPublish(var1, var5, var2, var3, var4);
         return true;
      } catch (InsufficientCapacityException var7) {
         return false;
      }
   }

   @Override
   public void publishEvent(EventTranslatorVararg<E> var1, Object... var2) {
      long var3 = this.sequencer.next();
      this.translateAndPublish(var1, var3, var2);
   }

   @Override
   public boolean tryPublishEvent(EventTranslatorVararg<E> var1, Object... var2) {
      try {
         long var3 = this.sequencer.tryNext();
         this.translateAndPublish(var1, var3, var2);
         return true;
      } catch (InsufficientCapacityException var5) {
         return false;
      }
   }

   @Override
   public void publishEvents(EventTranslator<E>[] var1) {
      this.publishEvents(var1, 0, var1.length);
   }

   @Override
   public void publishEvents(EventTranslator<E>[] var1, int var2, int var3) {
      this.checkBounds(var1, var2, var3);
      long var4 = this.sequencer.next(var3);
      this.translateAndPublishBatch(var1, var2, var3, var4);
   }

   @Override
   public boolean tryPublishEvents(EventTranslator<E>[] var1) {
      return this.tryPublishEvents(var1, 0, var1.length);
   }

   @Override
   public boolean tryPublishEvents(EventTranslator<E>[] var1, int var2, int var3) {
      this.checkBounds(var1, var2, var3);

      try {
         long var4 = this.sequencer.tryNext(var3);
         this.translateAndPublishBatch(var1, var2, var3, var4);
         return true;
      } catch (InsufficientCapacityException var6) {
         return false;
      }
   }

   @Override
   public <A> void publishEvents(EventTranslatorOneArg<E, A> var1, A[] var2) {
      this.publishEvents(var1, 0, var2.length, var2);
   }

   @Override
   public <A> void publishEvents(EventTranslatorOneArg<E, A> var1, int var2, int var3, A[] var4) {
      this.checkBounds(var4, var2, var3);
      long var5 = this.sequencer.next(var3);
      this.translateAndPublishBatch(var1, var4, var2, var3, var5);
   }

   @Override
   public <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> var1, A[] var2) {
      return this.tryPublishEvents(var1, 0, var2.length, var2);
   }

   @Override
   public <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> var1, int var2, int var3, A[] var4) {
      this.checkBounds(var4, var2, var3);

      try {
         long var5 = this.sequencer.tryNext(var3);
         this.translateAndPublishBatch(var1, var4, var2, var3, var5);
         return true;
      } catch (InsufficientCapacityException var7) {
         return false;
      }
   }

   @Override
   public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> var1, A[] var2, B[] var3) {
      this.publishEvents(var1, 0, var2.length, var2, var3);
   }

   @Override
   public <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> var1, int var2, int var3, A[] var4, B[] var5) {
      this.checkBounds(var4, var5, var2, var3);
      long var6 = this.sequencer.next(var3);
      this.translateAndPublishBatch(var1, var4, var5, var2, var3, var6);
   }

   @Override
   public <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> var1, A[] var2, B[] var3) {
      return this.tryPublishEvents(var1, 0, var2.length, var2, var3);
   }

   @Override
   public <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> var1, int var2, int var3, A[] var4, B[] var5) {
      this.checkBounds(var4, var5, var2, var3);

      try {
         long var6 = this.sequencer.tryNext(var3);
         this.translateAndPublishBatch(var1, var4, var5, var2, var3, var6);
         return true;
      } catch (InsufficientCapacityException var8) {
         return false;
      }
   }

   @Override
   public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> var1, A[] var2, B[] var3, C[] var4) {
      this.publishEvents(var1, 0, var2.length, var2, var3, var4);
   }

   @Override
   public <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> var1, int var2, int var3, A[] var4, B[] var5, C[] var6) {
      this.checkBounds(var4, var5, var6, var2, var3);
      long var7 = this.sequencer.next(var3);
      this.translateAndPublishBatch(var1, var4, var5, var6, var2, var3, var7);
   }

   @Override
   public <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> var1, A[] var2, B[] var3, C[] var4) {
      return this.tryPublishEvents(var1, 0, var2.length, var2, var3, var4);
   }

   @Override
   public <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> var1, int var2, int var3, A[] var4, B[] var5, C[] var6) {
      this.checkBounds(var4, var5, var6, var2, var3);

      try {
         long var7 = this.sequencer.tryNext(var3);
         this.translateAndPublishBatch(var1, var4, var5, var6, var2, var3, var7);
         return true;
      } catch (InsufficientCapacityException var9) {
         return false;
      }
   }

   @Override
   public void publishEvents(EventTranslatorVararg<E> var1, Object[]... var2) {
      this.publishEvents(var1, 0, var2.length, var2);
   }

   @Override
   public void publishEvents(EventTranslatorVararg<E> var1, int var2, int var3, Object[]... var4) {
      this.checkBounds(var2, var3, var4);
      long var5 = this.sequencer.next(var3);
      this.translateAndPublishBatch(var1, var2, var3, var5, var4);
   }

   @Override
   public boolean tryPublishEvents(EventTranslatorVararg<E> var1, Object[]... var2) {
      return this.tryPublishEvents(var1, 0, var2.length, var2);
   }

   @Override
   public boolean tryPublishEvents(EventTranslatorVararg<E> var1, int var2, int var3, Object[]... var4) {
      this.checkBounds(var4, var2, var3);

      try {
         long var5 = this.sequencer.tryNext(var3);
         this.translateAndPublishBatch(var1, var2, var3, var5, var4);
         return true;
      } catch (InsufficientCapacityException var7) {
         return false;
      }
   }

   @Override
   public void publish(long var1) {
      this.sequencer.publish(var1);
   }

   @Override
   public void publish(long var1, long var3) {
      this.sequencer.publish(var1, var3);
   }

   @Override
   public long remainingCapacity() {
      return this.sequencer.remainingCapacity();
   }

   private void checkBounds(EventTranslator<E>[] var1, int var2, int var3) {
      this.checkBatchSizing(var2, var3);
      this.batchOverRuns(var1, var2, var3);
   }

   private void checkBatchSizing(int var1, int var2) {
      if (var1 < 0 || var2 < 0) {
         throw new IllegalArgumentException("Both batchStartsAt and batchSize must be positive but got: batchStartsAt " + var1 + " and batchSize " + var2);
      } else if (var2 > this.bufferSize) {
         throw new IllegalArgumentException("The ring buffer cannot accommodate " + var2 + " it only has space for " + this.bufferSize + " entities.");
      }
   }

   private <A> void checkBounds(A[] var1, int var2, int var3) {
      this.checkBatchSizing(var2, var3);
      this.batchOverRuns(var1, var2, var3);
   }

   private <A, B> void checkBounds(A[] var1, B[] var2, int var3, int var4) {
      this.checkBatchSizing(var3, var4);
      this.batchOverRuns(var1, var3, var4);
      this.batchOverRuns(var2, var3, var4);
   }

   private <A, B, C> void checkBounds(A[] var1, B[] var2, C[] var3, int var4, int var5) {
      this.checkBatchSizing(var4, var5);
      this.batchOverRuns(var1, var4, var5);
      this.batchOverRuns(var2, var4, var5);
      this.batchOverRuns(var3, var4, var5);
   }

   private void checkBounds(int var1, int var2, Object[][] var3) {
      this.checkBatchSizing(var1, var2);
      this.batchOverRuns(var3, var1, var2);
   }

   private <A> void batchOverRuns(A[] var1, int var2, int var3) {
      if (var2 + var3 > var1.length) {
         throw new IllegalArgumentException(
            "A batchSize of: " + var3 + " with batchStatsAt of: " + var2 + " will overrun the available number of arguments: " + (var1.length - var2)
         );
      }
   }

   private void translateAndPublish(EventTranslator<E> var1, long var2) {
      try {
         var1.translateTo(this.get(var2), var2);
      } finally {
         this.sequencer.publish(var2);
      }
   }

   private <A> void translateAndPublish(EventTranslatorOneArg<E, A> var1, long var2, A var4) {
      try {
         var1.translateTo(this.get(var2), var2, var4);
      } finally {
         this.sequencer.publish(var2);
      }
   }

   private <A, B> void translateAndPublish(EventTranslatorTwoArg<E, A, B> var1, long var2, A var4, B var5) {
      try {
         var1.translateTo(this.get(var2), var2, var4, var5);
      } finally {
         this.sequencer.publish(var2);
      }
   }

   private <A, B, C> void translateAndPublish(EventTranslatorThreeArg<E, A, B, C> var1, long var2, A var4, B var5, C var6) {
      try {
         var1.translateTo(this.get(var2), var2, var4, var5, var6);
      } finally {
         this.sequencer.publish(var2);
      }
   }

   private void translateAndPublish(EventTranslatorVararg<E> var1, long var2, Object... var4) {
      try {
         var1.translateTo(this.get(var2), var2, var4);
      } finally {
         this.sequencer.publish(var2);
      }
   }

   private void translateAndPublishBatch(EventTranslator<E>[] var1, int var2, int var3, long var4) {
      long var6 = var4 - (long)(var3 - 1);

      try {
         long var8 = var6;
         int var10 = var2 + var3;

         for (int var11 = var2; var11 < var10; var11++) {
            EventTranslator var12 = var1[var11];
            var12.translateTo(this.get(var8), var8++);
         }
      } finally {
         this.sequencer.publish(var6, var4);
      }
   }

   private <A> void translateAndPublishBatch(EventTranslatorOneArg<E, A> var1, A[] var2, int var3, int var4, long var5) {
      long var7 = var5 - (long)(var4 - 1);

      try {
         long var9 = var7;
         int var11 = var3 + var4;

         for (int var12 = var3; var12 < var11; var12++) {
            var1.translateTo(this.get(var9), var9++, var2[var12]);
         }
      } finally {
         this.sequencer.publish(var7, var5);
      }
   }

   private <A, B> void translateAndPublishBatch(EventTranslatorTwoArg<E, A, B> var1, A[] var2, B[] var3, int var4, int var5, long var6) {
      long var8 = var6 - (long)(var5 - 1);

      try {
         long var10 = var8;
         int var12 = var4 + var5;

         for (int var13 = var4; var13 < var12; var13++) {
            var1.translateTo(this.get(var10), var10++, var2[var13], var3[var13]);
         }
      } finally {
         this.sequencer.publish(var8, var6);
      }
   }

   private <A, B, C> void translateAndPublishBatch(EventTranslatorThreeArg<E, A, B, C> var1, A[] var2, B[] var3, C[] var4, int var5, int var6, long var7) {
      long var9 = var7 - (long)(var6 - 1);

      try {
         long var11 = var9;
         int var13 = var5 + var6;

         for (int var14 = var5; var14 < var13; var14++) {
            var1.translateTo(this.get(var11), var11++, var2[var14], var3[var14], var4[var14]);
         }
      } finally {
         this.sequencer.publish(var9, var7);
      }
   }

   private void translateAndPublishBatch(EventTranslatorVararg<E> var1, int var2, int var3, long var4, Object[][] var6) {
      long var7 = var4 - (long)(var3 - 1);

      try {
         long var9 = var7;
         int var11 = var2 + var3;

         for (int var12 = var2; var12 < var11; var12++) {
            var1.translateTo(this.get(var9), var9++, var6[var12]);
         }
      } finally {
         this.sequencer.publish(var7, var4);
      }
   }

   @Override
   public String toString() {
      return "RingBuffer{bufferSize=" + this.bufferSize + ", sequencer=" + this.sequencer + "}";
   }
}
