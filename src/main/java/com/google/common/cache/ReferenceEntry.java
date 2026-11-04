package com.google.common.cache;

import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
interface ReferenceEntry<K, V> {
   LocalCache.ValueReference<K, V> getValueReference();

   void setValueReference(LocalCache.ValueReference<K, V> var1);

   ReferenceEntry<K, V> getNext();

   int getHash();

   K getKey();

   long getAccessTime();

   void setAccessTime(long var1);

   ReferenceEntry<K, V> getNextInAccessQueue();

   void setNextInAccessQueue(ReferenceEntry<K, V> var1);

   ReferenceEntry<K, V> getPreviousInAccessQueue();

   void setPreviousInAccessQueue(ReferenceEntry<K, V> var1);

   long getWriteTime();

   void setWriteTime(long var1);

   ReferenceEntry<K, V> getNextInWriteQueue();

   void setNextInWriteQueue(ReferenceEntry<K, V> var1);

   ReferenceEntry<K, V> getPreviousInWriteQueue();

   void setPreviousInWriteQueue(ReferenceEntry<K, V> var1);
}
