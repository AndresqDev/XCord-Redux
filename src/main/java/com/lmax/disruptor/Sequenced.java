package com.lmax.disruptor;

public interface Sequenced {
   int getBufferSize();

   boolean hasAvailableCapacity(int var1);

   long remainingCapacity();

   long next();

   long next(int var1);

   long tryNext() throws InsufficientCapacityException;

   long tryNext(int var1) throws InsufficientCapacityException;

   void publish(long var1);

   void publish(long var1, long var3);
}
