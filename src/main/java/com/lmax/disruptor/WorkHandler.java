package com.lmax.disruptor;

public interface WorkHandler<T> {
   void onEvent(T var1) throws Exception;
}
