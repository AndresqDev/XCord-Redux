package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;

public class WeakMemoryHolder {
   ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
   IdentityHashMap<Reference<Object>, Memory> backingMap = new IdentityHashMap<>();

   public synchronized void put(Object var1, Memory var2) {
      this.clean();
      WeakReference var3 = new WeakReference<>(var1, this.referenceQueue);
      this.backingMap.put(var3, var2);
   }

   public synchronized void clean() {
      for (Reference var1 = this.referenceQueue.poll(); var1 != null; var1 = this.referenceQueue.poll()) {
         this.backingMap.remove(var1);
      }
   }
}
