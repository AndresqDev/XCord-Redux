package com.lmax.disruptor;

public interface EventTranslator<T> {
   void translateTo(T var1, long var2);
}
