package com.lmax.disruptor;

public interface EventSink<E> {
   void publishEvent(EventTranslator<E> var1);

   boolean tryPublishEvent(EventTranslator<E> var1);

   <A> void publishEvent(EventTranslatorOneArg<E, A> var1, A var2);

   <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> var1, A var2);

   <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> var1, A var2, B var3);

   <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> var1, A var2, B var3);

   <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> var1, A var2, B var3, C var4);

   <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> var1, A var2, B var3, C var4);

   void publishEvent(EventTranslatorVararg<E> var1, Object... var2);

   boolean tryPublishEvent(EventTranslatorVararg<E> var1, Object... var2);

   void publishEvents(EventTranslator<E>[] var1);

   void publishEvents(EventTranslator<E>[] var1, int var2, int var3);

   boolean tryPublishEvents(EventTranslator<E>[] var1);

   boolean tryPublishEvents(EventTranslator<E>[] var1, int var2, int var3);

   <A> void publishEvents(EventTranslatorOneArg<E, A> var1, A[] var2);

   <A> void publishEvents(EventTranslatorOneArg<E, A> var1, int var2, int var3, A[] var4);

   <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> var1, A[] var2);

   <A> boolean tryPublishEvents(EventTranslatorOneArg<E, A> var1, int var2, int var3, A[] var4);

   <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> var1, A[] var2, B[] var3);

   <A, B> void publishEvents(EventTranslatorTwoArg<E, A, B> var1, int var2, int var3, A[] var4, B[] var5);

   <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> var1, A[] var2, B[] var3);

   <A, B> boolean tryPublishEvents(EventTranslatorTwoArg<E, A, B> var1, int var2, int var3, A[] var4, B[] var5);

   <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> var1, A[] var2, B[] var3, C[] var4);

   <A, B, C> void publishEvents(EventTranslatorThreeArg<E, A, B, C> var1, int var2, int var3, A[] var4, B[] var5, C[] var6);

   <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> var1, A[] var2, B[] var3, C[] var4);

   <A, B, C> boolean tryPublishEvents(EventTranslatorThreeArg<E, A, B, C> var1, int var2, int var3, A[] var4, B[] var5, C[] var6);

   void publishEvents(EventTranslatorVararg<E> var1, Object[]... var2);

   void publishEvents(EventTranslatorVararg<E> var1, int var2, int var3, Object[]... var4);

   boolean tryPublishEvents(EventTranslatorVararg<E> var1, Object[]... var2);

   boolean tryPublishEvents(EventTranslatorVararg<E> var1, int var2, int var3, Object[]... var4);
}
