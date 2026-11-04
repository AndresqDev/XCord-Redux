package com.google.common.hash;

@ElementTypesAreNonnullByDefault
interface LongAddable {
   void increment();

   void add(long var1);

   long sum();
}
