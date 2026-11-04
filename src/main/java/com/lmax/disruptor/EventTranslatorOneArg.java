package com.lmax.disruptor;

public interface EventTranslatorOneArg<T, A> {
   void translateTo(T var1, long var2, A var4);
}
