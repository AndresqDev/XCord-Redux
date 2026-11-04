package com.lmax.disruptor.dsl;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkHandler;
import java.util.Arrays;

public class EventHandlerGroup<T> {
   private final Disruptor<T> disruptor;
   private final ConsumerRepository<T> consumerRepository;
   private final Sequence[] sequences;

   EventHandlerGroup(Disruptor<T> var1, ConsumerRepository<T> var2, Sequence[] var3) {
      this.disruptor = var1;
      this.consumerRepository = var2;
      this.sequences = Arrays.copyOf(var3, var3.length);
   }

   public EventHandlerGroup<T> and(EventHandlerGroup<T> var1) {
      Sequence[] var2 = new Sequence[this.sequences.length + var1.sequences.length];
      System.arraycopy(this.sequences, 0, var2, 0, this.sequences.length);
      System.arraycopy(var1.sequences, 0, var2, this.sequences.length, var1.sequences.length);
      return new EventHandlerGroup<>(this.disruptor, this.consumerRepository, var2);
   }

   public EventHandlerGroup<T> and(EventProcessor... var1) {
      Sequence[] var2 = new Sequence[this.sequences.length + var1.length];

      for (int var3 = 0; var3 < var1.length; var3++) {
         this.consumerRepository.add(var1[var3]);
         var2[var3] = var1[var3].getSequence();
      }

      System.arraycopy(this.sequences, 0, var2, var1.length, this.sequences.length);
      return new EventHandlerGroup<>(this.disruptor, this.consumerRepository, var2);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> then(EventHandler<? super T>... var1) {
      return this.handleEventsWith(var1);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> then(EventProcessorFactory<T>... var1) {
      return this.handleEventsWith(var1);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> thenHandleEventsWithWorkerPool(WorkHandler<? super T>... var1) {
      return this.handleEventsWithWorkerPool(var1);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> handleEventsWith(EventHandler<? super T>... var1) {
      return this.disruptor.createEventProcessors(this.sequences, var1);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> handleEventsWith(EventProcessorFactory<T>... var1) {
      return this.disruptor.createEventProcessors(this.sequences, var1);
   }

   @SafeVarargs
   public final EventHandlerGroup<T> handleEventsWithWorkerPool(WorkHandler<? super T>... var1) {
      return this.disruptor.createWorkerPool(this.sequences, var1);
   }

   public SequenceBarrier asSequenceBarrier() {
      return this.disruptor.getRingBuffer().newBarrier(this.sequences);
   }
}
