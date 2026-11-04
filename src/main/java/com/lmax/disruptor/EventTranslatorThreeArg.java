package com.lmax.disruptor;

public interface EventTranslatorThreeArg<T, A, B, C> {
   void translateTo(T var1, long var2, A var4, B var5, C var6);
}
