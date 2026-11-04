package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMultiset<E> extends ForwardingCollection<E> implements Multiset<E> {
   protected ForwardingMultiset() {
   }

   protected abstract Multiset<E> delegate();

   @Override
   public int count(Object var1) {
      return this.delegate().count(var1);
   }

   @CanIgnoreReturnValue
   @Override
   public int add(@ParametricNullness E var1, int var2) {
      return this.delegate().add((E)var1, var2);
   }

   @CanIgnoreReturnValue
   @Override
   public int remove(Object var1, int var2) {
      return this.delegate().remove(var1, var2);
   }

   @Override
   public Set<E> elementSet() {
      return this.delegate().elementSet();
   }

   @Override
   public Set<Multiset.Entry<E>> entrySet() {
      return this.delegate().entrySet();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || this.delegate().equals(var1);
   }

   @Override
   public int hashCode() {
      return this.delegate().hashCode();
   }

   @CanIgnoreReturnValue
   @Override
   public int setCount(@ParametricNullness E var1, int var2) {
      return this.delegate().setCount((E)var1, var2);
   }

   @CanIgnoreReturnValue
   @Override
   public boolean setCount(@ParametricNullness E var1, int var2, int var3) {
      return this.delegate().setCount((E)var1, var2, var3);
   }

   @Override
   protected boolean standardContains(Object var1) {
      return this.count(var1) > 0;
   }

   @Override
   protected void standardClear() {
      Iterators.clear(this.entrySet().iterator());
   }

   @Beta
   protected int standardCount(Object var1) {
      for (Multiset.Entry var3 : this.entrySet()) {
         if (Objects.equal(var3.getElement(), var1)) {
            return var3.getCount();
         }
      }

      return 0;
   }

   protected boolean standardAdd(@ParametricNullness E var1) {
      this.add((E)var1, 1);
      return true;
   }

   @Beta
   @Override
   protected boolean standardAddAll(Collection<? extends E> var1) {
      return Multisets.addAllImpl(this, var1);
   }

   @Override
   protected boolean standardRemove(Object var1) {
      return this.remove(var1, 1) > 0;
   }

   @Override
   protected boolean standardRemoveAll(Collection<?> var1) {
      return Multisets.removeAllImpl(this, var1);
   }

   @Override
   protected boolean standardRetainAll(Collection<?> var1) {
      return Multisets.retainAllImpl(this, var1);
   }

   protected int standardSetCount(@ParametricNullness E var1, int var2) {
      return Multisets.setCountImpl(this, (E)var1, var2);
   }

   protected boolean standardSetCount(@ParametricNullness E var1, int var2, int var3) {
      return Multisets.setCountImpl(this, (E)var1, var2, var3);
   }

   protected Iterator<E> standardIterator() {
      return Multisets.iteratorImpl(this);
   }

   protected int standardSize() {
      return Multisets.linearTimeSizeImpl(this);
   }

   protected boolean standardEquals(Object var1) {
      return Multisets.equalsImpl(this, var1);
   }

   protected int standardHashCode() {
      return this.entrySet().hashCode();
   }

   @Override
   protected String standardToString() {
      return this.entrySet().toString();
   }

   @Beta
   protected class StandardElementSet extends Multisets.ElementSet<E> {
      public StandardElementSet() {
      }

      @Override
      Multiset<E> multiset() {
         return ForwardingMultiset.this;
      }

      @Override
      public Iterator<E> iterator() {
         return Multisets.elementIterator(this.multiset().entrySet().iterator());
      }
   }
}
