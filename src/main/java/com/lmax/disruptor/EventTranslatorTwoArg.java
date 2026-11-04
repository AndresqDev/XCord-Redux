package com.lmax.disruptor;

public interface EventTranslatorTwoArg<T, A, B> {
   void translateTo(T var1, long var2, A var4, B var5);
}
