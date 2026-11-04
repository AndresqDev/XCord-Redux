package com.lmax.disruptor;

public class EventPoller<T> {
   private final DataProvider<T> dataProvider;
   private final Sequencer sequencer;
   private final Sequence sequence;
   private final Sequence gatingSequence;

   public EventPoller(DataProvider<T> var1, Sequencer var2, Sequence var3, Sequence var4) {
      this.dataProvider = var1;
      this.sequencer = var2;
      this.sequence = var3;
      this.gatingSequence = var4;
   }

   public EventPoller.PollState poll(EventPoller.Handler<T> var1) throws Exception {
      long var2 = this.sequence.get();
      long var4 = var2 + 1L;
      long var6 = this.sequencer.getHighestPublishedSequence(var4, this.gatingSequence.get());
      if (var4 > var6) {
         return this.sequencer.getCursor() >= var4 ? EventPoller.PollState.GATING : EventPoller.PollState.IDLE;
      } else {
         long var9 = var2;

         boolean var8;
         try {
            do {
               Object var11 = this.dataProvider.get(var4);
               var8 = var1.onEvent(var11, var4, var4 == var6);
               var9 = var4++;
            } while (var4 <= var6 & var8);
         } finally {
            this.sequence.set(var9);
         }

         return EventPoller.PollState.PROCESSING;
      }
   }

   public static <T> EventPoller<T> newInstance(DataProvider<T> var0, Sequencer var1, Sequence var2, Sequence var3, Sequence... var4) {
      Object var5;
      if (var4.length == 0) {
         var5 = var3;
      } else if (var4.length == 1) {
         var5 = var4[0];
      } else {
         var5 = new FixedSequenceGroup(var4);
      }

      return new EventPoller<>(var0, var1, var2, (Sequence)var5);
   }

   public Sequence getSequence() {
      return this.sequence;
   }

   public interface Handler<T> {
      boolean onEvent(T var1, long var2, boolean var4) throws Exception;
   }

   public static enum PollState {
      PROCESSING,
      GATING,
      IDLE;
   }
}
