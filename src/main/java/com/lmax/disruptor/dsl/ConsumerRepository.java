package com.lmax.disruptor.dsl;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WorkerPool;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

class ConsumerRepository<T> implements Iterable<ConsumerInfo> {
   private final Map<EventHandler<?>, EventProcessorInfo<T>> eventProcessorInfoByEventHandler = new IdentityHashMap<>();
   private final Map<Sequence, ConsumerInfo> eventProcessorInfoBySequence = new IdentityHashMap<>();
   private final Collection<ConsumerInfo> consumerInfos = new ArrayList<>();

   public void add(EventProcessor var1, EventHandler<? super T> var2, SequenceBarrier var3) {
      EventProcessorInfo var4 = new EventProcessorInfo(var1, var2, var3);
      this.eventProcessorInfoByEventHandler.put(var2, var4);
      this.eventProcessorInfoBySequence.put(var1.getSequence(), var4);
      this.consumerInfos.add(var4);
   }

   public void add(EventProcessor var1) {
      EventProcessorInfo var2 = new EventProcessorInfo(var1, null, null);
      this.eventProcessorInfoBySequence.put(var1.getSequence(), var2);
      this.consumerInfos.add(var2);
   }

   public void add(WorkerPool<T> var1, SequenceBarrier var2) {
      WorkerPoolInfo var3 = new WorkerPoolInfo(var1, var2);
      this.consumerInfos.add(var3);

      for (Sequence var7 : var1.getWorkerSequences()) {
         this.eventProcessorInfoBySequence.put(var7, var3);
      }
   }

   public Sequence[] getLastSequenceInChain(boolean var1) {
      ArrayList var2 = new ArrayList();

      for (ConsumerInfo var4 : this.consumerInfos) {
         if ((var1 || var4.isRunning()) && var4.isEndOfChain()) {
            Sequence[] var5 = var4.getSequences();
            Collections.addAll(var2, var5);
         }
      }

      return var2.toArray(new Sequence[var2.size()]);
   }

   public EventProcessor getEventProcessorFor(EventHandler<T> var1) {
      EventProcessorInfo var2 = this.getEventProcessorInfo(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("The event handler " + var1 + " is not processing events.");
      } else {
         return var2.getEventProcessor();
      }
   }

   public Sequence getSequenceFor(EventHandler<T> var1) {
      return this.getEventProcessorFor(var1).getSequence();
   }

   public void unMarkEventProcessorsAsEndOfChain(Sequence... var1) {
      for (Sequence var5 : var1) {
         this.getEventProcessorInfo(var5).markAsUsedInBarrier();
      }
   }

   @Override
   public Iterator<ConsumerInfo> iterator() {
      return this.consumerInfos.iterator();
   }

   public SequenceBarrier getBarrierFor(EventHandler<T> var1) {
      EventProcessorInfo var2 = this.getEventProcessorInfo(var1);
      return var2 != null ? var2.getBarrier() : null;
   }

   private EventProcessorInfo<T> getEventProcessorInfo(EventHandler<T> var1) {
      return this.eventProcessorInfoByEventHandler.get(var1);
   }

   private ConsumerInfo getEventProcessorInfo(Sequence var1) {
      return this.eventProcessorInfoBySequence.get(var1);
   }
}
