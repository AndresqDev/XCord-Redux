package com.lmax.disruptor;

public interface EventHandler<T> {
   void onEvent(T var1, long var2, boolean var4) throws Exception;
}
