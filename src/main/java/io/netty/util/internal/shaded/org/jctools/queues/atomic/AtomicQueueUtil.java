package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

final class AtomicQueueUtil {
   static <E> E lvRefElement(AtomicReferenceArray<E> var0, int var1) {
      return (E)var0.get(var1);
   }

   static <E> E lpRefElement(AtomicReferenceArray<E> var0, int var1) {
      return (E)var0.get(var1);
   }

   static <E> void spRefElement(AtomicReferenceArray<E> var0, int var1, E var2) {
      var0.lazySet(var1, var2);
   }

   static void soRefElement(AtomicReferenceArray var0, int var1, Object var2) {
      var0.lazySet(var1, var2);
   }

   static <E> void svRefElement(AtomicReferenceArray<E> var0, int var1, E var2) {
      var0.set(var1, var2);
   }

   static int calcRefElementOffset(long var0) {
      return (int)var0;
   }

   static int calcCircularRefElementOffset(long var0, long var2) {
      return (int)(var0 & var2);
   }

   static <E> AtomicReferenceArray<E> allocateRefArray(int var0) {
      return new AtomicReferenceArray<>(var0);
   }

   static void spLongElement(AtomicLongArray var0, int var1, long var2) {
      var0.lazySet(var1, var2);
   }

   static void soLongElement(AtomicLongArray var0, int var1, long var2) {
      var0.lazySet(var1, var2);
   }

   static long lpLongElement(AtomicLongArray var0, int var1) {
      return var0.get(var1);
   }

   static long lvLongElement(AtomicLongArray var0, int var1) {
      return var0.get(var1);
   }

   static int calcLongElementOffset(long var0) {
      return (int)var0;
   }

   static int calcCircularLongElementOffset(long var0, int var2) {
      return (int)(var0 & (long)var2);
   }

   static AtomicLongArray allocateLongArray(int var0) {
      return new AtomicLongArray(var0);
   }

   static int length(AtomicReferenceArray<?> var0) {
      return var0.length();
   }

   static int modifiedCalcCircularRefElementOffset(long var0, long var2) {
      return (int)(var0 & var2) >> 1;
   }

   static int nextArrayOffset(AtomicReferenceArray<?> var0) {
      return length(var0) - 1;
   }
}
