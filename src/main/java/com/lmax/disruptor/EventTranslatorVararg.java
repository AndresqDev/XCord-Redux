package com.lmax.disruptor;

public interface EventTranslatorVararg<T> {
   void translateTo(T var1, long var2, Object... var4);
}
