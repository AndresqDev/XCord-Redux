package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   serializable = true,
   emulated = true
)
public abstract class ImmutableMultiset<E> extends ImmutableMultisetGwtSerializationDependencies<E> implements Multiset<E> {
   @LazyInit
   private transient ImmutableList<E> asList;
   @LazyInit
   private transient ImmutableSet<Multiset.Entry<E>> entrySet;

   public static <E> Collector<E, ?, ImmutableMultiset<E>> toImmutableMultiset() {
      return CollectCollectors.toImmutableMultiset(Function.identity(), var0 -> 1);
   }

   public static <T, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(Function<? super T, ? extends E> var0, ToIntFunction<? super T> var1) {
      return CollectCollectors.toImmutableMultiset(var0, var1);
   }

   public static <E> ImmutableMultiset<E> of() {
      return (ImmutableMultiset<E>)RegularImmutableMultiset.EMPTY;
   }

   public static <E> ImmutableMultiset<E> of(E var0) {
      return copyFromElements((E[])(new Object[]{var0}));
   }

   public static <E> ImmutableMultiset<E> of(E var0, E var1) {
      return copyFromElements((E[])(new Object[]{var0, var1}));
   }

   public static <E> ImmutableMultiset<E> of(E var0, E var1, E var2) {
      return copyFromElements((E[])(new Object[]{var0, var1, var2}));
   }

   public static <E> ImmutableMultiset<E> of(E var0, E var1, E var2, E var3) {
      return copyFromElements((E[])(new Object[]{var0, var1, var2, var3}));
   }

   public static <E> ImmutableMultiset<E> of(E var0, E var1, E var2, E var3, E var4) {
      return copyFromElements((E[])(new Object[]{var0, var1, var2, var3, var4}));
   }

   public static <E> ImmutableMultiset<E> of(E var0, E var1, E var2, E var3, E var4, E var5, E... var6) {
      return new ImmutableMultiset.Builder<E>().add((E)var0).add((E)var1).add((E)var2).add((E)var3).add((E)var4).add((E)var5).add((E[])var6).build();
   }

   public static <E> ImmutableMultiset<E> copyOf(E[] var0) {
      return copyFromElements((E[])var0);
   }

   public static <E> ImmutableMultiset<E> copyOf(Iterable<? extends E> var0) {
      if (var0 instanceof ImmutableMultiset) {
         ImmutableMultiset var1 = (ImmutableMultiset)var0;
         if (!var1.isPartialView()) {
            return var1;
         }
      }

      Object var2 = var0 instanceof Multiset ? Multisets.cast(var0) : LinkedHashMultiset.create(var0);
      return copyFromEntries(((Multiset)var2).entrySet());
   }

   public static <E> ImmutableMultiset<E> copyOf(Iterator<? extends E> var0) {
      LinkedHashMultiset var1 = LinkedHashMultiset.create();
      Iterators.addAll(var1, var0);
      return copyFromEntries(var1.entrySet());
   }

   private static <E> ImmutableMultiset<E> copyFromElements(E... var0) {
      LinkedHashMultiset var1 = LinkedHashMultiset.create();
      Collections.addAll(var1, (E[])var0);
      return copyFromEntries(var1.entrySet());
   }

   static <E> ImmutableMultiset<E> copyFromEntries(Collection<? extends Multiset.Entry<? extends E>> var0) {
      return var0.isEmpty() ? of() : RegularImmutableMultiset.create(var0);
   }

   ImmutableMultiset() {
   }

   @Override
   public UnmodifiableIterator<E> iterator() {
      final UnmodifiableIterator var1 = this.entrySet().iterator();
      return new UnmodifiableIterator<E>(this) {
         int remaining;
         E element;

         @Override
         public boolean hasNext() {
            return this.remaining > 0 || var1.hasNext();
         }

         @Override
         public E next() {
            if (this.remaining <= 0) {
               Multiset.Entry var1x = (Multiset.Entry)var1.next();
               this.element = (E)var1x.getElement();
               this.remaining = var1x.getCount();
            }

            this.remaining--;
            return Objects.requireNonNull(this.element);
         }
      };
   }

   @Override
   public ImmutableList<E> asList() {
      ImmutableList var1 = this.asList;
      return var1 == null ? (this.asList = super.asList()) : var1;
   }

   @Override
   public boolean contains(Object var1) {
      return this.count(var1) > 0;
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final int add(E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final int remove(Object var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final int setCount(E var1, int var2) {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   @CanIgnoreReturnValue
   @DoNotCall("Always throws UnsupportedOperationException")
   @Override
   public final boolean setCount(E var1, int var2, int var3) {
      throw new UnsupportedOperationException();
   }

   @GwtIncompatible
   @Override
   int copyIntoArray(Object[] var1, int var2) {
      for (Multiset.Entry var4 : this.entrySet()) {
         Arrays.fill(var1, var2, var2 + var4.getCount(), var4.getElement());
         var2 += var4.getCount();
      }

      return var2;
   }

   @Override
   public boolean equals(Object var1) {
      return Multisets.equalsImpl(this, var1);
   }

   @Override
   public int hashCode() {
      return Sets.hashCodeImpl(this.entrySet());
   }

   @Override
   public String toString() {
      return this.entrySet().toString();
   }

   public abstract ImmutableSet<E> elementSet();

   public ImmutableSet<Multiset.Entry<E>> entrySet() {
      ImmutableSet var1 = this.entrySet;
      return var1 == null ? (this.entrySet = this.createEntrySet()) : var1;
   }

   private ImmutableSet<Multiset.Entry<E>> createEntrySet() {
      return (ImmutableSet<Multiset.Entry<E>>)(this.isEmpty() ? ImmutableSet.of() : new ImmutableMultiset.EntrySet());
   }

   abstract Multiset.Entry<E> getEntry(int var1);

   @GwtIncompatible
   @Override
   Object writeReplace() {
      return new ImmutableMultiset.SerializedForm(this);
   }

   public static <E> ImmutableMultiset.Builder<E> builder() {
      return new ImmutableMultiset.Builder<>();
   }

   public static class Builder<E> extends ImmutableCollection.Builder<E> {
      final Multiset<E> contents;

      public Builder() {
         this(LinkedHashMultiset.create());
      }

      Builder(Multiset<E> var1) {
         this.contents = var1;
      }

      @CanIgnoreReturnValue
      public ImmutableMultiset.Builder<E> add(E var1) {
         this.contents.add(Preconditions.checkNotNull((E)var1));
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultiset.Builder<E> add(E... var1) {
         super.add((E[])var1);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultiset.Builder<E> addCopies(E var1, int var2) {
         this.contents.add(Preconditions.checkNotNull((E)var1), var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultiset.Builder<E> setCount(E var1, int var2) {
         this.contents.setCount(Preconditions.checkNotNull((E)var1), var2);
         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultiset.Builder<E> addAll(Iterable<? extends E> var1) {
         if (var1 instanceof Multiset) {
            Multiset var2 = Multisets.cast(var1);
            var2.forEachEntry((var1x, var2x) -> this.contents.add(Preconditions.checkNotNull((E)var1x), var2x));
         } else {
            super.addAll(var1);
         }

         return this;
      }

      @CanIgnoreReturnValue
      public ImmutableMultiset.Builder<E> addAll(Iterator<? extends E> var1) {
         super.addAll(var1);
         return this;
      }

      public ImmutableMultiset<E> build() {
         return ImmutableMultiset.copyOf(this.contents);
      }

      @VisibleForTesting
      ImmutableMultiset<E> buildJdkBacked() {
         return this.contents.isEmpty() ? ImmutableMultiset.of() : JdkBackedImmutableMultiset.create(this.contents.entrySet());
      }
   }

   static final class ElementSet<E> extends ImmutableSet.Indexed<E> {
      private final List<Multiset.Entry<E>> entries;
      private final Multiset<E> delegate;

      ElementSet(List<Multiset.Entry<E>> var1, Multiset<E> var2) {
         this.entries = var1;
         this.delegate = var2;
      }

      @Override
      E get(int var1) {
         return this.entries.get(var1).getElement();
      }

      @Override
      public boolean contains(Object var1) {
         return this.delegate.contains(var1);
      }

      @Override
      boolean isPartialView() {
         return true;
      }

      @Override
      public int size() {
         return this.entries.size();
      }
   }

   private final class EntrySet extends IndexedImmutableSet<Multiset.Entry<E>> {
      private static final long serialVersionUID = 0L;

      private EntrySet() {
      }

      @Override
      boolean isPartialView() {
         return ImmutableMultiset.this.isPartialView();
      }

      Multiset.Entry<E> get(int var1) {
         return ImmutableMultiset.this.getEntry(var1);
      }

      @Override
      public int size() {
         return ImmutableMultiset.this.elementSet().size();
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof Multiset.Entry) {
            Multiset.Entry var2 = (Multiset.Entry)var1;
            if (var2.getCount() <= 0) {
               return false;
            } else {
               int var3 = ImmutableMultiset.this.count(var2.getElement());
               return var3 == var2.getCount();
            }
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return ImmutableMultiset.this.hashCode();
      }

      @GwtIncompatible
      @Override
      Object writeReplace() {
         return new ImmutableMultiset.EntrySetSerializedForm<>(ImmutableMultiset.this);
      }
   }

   @GwtIncompatible
   static class EntrySetSerializedForm<E> implements Serializable {
      final ImmutableMultiset<E> multiset;

      EntrySetSerializedForm(ImmutableMultiset<E> var1) {
         this.multiset = var1;
      }

      Object readResolve() {
         return this.multiset.entrySet();
      }
   }

   static final class SerializedForm implements Serializable {
      final Object[] elements;
      final int[] counts;
      private static final long serialVersionUID = 0L;

      SerializedForm(Multiset<? extends Object> var1) {
         int var2 = var1.entrySet().size();
         this.elements = new Object[var2];
         this.counts = new int[var2];
         int var3 = 0;

         for (Multiset.Entry var5 : var1.entrySet()) {
            this.elements[var3] = var5.getElement();
            this.counts[var3] = var5.getCount();
            var3++;
         }
      }

      Object readResolve() {
         LinkedHashMultiset var1 = LinkedHashMultiset.create(this.elements.length);

         for (int var2 = 0; var2 < this.elements.length; var2++) {
            var1.add(this.elements[var2], this.counts[var2]);
         }

         return ImmutableMultiset.copyOf(var1);
      }
   }
}
