package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.ObjIntConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
abstract class AbstractMapBasedMultiset<E> extends AbstractMultiset<E> implements Serializable {
   private transient Map<E, Count> backingMap;
   private transient long size;
   @GwtIncompatible
   private static final long serialVersionUID = -2250766705698539974L;

   protected AbstractMapBasedMultiset(Map<E, Count> var1) {
      Preconditions.checkArgument(var1.isEmpty());
      this.backingMap = var1;
   }

   void setBackingMap(Map<E, Count> var1) {
      this.backingMap = var1;
   }

   @Override
   public Set<Multiset.Entry<E>> entrySet() {
      return super.entrySet();
   }

   @Override
   Iterator<E> elementIterator() {
      final Iterator var1 = this.backingMap.entrySet().iterator();
      return new Iterator<E>() {
         Map.Entry<E, Count> toRemove;

         @Override
         public boolean hasNext() {
            return var1.hasNext();
         }

         @ParametricNullness
         @Override
         public E next() {
            Map.Entry var1x = (Map.Entry)var1.next();
            this.toRemove = var1x;
            return (E)var1x.getKey();
         }

         @Override
         public void remove() {
            Preconditions.checkState(this.toRemove != null, "no calls to next() since the last call to remove()");
            AbstractMapBasedMultiset.this.size -= (long)this.toRemove.getValue().getAndSet(0);
            var1.remove();
            this.toRemove = null;
         }
      };
   }

   @Override
   Iterator<Multiset.Entry<E>> entryIterator() {
      final Iterator var1 = this.backingMap.entrySet().iterator();
      return new Iterator<Multiset.Entry<E>>() {
         Map.Entry<E, Count> toRemove;

         @Override
         public boolean hasNext() {
            return var1.hasNext();
         }

         public Multiset.Entry<E> next() {
            final Map.Entry var1x = (Map.Entry)var1.next();
            this.toRemove = var1x;
            return new Multisets.AbstractEntry<E>() {
               @ParametricNullness
               @Override
               public E getElement() {
                  return (E)var1x.getKey();
               }

               @Override
               public int getCount() {
                  Count var1xx = (Count)var1x.getValue();
                  if (var1xx == null || var1xx.get() == 0) {
                     Count var2 = AbstractMapBasedMultiset.this.backingMap.get(this.getElement());
                     if (var2 != null) {
                        return var2.get();
                     }
                  }

                  return var1xx == null ? 0 : var1xx.get();
               }
            };
         }

         @Override
         public void remove() {
            Preconditions.checkState(this.toRemove != null, "no calls to next() since the last call to remove()");
            AbstractMapBasedMultiset.this.size -= (long)this.toRemove.getValue().getAndSet(0);
            var1.remove();
            this.toRemove = null;
         }
      };
   }

   @Override
   public void forEachEntry(ObjIntConsumer<? super E> var1) {
      Preconditions.checkNotNull(var1);
      this.backingMap.forEach((var1x, var2) -> var1.accept(var1x, var2.get()));
   }

   @Override
   public void clear() {
      for (Count var2 : this.backingMap.values()) {
         var2.set(0);
      }

      this.backingMap.clear();
      this.size = 0L;
   }

   @Override
   int distinctElements() {
      return this.backingMap.size();
   }

   @Override
   public int size() {
      return Ints.saturatedCast(this.size);
   }

   @Override
   public Iterator<E> iterator() {
      return new AbstractMapBasedMultiset.MapBasedMultisetIterator();
   }

   @Override
   public int count(Object var1) {
      Count var2 = Maps.safeGet(this.backingMap, var1);
      return var2 == null ? 0 : var2.get();
   }

   @CanIgnoreReturnValue
   @Override
   public int add(@ParametricNullness E var1, int var2) {
      if (var2 == 0) {
         return this.count(var1);
      } else {
         Preconditions.checkArgument(var2 > 0, "occurrences cannot be negative: %s", var2);
         Count var3 = this.backingMap.get(var1);
         int var4;
         if (var3 == null) {
            var4 = 0;
            this.backingMap.put((E)var1, new Count(var2));
         } else {
            var4 = var3.get();
            long var5 = (long)var4 + (long)var2;
            Preconditions.checkArgument(var5 <= 2147483647L, "too many occurrences: %s", var5);
            var3.add(var2);
         }

         this.size += (long)var2;
         return var4;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public int remove(Object var1, int var2) {
      if (var2 == 0) {
         return this.count(var1);
      } else {
         Preconditions.checkArgument(var2 > 0, "occurrences cannot be negative: %s", var2);
         Count var3 = this.backingMap.get(var1);
         if (var3 == null) {
            return 0;
         } else {
            int var4 = var3.get();
            int var5;
            if (var4 > var2) {
               var5 = var2;
            } else {
               var5 = var4;
               this.backingMap.remove(var1);
            }

            var3.add(-var5);
            this.size -= (long)var5;
            return var4;
         }
      }
   }

   @CanIgnoreReturnValue
   @Override
   public int setCount(@ParametricNullness E var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "count");
      int var4;
      if (var2 == 0) {
         Count var3 = this.backingMap.remove(var1);
         var4 = getAndSet(var3, var2);
      } else {
         Count var5 = this.backingMap.get(var1);
         var4 = getAndSet(var5, var2);
         if (var5 == null) {
            this.backingMap.put((E)var1, new Count(var2));
         }
      }

      this.size += (long)(var2 - var4);
      return var4;
   }

   private static int getAndSet(Count var0, int var1) {
      return var0 == null ? 0 : var0.getAndSet(var1);
   }

   @GwtIncompatible
   private void readObjectNoData() throws ObjectStreamException {
      throw new InvalidObjectException("Stream data required");
   }

   private class MapBasedMultisetIterator implements Iterator<E> {
      final Iterator<Map.Entry<E, Count>> entryIterator = AbstractMapBasedMultiset.this.backingMap.entrySet().iterator();
      Map.Entry<E, Count> currentEntry;
      int occurrencesLeft;
      boolean canRemove;

      MapBasedMultisetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.occurrencesLeft > 0 || this.entryIterator.hasNext();
      }

      @ParametricNullness
      @Override
      public E next() {
         if (this.occurrencesLeft == 0) {
            this.currentEntry = this.entryIterator.next();
            this.occurrencesLeft = this.currentEntry.getValue().get();
         }

         this.occurrencesLeft--;
         this.canRemove = true;
         return Objects.requireNonNull(this.currentEntry).getKey();
      }

      @Override
      public void remove() {
         CollectPreconditions.checkRemove(this.canRemove);
         int var1 = Objects.requireNonNull(this.currentEntry).getValue().get();
         if (var1 <= 0) {
            throw new ConcurrentModificationException();
         } else {
            if (this.currentEntry.getValue().addAndGet(-1) == 0) {
               this.entryIterator.remove();
            }

            AbstractMapBasedMultiset.this.size--;
            this.canRemove = false;
         }
      }
   }
}
