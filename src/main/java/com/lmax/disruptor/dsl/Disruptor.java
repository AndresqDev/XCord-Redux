package com.lmax.disruptor.dsl;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.util.Util;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Disruptor<T> {
   private final RingBuffer<T> ringBuffer;
   private final Executor executor;
   private final ConsumerRepository<T> consumerRepository = new ConsumerRepository<>();
   private final AtomicBoolean started = new AtomicBoolean(false);
   private ExceptionHandler<? super T> exceptionHandler = new ExceptionHandlerWrapper<>();

   @Deprecated
   public Disruptor(EventFactory<T> var1, int var2, Executor var3) {
      this(RingBuffer.createMultiProducer(var1, var2), var3);
   }

   @Deprecated
   public Disruptor(EventFactory<T> var1, int var2, Executor var3, ProducerType var4, WaitStrategy var5) {
      this(RingBuffer.create(var4, var1, var2, var5), var3);
   }

   public Disruptor(EventFactory<T> var1, int var2, ThreadFactory var3) {
      this(RingBuffer.createMultiProducer(var1, var2), new BasicExecutor(var3));
   }

   public Disruptor(EventFactory<T> var1, int var2, ThreadFactory var3, ProducerType var4, WaitStrategy var5) {
      this(RingBuffer.create(var4, var1, var2, var5), new BasicExecutor(var3));
   }

   private Disruptor(RingBuffer<T> var1, Executor var2) {
      this.ringBuffer = var1;
      this.executor = var2;
   }

   @SafeVarargs
   public final EventHandlerGroup<T> handleEventsWith(EventHandler<? super T>... var1) {
      return this.createEventProcessors(new Sequence[0], var1);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> handleEventsWith(EventProcessorFactory<T>... var1) {
      Sequence[] var2 = new Sequence[0];
      return this.createEventProcessors(var2, var1);
   }

   public EventHandlerGroup<T> handleEventsWith(EventProcessor... var1) {
      for (EventProcessor var5 : var1) {
         this.consumerRepository.add(var5);
      }

      Sequence[] var6 = new Sequence[var1.length];

      for (int var7 = 0; var7 < var1.length; var7++) {
         var6[var7] = var1[var7].getSequence();
      }

      this.ringBuffer.addGatingSequences(var6);
      return new EventHandlerGroup<>(this, this.consumerRepository, Util.getSequencesFor(var1));
   }

   @SafeVarargs
   public final EventHandlerGroup<T> handleEventsWithWorkerPool(WorkHandler<T>... var1) {
      return this.createWorkerPool(new Sequence[0], var1);
   }

      public void handleExceptionsWith(ExceptionHandler<? super T> var1) {
      this.exceptionHandler = var1;
   }

   public void setDefaultExceptionHandler(ExceptionHandler<? super T> var1) {
      this.checkNotStarted();
      if (!(this.exceptionHandler instanceof ExceptionHandlerWrapper)) {
         throw new IllegalStateException("setDefaultExceptionHandler can not be used after handleExceptionsWith");
      } else {
         ((ExceptionHandlerWrapper)this.exceptionHandler).switchTo(var1);
      }
   }

   public ExceptionHandlerSetting<T> handleExceptionsFor(EventHandler<T> var1) {
      return new ExceptionHandlerSetting<>(var1, this.consumerRepository);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> after(EventHandler<T>... var1) {
      Sequence[] var2 = new Sequence[var1.length];
      int var3 = 0;

      for (int var4 = var1.length; var3 < var4; var3++) {
         var2[var3] = this.consumerRepository.getSequenceFor(var1[var3]);
      }

      return new EventHandlerGroup<>(this, this.consumerRepository, var2);
   }

   public EventHandlerGroup<T> after(EventProcessor... var1) {
      for (EventProcessor var5 : var1) {
         this.consumerRepository.add(var5);
      }

      return new EventHandlerGroup<>(this, this.consumerRepository, Util.getSequencesFor(var1));
   }

   public void publishEvent(EventTranslator<T> var1) {
      this.ringBuffer.publishEvent(var1);
   }

   public <A> void publishEvent(EventTranslatorOneArg<T, A> var1, A var2) {
      this.ringBuffer.publishEvent(var1, var2);
   }

   public <A> void publishEvents(EventTranslatorOneArg<T, A> var1, A[] var2) {
      this.ringBuffer.publishEvents(var1, var2);
   }

   public <A, B> void publishEvent(EventTranslatorTwoArg<T, A, B> var1, A var2, B var3) {
      this.ringBuffer.publishEvent(var1, var2, var3);
   }

   public <A, B, C> void publishEvent(EventTranslatorThreeArg<T, A, B, C> var1, A var2, B var3, C var4) {
      this.ringBuffer.publishEvent(var1, var2, var3, var4);
   }

   public RingBuffer<T> start() {
      this.checkOnlyStartedOnce();

      for (ConsumerInfo var2 : this.consumerRepository) {
         var2.start(this.executor);
      }

      return this.ringBuffer;
   }

   public void halt() {
      for (ConsumerInfo var2 : this.consumerRepository) {
         var2.halt();
      }
   }

   public void shutdown() {
      try {
         this.shutdown(-1L, TimeUnit.MILLISECONDS);
      } catch (TimeoutException var2) {
         this.exceptionHandler.handleOnShutdownException(var2);
      }
   }

   public void shutdown(long var1, TimeUnit var3) throws TimeoutException {
      long var4 = System.currentTimeMillis() + var3.toMillis(var1);

      while (this.hasBacklog()) {
         if (var1 >= 0L && System.currentTimeMillis() > var4) {
            throw TimeoutException.INSTANCE;
         }
      }

      this.halt();
   }

   public RingBuffer<T> getRingBuffer() {
      return this.ringBuffer;
   }

   public long getCursor() {
      return this.ringBuffer.getCursor();
   }

   public long getBufferSize() {
      return (long)this.ringBuffer.getBufferSize();
   }

   public T get(long var1) {
      return this.ringBuffer.get(var1);
   }

   public SequenceBarrier getBarrierFor(EventHandler<T> var1) {
      return this.consumerRepository.getBarrierFor(var1);
   }

   public long getSequenceValueFor(EventHandler<T> var1) {
      return this.consumerRepository.getSequenceFor(var1).get();
   }

   private boolean hasBacklog() {
      long var1 = this.ringBuffer.getCursor();

      for (Sequence var6 : this.consumerRepository.getLastSequenceInChain(false)) {
         if (var1 > var6.get()) {
            return true;
         }
      }

      return false;
   }

   EventHandlerGroup<T> createEventProcessors(Sequence[] var1, EventHandler<? super T>[] var2) {
      this.checkNotStarted();
      Sequence[] var3 = new Sequence[var2.length];
      SequenceBarrier var4 = this.ringBuffer.newBarrier(var1);
      int var5 = 0;

      for (int var6 = var2.length; var5 < var6; var5++) {
         EventHandler var7 = var2[var5];
         BatchEventProcessor var8 = new BatchEventProcessor<>(this.ringBuffer, var4, var7);
         if (this.exceptionHandler != null) {
            var8.setExceptionHandler(this.exceptionHandler);
         }

         this.consumerRepository.add(var8, var7, var4);
         var3[var5] = var8.getSequence();
      }

      this.updateGatingSequencesForNextInChain(var1, var3);
      return new EventHandlerGroup<>(this, this.consumerRepository, var3);
   }

   private void updateGatingSequencesForNextInChain(Sequence[] var1, Sequence[] var2) {
      if (var2.length > 0) {
         this.ringBuffer.addGatingSequences(var2);

         for (Sequence var6 : var1) {
            this.ringBuffer.removeGatingSequence(var6);
         }

         this.consumerRepository.unMarkEventProcessorsAsEndOfChain(var1);
      }
   }

   EventHandlerGroup<T> createEventProcessors(Sequence[] var1, EventProcessorFactory<T>[] var2) {
      EventProcessor[] var3 = new EventProcessor[var2.length];

      for (int var4 = 0; var4 < var2.length; var4++) {
         var3[var4] = var2[var4].createEventProcessor(this.ringBuffer, var1);
      }

      return this.handleEventsWith(var3);
   }

   EventHandlerGroup<T> createWorkerPool(Sequence[] var1, WorkHandler<? super T>[] var2) {
      SequenceBarrier var3 = this.ringBuffer.newBarrier(var1);
      WorkerPool var4 = new WorkerPool<>(this.ringBuffer, var3, this.exceptionHandler, var2);
      this.consumerRepository.add(var4, var3);
      Sequence[] var5 = var4.getWorkerSequences();
      this.updateGatingSequencesForNextInChain(var1, var5);
      return new EventHandlerGroup<>(this, this.consumerRepository, var5);
   }

   private void checkNotStarted() {
      if (this.started.get()) {
         throw new IllegalStateException("All event handlers must be added before calling starts.");
      }
   }

   private void checkOnlyStartedOnce() {
      if (!this.started.compareAndSet(false, true)) {
         throw new IllegalStateException("Disruptor.start() must only be called once.");
      }
   }

   @Override
   public String toString() {
      return "Disruptor{ringBuffer=" + this.ringBuffer + ", started=" + this.started + ", executor=" + this.executor + '}';
   }
}
