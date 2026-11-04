package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E> {
   static final RegularImmutableSortedSet<Comparable> NATURAL_EMPTY_SET = new RegularImmutableSortedSet<>(ImmutableList.of(), Ordering.natural());
   private final transient ImmutableList<E> elements;

   RegularImmutableSortedSet(ImmutableList<E> var1, Comparator<? super E> var2) {
      super(var2);
      this.elements = var1;
   }

   @Override
   Object[] internalArray() {
      return this.elements.internalArray();
   }

   @Override
   int internalArrayStart() {
      return this.elements.internalArrayStart();
   }

   @Override
   int internalArrayEnd() {
      return this.elements.internalArrayEnd();
   }

   @Override
   public UnmodifiableIterator<E> iterator() {
      return this.elements.iterator();
   }

   @GwtIncompatible
   @Override
   public UnmodifiableIterator<E> descendingIterator() {
      return this.elements.reverse().iterator();
   }

   @Override
   public Spliterator<E> spliterator() {
      return this.asList().spliterator();
   }

   @Override
   public void forEach(Consumer<? super E> var1) {
      this.elements.forEach(var1);
   }

   @Override
   public int size() {
      return this.elements.size();
   }

   @Override
   public boolean contains(Object var1) {
      try {
         return var1 != null && this.unsafeBinarySearch(var1) >= 0;
      } catch (ClassCastException var3) {
         return false;
      }
   }

   @Override
   public boolean containsAll(Collection<?> var1) {
      if (var1 instanceof Multiset) {
         var1 = ((Multiset)var1).elementSet();
      }

      if (SortedIterables.hasSameComparator(this.comparator(), (Iterable<?>)var1) && var1.size() > 1) {
         UnmodifiableIterator var2 = this.iterator();
         Iterator var3 = var1.iterator();
         if (!var2.hasNext()) {
            return false;
         } else {
            Object var4 = var3.next();
            Object var5 = var2.next();

            try {
               while (true) {
                  int var6 = this.unsafeCompare(var5, var4);
                  if (var6 < 0) {
                     if (!var2.hasNext()) {
                        return false;
                     }

                     var5 = var2.next();
                  } else if (var6 == 0) {
                     if (!var3.hasNext()) {
                        return true;
                     }

                     var4 = var3.next();
                  } else if (var6 > 0) {
                     return false;
                  }
               }
            } catch (ClassCastException | NullPointerException var7) {
               return false;
            }
         }
      } else {
         return super.containsAll((Collection<?>)var1);
      }
   }

   private int unsafeBinarySearch(Object var1) throws ClassCastException {
      return Collections.binarySearch(this.elements, (E)var1, this.unsafeComparator());
   }

   @Override
   boolean isPartialView() {
      return this.elements.isPartialView();
   }

   @Override
   int copyIntoArray(Object[] var1, int var2) {
      return this.elements.copyIntoArray(var1, var2);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Set)) {
         return false;
      } else {
         Set var2 = (Set)var1;
         if (this.size() != var2.size()) {
            return false;
         } else if (this.isEmpty()) {
            return true;
         } else if (SortedIterables.hasSameComparator(this.comparator, var2)) {
            Iterator var3 = var2.iterator();

            try {
               for (Object var5 : this) {
                  Object var6 = var3.next();
                  if (var6 == null || this.unsafeCompare(var5, var6) != 0) {
                     return false;
                  }
               }

               return true;
            } catch (ClassCastException var7) {
               return false;
            } catch (NoSuchElementException var8) {
               return false;
            }
         } else {
            return this.containsAll(var2);
         }
      }
   }

   @Override
   public E first() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.elements.get(0);
      }
   }

   @Override
   public E last() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.elements.get(this.size() - 1);
      }
   }

   @Override
   public E lower(E var1) {
      int var2 = this.headIndex((E)var1, false) - 1;
      return var2 == -1 ? null : this.elements.get(var2);
   }

   @Override
   public E floor(E var1) {
      int var2 = this.headIndex((E)var1, true) - 1;
      return var2 == -1 ? null : this.elements.get(var2);
   }

   @Override
   public E ceiling(E var1) {
      int var2 = this.tailIndex((E)var1, true);
      return var2 == this.size() ? null : this.elements.get(var2);
   }

   @Override
   public E higher(E var1) {
      int var2 = this.tailIndex((E)var1, false);
      return var2 == this.size() ? null : this.elements.get(var2);
   }

   @Override
   ImmutableSortedSet<E> headSetImpl(E var1, boolean var2) {
      return this.getSubSet(0, this.headIndex((E)var1, var2));
   }

   int headIndex(E var1, boolean var2) {
      int var3 = Collections.binarySearch(this.elements, Preconditions.checkNotNull((E)var1), this.comparator());
      if (var3 >= 0) {
         return var2 ? var3 + 1 : var3;
      } else {
         return ~var3;
      }
   }

   @Override
   ImmutableSortedSet<E> subSetImpl(E var1, boolean var2, E var3, boolean var4) {
      return this.tailSetImpl((E)var1, var2).headSetImpl((E)var3, var4);
   }

   @Override
   ImmutableSortedSet<E> tailSetImpl(E var1, boolean var2) {
      return this.getSubSet(this.tailIndex((E)var1, var2), this.size());
   }

   int tailIndex(E var1, boolean var2) {
      int var3 = Collections.binarySearch(this.elements, Preconditions.checkNotNull((E)var1), this.comparator());
      if (var3 >= 0) {
         return var2 ? var3 : var3 + 1;
      } else {
         return ~var3;
      }
   }

   Comparator<Object> unsafeComparator() {
      return this.comparator;
   }

   RegularImmutableSortedSet<E> getSubSet(int var1, int var2) {
      if (var1 == 0 && var2 == this.size()) {
         return this;
      } else {
         return var1 < var2 ? new RegularImmutableSortedSet<>(this.elements.subList(var1, var2), this.comparator) : emptySet(this.comparator);
      }
   }

   @Override
   int indexOf(Object var1) {
      if (var1 == null) {
         return -1;
      } else {
         int var2;
         try {
            var2 = Collections.binarySearch(this.elements, (E)var1, this.unsafeComparator());
         } catch (ClassCastException var4) {
            return -1;
         }

         return var2 >= 0 ? var2 : -1;
      }
   }

   @Override
   ImmutableList<E> createAsList() {
      return (ImmutableList<E>)(this.size() <= 1 ? this.elements : new ImmutableSortedAsList<>(this, this.elements));
   }

   @Override
   ImmutableSortedSet<E> createDescendingSet() {
      Comparator var1 = Collections.reverseOrder(this.comparator);
      return this.isEmpty() ? emptySet(var1) : new RegularImmutableSortedSet<>(this.elements.reverse(), var1);
   }
}
