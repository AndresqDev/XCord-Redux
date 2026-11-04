package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class TreeMultiset<E> extends AbstractSortedMultiset<E> implements Serializable {
   private final transient TreeMultiset.Reference<TreeMultiset.AvlNode<E>> rootReference;
   private final transient GeneralRange<E> range;
   private final transient TreeMultiset.AvlNode<E> header;
   @GwtIncompatible
   private static final long serialVersionUID = 1L;

   public static <E extends Comparable> TreeMultiset<E> create() {
      return new TreeMultiset<>(Ordering.natural());
   }

   public static <E> TreeMultiset<E> create(Comparator<? super E> var0) {
      return var0 == null ? new TreeMultiset<>(Ordering.natural()) : new TreeMultiset<>(var0);
   }

   public static <E extends Comparable> TreeMultiset<E> create(Iterable<? extends E> var0) {
      TreeMultiset var1 = create();
      Iterables.addAll(var1, var0);
      return var1;
   }

   TreeMultiset(TreeMultiset.Reference<TreeMultiset.AvlNode<E>> var1, GeneralRange<E> var2, TreeMultiset.AvlNode<E> var3) {
      super(var2.comparator());
      this.rootReference = var1;
      this.range = var2;
      this.header = var3;
   }

   TreeMultiset(Comparator<? super E> var1) {
      super(var1);
      this.range = GeneralRange.all(var1);
      this.header = new TreeMultiset.AvlNode<>();
      successor(this.header, this.header);
      this.rootReference = new TreeMultiset.Reference<>();
   }

   private long aggregateForEntries(TreeMultiset.Aggregate var1) {
      TreeMultiset.AvlNode var2 = this.rootReference.get();
      long var3 = var1.treeAggregate(var2);
      if (this.range.hasLowerBound()) {
         var3 -= this.aggregateBelowRange(var1, var2);
      }

      if (this.range.hasUpperBound()) {
         var3 -= this.aggregateAboveRange(var1, var2);
      }

      return var3;
   }

   private long aggregateBelowRange(TreeMultiset.Aggregate var1, TreeMultiset.AvlNode<E> var2) {
      if (var2 == null) {
         return 0L;
      } else {
         int var3 = this.comparator().compare(NullnessCasts.uncheckedCastNullableTToT(this.range.getLowerEndpoint()), (E)var2.getElement());
         if (var3 < 0) {
            return this.aggregateBelowRange(var1, var2.left);
         } else if (var3 == 0) {
            switch (this.range.getLowerBoundType()) {
               case OPEN:
                  return (long)var1.nodeAggregate(var2) + var1.treeAggregate(var2.left);
               case CLOSED:
                  return var1.treeAggregate(var2.left);
               default:
                  throw new AssertionError();
            }
         } else {
            return var1.treeAggregate(var2.left) + (long)var1.nodeAggregate(var2) + this.aggregateBelowRange(var1, var2.right);
         }
      }
   }

   private long aggregateAboveRange(TreeMultiset.Aggregate var1, TreeMultiset.AvlNode<E> var2) {
      if (var2 == null) {
         return 0L;
      } else {
         int var3 = this.comparator().compare(NullnessCasts.uncheckedCastNullableTToT(this.range.getUpperEndpoint()), (E)var2.getElement());
         if (var3 > 0) {
            return this.aggregateAboveRange(var1, var2.right);
         } else if (var3 == 0) {
            switch (this.range.getUpperBoundType()) {
               case OPEN:
                  return (long)var1.nodeAggregate(var2) + var1.treeAggregate(var2.right);
               case CLOSED:
                  return var1.treeAggregate(var2.right);
               default:
                  throw new AssertionError();
            }
         } else {
            return var1.treeAggregate(var2.right) + (long)var1.nodeAggregate(var2) + this.aggregateAboveRange(var1, var2.left);
         }
      }
   }

   @Override
   public int size() {
      return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.SIZE));
   }

   @Override
   int distinctElements() {
      return Ints.saturatedCast(this.aggregateForEntries(TreeMultiset.Aggregate.DISTINCT));
   }

   static int distinctElements(TreeMultiset.AvlNode<?> var0) {
      return var0 == null ? 0 : var0.distinctElements;
   }

   @Override
   public int count(Object var1) {
      try {
         TreeMultiset.AvlNode var3 = this.rootReference.get();
         return this.range.contains((E)var1) && var3 != null ? var3.count(this.comparator(), var1) : 0;
      } catch (NullPointerException | ClassCastException var4) {
         return 0;
      }
   }

   @CanIgnoreReturnValue
   @Override
   public int add(@ParametricNullness E var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "occurrences");
      if (var2 == 0) {
         return this.count(var1);
      } else {
         Preconditions.checkArgument(this.range.contains((E)var1));
         TreeMultiset.AvlNode var3 = this.rootReference.get();
         if (var3 == null) {
            this.comparator().compare(var1, var1);
            TreeMultiset.AvlNode var6 = new TreeMultiset.AvlNode<>(var1, var2);
            successor(this.header, var6, this.header);
            this.rootReference.checkAndSet(var3, var6);
            return 0;
         } else {
            int[] var4 = new int[1];
            TreeMultiset.AvlNode var5 = var3.add(this.comparator(), var1, var2, var4);
            this.rootReference.checkAndSet(var3, var5);
            return var4[0];
         }
      }
   }

   @CanIgnoreReturnValue
   @Override
   public int remove(Object var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "occurrences");
      if (var2 == 0) {
         return this.count(var1);
      } else {
         TreeMultiset.AvlNode var3 = this.rootReference.get();
         int[] var4 = new int[1];

         TreeMultiset.AvlNode var5;
         try {
            if (!this.range.contains((E)var1) || var3 == null) {
               return 0;
            }

            var5 = var3.remove(this.comparator(), var1, var2, var4);
         } catch (NullPointerException | ClassCastException var7) {
            return 0;
         }

         this.rootReference.checkAndSet(var3, var5);
         return var4[0];
      }
   }

   @CanIgnoreReturnValue
   @Override
   public int setCount(@ParametricNullness E var1, int var2) {
      CollectPreconditions.checkNonnegative(var2, "count");
      if (!this.range.contains((E)var1)) {
         Preconditions.checkArgument(var2 == 0);
         return 0;
      } else {
         TreeMultiset.AvlNode var3 = this.rootReference.get();
         if (var3 == null) {
            if (var2 > 0) {
               this.add((E)var1, var2);
            }

            return 0;
         } else {
            int[] var4 = new int[1];
            TreeMultiset.AvlNode var5 = var3.setCount(this.comparator(), var1, var2, var4);
            this.rootReference.checkAndSet(var3, var5);
            return var4[0];
         }
      }
   }

   @CanIgnoreReturnValue
   @Override
   public boolean setCount(@ParametricNullness E var1, int var2, int var3) {
      CollectPreconditions.checkNonnegative(var3, "newCount");
      CollectPreconditions.checkNonnegative(var2, "oldCount");
      Preconditions.checkArgument(this.range.contains((E)var1));
      TreeMultiset.AvlNode var4 = this.rootReference.get();
      if (var4 == null) {
         if (var2 == 0) {
            if (var3 > 0) {
               this.add((E)var1, var3);
            }

            return true;
         } else {
            return false;
         }
      } else {
         int[] var5 = new int[1];
         TreeMultiset.AvlNode var6 = var4.setCount(this.comparator(), var1, var2, var3, var5);
         this.rootReference.checkAndSet(var4, var6);
         return var5[0] == var2;
      }
   }

   @Override
   public void clear() {
      if (!this.range.hasLowerBound() && !this.range.hasUpperBound()) {
         TreeMultiset.AvlNode var1 = this.header.succ();

         while (var1 != this.header) {
            TreeMultiset.AvlNode var2 = var1.succ();
            var1.elemCount = 0;
            var1.left = null;
            var1.right = null;
            var1.pred = null;
            var1.succ = null;
            var1 = var2;
         }

         successor(this.header, this.header);
         this.rootReference.clear();
      } else {
         Iterators.clear(this.entryIterator());
      }
   }

   private Multiset.Entry<E> wrapEntry(final TreeMultiset.AvlNode<E> var1) {
      return new Multisets.AbstractEntry<E>() {
         @ParametricNullness
         @Override
         public E getElement() {
            return (E)var1.getElement();
         }

         @Override
         public int getCount() {
            int var1x = var1.getCount();
            return var1x == 0 ? TreeMultiset.this.count(this.getElement()) : var1x;
         }
      };
   }

   private TreeMultiset.AvlNode<E> firstNode() {
      TreeMultiset.AvlNode var1 = this.rootReference.get();
      if (var1 == null) {
         return null;
      } else {
         TreeMultiset.AvlNode var2;
         if (this.range.hasLowerBound()) {
            Object var3 = NullnessCasts.uncheckedCastNullableTToT(this.range.getLowerEndpoint());
            var2 = var1.ceiling(this.comparator(), var3);
            if (var2 == null) {
               return null;
            }

            if (this.range.getLowerBoundType() == BoundType.OPEN && this.comparator().compare(var3, var2.getElement()) == 0) {
               var2 = var2.succ();
            }
         } else {
            var2 = this.header.succ();
         }

         return var2 != this.header && this.range.contains((E)var2.getElement()) ? var2 : null;
      }
   }

   private TreeMultiset.AvlNode<E> lastNode() {
      TreeMultiset.AvlNode var1 = this.rootReference.get();
      if (var1 == null) {
         return null;
      } else {
         TreeMultiset.AvlNode var2;
         if (this.range.hasUpperBound()) {
            Object var3 = NullnessCasts.uncheckedCastNullableTToT(this.range.getUpperEndpoint());
            var2 = var1.floor(this.comparator(), var3);
            if (var2 == null) {
               return null;
            }

            if (this.range.getUpperBoundType() == BoundType.OPEN && this.comparator().compare(var3, var2.getElement()) == 0) {
               var2 = var2.pred();
            }
         } else {
            var2 = this.header.pred();
         }

         return var2 != this.header && this.range.contains((E)var2.getElement()) ? var2 : null;
      }
   }

   @Override
   Iterator<E> elementIterator() {
      return Multisets.elementIterator(this.entryIterator());
   }

   @Override
   Iterator<Multiset.Entry<E>> entryIterator() {
      return new Iterator<Multiset.Entry<E>>() {
         TreeMultiset.AvlNode<E> current = TreeMultiset.this.firstNode();
         Multiset.Entry<E> prevEntry;

         @Override
         public boolean hasNext() {
            if (this.current == null) {
               return false;
            } else if (TreeMultiset.this.range.tooHigh(this.current.getElement())) {
               this.current = null;
               return false;
            } else {
               return true;
            }
         }

         public Multiset.Entry<E> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Multiset.Entry var1 = TreeMultiset.this.wrapEntry(Objects.requireNonNull(this.current));
               this.prevEntry = var1;
               if (this.current.succ() == TreeMultiset.this.header) {
                  this.current = null;
               } else {
                  this.current = this.current.succ();
               }

               return var1;
            }
         }

         @Override
         public void remove() {
            Preconditions.checkState(this.prevEntry != null, "no calls to next() since the last call to remove()");
            TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
            this.prevEntry = null;
         }
      };
   }

   @Override
   Iterator<Multiset.Entry<E>> descendingEntryIterator() {
      return new Iterator<Multiset.Entry<E>>() {
         TreeMultiset.AvlNode<E> current = TreeMultiset.this.lastNode();
         Multiset.Entry<E> prevEntry = null;

         @Override
         public boolean hasNext() {
            if (this.current == null) {
               return false;
            } else if (TreeMultiset.this.range.tooLow(this.current.getElement())) {
               this.current = null;
               return false;
            } else {
               return true;
            }
         }

         public Multiset.Entry<E> next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               Objects.requireNonNull(this.current);
               Multiset.Entry var1 = TreeMultiset.this.wrapEntry(this.current);
               this.prevEntry = var1;
               if (this.current.pred() == TreeMultiset.this.header) {
                  this.current = null;
               } else {
                  this.current = this.current.pred();
               }

               return var1;
            }
         }

         @Override
         public void remove() {
            Preconditions.checkState(this.prevEntry != null, "no calls to next() since the last call to remove()");
            TreeMultiset.this.setCount(this.prevEntry.getElement(), 0);
            this.prevEntry = null;
         }
      };
   }

   @Override
   public void forEachEntry(ObjIntConsumer<? super E> var1) {
      Preconditions.checkNotNull(var1);

      for (TreeMultiset.AvlNode var2 = this.firstNode(); var2 != this.header && var2 != null && !this.range.tooHigh((E)var2.getElement()); var2 = var2.succ()) {
         var1.accept(var2.getElement(), var2.getCount());
      }
   }

   @Override
   public Iterator<E> iterator() {
      return Multisets.iteratorImpl(this);
   }

   @Override
   public SortedMultiset<E> headMultiset(@ParametricNullness E var1, BoundType var2) {
      return new TreeMultiset<>(this.rootReference, this.range.intersect(GeneralRange.upTo(this.comparator(), (E)var1, var2)), this.header);
   }

   @Override
   public SortedMultiset<E> tailMultiset(@ParametricNullness E var1, BoundType var2) {
      return new TreeMultiset<>(this.rootReference, this.range.intersect(GeneralRange.downTo(this.comparator(), (E)var1, var2)), this.header);
   }

   private static <T> void successor(TreeMultiset.AvlNode<T> var0, TreeMultiset.AvlNode<T> var1) {
      var0.succ = var1;
      var1.pred = var0;
   }

   private static <T> void successor(TreeMultiset.AvlNode<T> var0, TreeMultiset.AvlNode<T> var1, TreeMultiset.AvlNode<T> var2) {
      successor(var0, var1);
      successor(var1, var2);
   }

   @GwtIncompatible
   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.defaultWriteObject();
      var1.writeObject(this.elementSet().comparator());
      Serialization.writeMultiset(this, var1);
   }

   @GwtIncompatible
   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      Comparator var2 = (Comparator)var1.readObject();
      Serialization.getFieldSetter(AbstractSortedMultiset.class, "comparator").set(this, var2);
      Serialization.getFieldSetter(TreeMultiset.class, "range").set(this, GeneralRange.all(var2));
      Serialization.getFieldSetter(TreeMultiset.class, "rootReference").set(this, new TreeMultiset.Reference());
      TreeMultiset.AvlNode var3 = new TreeMultiset.AvlNode();
      Serialization.getFieldSetter(TreeMultiset.class, "header").set(this, var3);
      successor(var3, var3);
      Serialization.populateMultiset(this, var1);
   }

   private static enum Aggregate {
      SIZE {
         @Override
         int nodeAggregate(TreeMultiset.AvlNode<?> var1) {
            return var1.elemCount;
         }

         @Override
         long treeAggregate(TreeMultiset.AvlNode<?> var1) {
            return var1 == null ? 0L : var1.totalCount;
         }
      },
      DISTINCT {
         @Override
         int nodeAggregate(TreeMultiset.AvlNode<?> var1) {
            return 1;
         }

         @Override
         long treeAggregate(TreeMultiset.AvlNode<?> var1) {
            return var1 == null ? 0L : (long)var1.distinctElements;
         }
      };

      private Aggregate() {
      }

      abstract int nodeAggregate(TreeMultiset.AvlNode<?> var1);

      abstract long treeAggregate(TreeMultiset.AvlNode<?> var1);
   }

   private static final class AvlNode<E> {
      private final E elem;
      private int elemCount;
      private int distinctElements;
      private long totalCount;
      private int height;
      private TreeMultiset.AvlNode<E> left;
      private TreeMultiset.AvlNode<E> right;
      private TreeMultiset.AvlNode<E> pred;
      private TreeMultiset.AvlNode<E> succ;

      AvlNode(@ParametricNullness E var1, int var2) {
         Preconditions.checkArgument(var2 > 0);
         this.elem = (E)var1;
         this.elemCount = var2;
         this.totalCount = (long)var2;
         this.distinctElements = 1;
         this.height = 1;
         this.left = null;
         this.right = null;
      }

      AvlNode() {
         this.elem = null;
         this.elemCount = 1;
      }

      private TreeMultiset.AvlNode<E> pred() {
         return Objects.requireNonNull(this.pred);
      }

      private TreeMultiset.AvlNode<E> succ() {
         return Objects.requireNonNull(this.succ);
      }

      int count(Comparator<? super E> var1, @ParametricNullness E var2) {
         int var3 = var1.compare(var2, this.getElement());
         if (var3 < 0) {
            return this.left == null ? 0 : this.left.count(var1, (E)var2);
         } else if (var3 > 0) {
            return this.right == null ? 0 : this.right.count(var1, (E)var2);
         } else {
            return this.elemCount;
         }
      }

      private TreeMultiset.AvlNode<E> addRightChild(@ParametricNullness E var1, int var2) {
         this.right = new TreeMultiset.AvlNode<>((E)var1, var2);
         TreeMultiset.successor(this, this.right, this.succ());
         this.height = Math.max(2, this.height);
         this.distinctElements++;
         this.totalCount += (long)var2;
         return this;
      }

      private TreeMultiset.AvlNode<E> addLeftChild(@ParametricNullness E var1, int var2) {
         this.left = new TreeMultiset.AvlNode<>((E)var1, var2);
         TreeMultiset.successor(this.pred(), this.left, this);
         this.height = Math.max(2, this.height);
         this.distinctElements++;
         this.totalCount += (long)var2;
         return this;
      }

      TreeMultiset.AvlNode<E> add(Comparator<? super E> var1, @ParametricNullness E var2, int var3, int[] var4) {
         int var5 = var1.compare(var2, this.getElement());
         if (var5 < 0) {
            TreeMultiset.AvlNode var9 = this.left;
            if (var9 == null) {
               var4[0] = 0;
               return this.addLeftChild((E)var2, var3);
            } else {
               int var10 = var9.height;
               this.left = (TreeMultiset.AvlNode<E>)var9.add(var1, var2, var3, var4);
               if (var4[0] == 0) {
                  this.distinctElements++;
               }

               this.totalCount += (long)var3;
               return this.left.height == var10 ? this : this.rebalance();
            }
         } else if (var5 > 0) {
            TreeMultiset.AvlNode var8 = this.right;
            if (var8 == null) {
               var4[0] = 0;
               return this.addRightChild((E)var2, var3);
            } else {
               int var7 = var8.height;
               this.right = (TreeMultiset.AvlNode<E>)var8.add(var1, var2, var3, var4);
               if (var4[0] == 0) {
                  this.distinctElements++;
               }

               this.totalCount += (long)var3;
               return this.right.height == var7 ? this : this.rebalance();
            }
         } else {
            var4[0] = this.elemCount;
            long var6 = (long)this.elemCount + (long)var3;
            Preconditions.checkArgument(var6 <= 2147483647L);
            this.elemCount += var3;
            this.totalCount += (long)var3;
            return this;
         }
      }

      TreeMultiset.AvlNode<E> remove(Comparator<? super E> var1, @ParametricNullness E var2, int var3, int[] var4) {
         int var5 = var1.compare(var2, this.getElement());
         if (var5 < 0) {
            TreeMultiset.AvlNode var7 = this.left;
            if (var7 == null) {
               var4[0] = 0;
               return this;
            } else {
               this.left = (TreeMultiset.AvlNode<E>)var7.remove(var1, var2, var3, var4);
               if (var4[0] > 0) {
                  if (var3 >= var4[0]) {
                     this.distinctElements--;
                     this.totalCount = this.totalCount - (long)var4[0];
                  } else {
                     this.totalCount -= (long)var3;
                  }
               }

               return var4[0] == 0 ? this : this.rebalance();
            }
         } else if (var5 > 0) {
            TreeMultiset.AvlNode var6 = this.right;
            if (var6 == null) {
               var4[0] = 0;
               return this;
            } else {
               this.right = (TreeMultiset.AvlNode<E>)var6.remove(var1, var2, var3, var4);
               if (var4[0] > 0) {
                  if (var3 >= var4[0]) {
                     this.distinctElements--;
                     this.totalCount = this.totalCount - (long)var4[0];
                  } else {
                     this.totalCount -= (long)var3;
                  }
               }

               return this.rebalance();
            }
         } else {
            var4[0] = this.elemCount;
            if (var3 >= this.elemCount) {
               return this.deleteMe();
            } else {
               this.elemCount -= var3;
               this.totalCount -= (long)var3;
               return this;
            }
         }
      }

      TreeMultiset.AvlNode<E> setCount(Comparator<? super E> var1, @ParametricNullness E var2, int var3, int[] var4) {
         int var5 = var1.compare(var2, this.getElement());
         if (var5 < 0) {
            TreeMultiset.AvlNode var7 = this.left;
            if (var7 == null) {
               var4[0] = 0;
               return var3 > 0 ? this.addLeftChild((E)var2, var3) : this;
            } else {
               this.left = (TreeMultiset.AvlNode<E>)var7.setCount(var1, var2, var3, var4);
               if (var3 == 0 && var4[0] != 0) {
                  this.distinctElements--;
               } else if (var3 > 0 && var4[0] == 0) {
                  this.distinctElements++;
               }

               this.totalCount = this.totalCount + (long)(var3 - var4[0]);
               return this.rebalance();
            }
         } else if (var5 > 0) {
            TreeMultiset.AvlNode var6 = this.right;
            if (var6 == null) {
               var4[0] = 0;
               return var3 > 0 ? this.addRightChild((E)var2, var3) : this;
            } else {
               this.right = (TreeMultiset.AvlNode<E>)var6.setCount(var1, var2, var3, var4);
               if (var3 == 0 && var4[0] != 0) {
                  this.distinctElements--;
               } else if (var3 > 0 && var4[0] == 0) {
                  this.distinctElements++;
               }

               this.totalCount = this.totalCount + (long)(var3 - var4[0]);
               return this.rebalance();
            }
         } else {
            var4[0] = this.elemCount;
            if (var3 == 0) {
               return this.deleteMe();
            } else {
               this.totalCount = this.totalCount + (long)(var3 - this.elemCount);
               this.elemCount = var3;
               return this;
            }
         }
      }

      TreeMultiset.AvlNode<E> setCount(Comparator<? super E> var1, @ParametricNullness E var2, int var3, int var4, int[] var5) {
         int var6 = var1.compare(var2, this.getElement());
         if (var6 < 0) {
            TreeMultiset.AvlNode var8 = this.left;
            if (var8 == null) {
               var5[0] = 0;
               return var3 == 0 && var4 > 0 ? this.addLeftChild((E)var2, var4) : this;
            } else {
               this.left = (TreeMultiset.AvlNode<E>)var8.setCount(var1, var2, var3, var4, var5);
               if (var5[0] == var3) {
                  if (var4 == 0 && var5[0] != 0) {
                     this.distinctElements--;
                  } else if (var4 > 0 && var5[0] == 0) {
                     this.distinctElements++;
                  }

                  this.totalCount = this.totalCount + (long)(var4 - var5[0]);
               }

               return this.rebalance();
            }
         } else if (var6 > 0) {
            TreeMultiset.AvlNode var7 = this.right;
            if (var7 == null) {
               var5[0] = 0;
               return var3 == 0 && var4 > 0 ? this.addRightChild((E)var2, var4) : this;
            } else {
               this.right = (TreeMultiset.AvlNode<E>)var7.setCount(var1, var2, var3, var4, var5);
               if (var5[0] == var3) {
                  if (var4 == 0 && var5[0] != 0) {
                     this.distinctElements--;
                  } else if (var4 > 0 && var5[0] == 0) {
                     this.distinctElements++;
                  }

                  this.totalCount = this.totalCount + (long)(var4 - var5[0]);
               }

               return this.rebalance();
            }
         } else {
            var5[0] = this.elemCount;
            if (var3 == this.elemCount) {
               if (var4 == 0) {
                  return this.deleteMe();
               }

               this.totalCount = this.totalCount + (long)(var4 - this.elemCount);
               this.elemCount = var4;
            }

            return this;
         }
      }

      private TreeMultiset.AvlNode<E> deleteMe() {
         int var1 = this.elemCount;
         this.elemCount = 0;
         TreeMultiset.successor(this.pred(), this.succ());
         if (this.left == null) {
            return this.right;
         } else if (this.right == null) {
            return this.left;
         } else if (this.left.height >= this.right.height) {
            TreeMultiset.AvlNode var3 = this.pred();
            var3.left = this.left.removeMax(var3);
            var3.right = this.right;
            var3.distinctElements = this.distinctElements - 1;
            var3.totalCount = this.totalCount - (long)var1;
            return var3.rebalance();
         } else {
            TreeMultiset.AvlNode var2 = this.succ();
            var2.right = this.right.removeMin(var2);
            var2.left = this.left;
            var2.distinctElements = this.distinctElements - 1;
            var2.totalCount = this.totalCount - (long)var1;
            return var2.rebalance();
         }
      }

      private TreeMultiset.AvlNode<E> removeMin(TreeMultiset.AvlNode<E> var1) {
         if (this.left == null) {
            return this.right;
         } else {
            this.left = this.left.removeMin(var1);
            this.distinctElements--;
            this.totalCount = this.totalCount - (long)var1.elemCount;
            return this.rebalance();
         }
      }

      private TreeMultiset.AvlNode<E> removeMax(TreeMultiset.AvlNode<E> var1) {
         if (this.right == null) {
            return this.left;
         } else {
            this.right = this.right.removeMax(var1);
            this.distinctElements--;
            this.totalCount = this.totalCount - (long)var1.elemCount;
            return this.rebalance();
         }
      }

      private void recomputeMultiset() {
         this.distinctElements = 1 + TreeMultiset.distinctElements(this.left) + TreeMultiset.distinctElements(this.right);
         this.totalCount = (long)this.elemCount + totalCount(this.left) + totalCount(this.right);
      }

      private void recomputeHeight() {
         this.height = 1 + Math.max(height(this.left), height(this.right));
      }

      private void recompute() {
         this.recomputeMultiset();
         this.recomputeHeight();
      }

      private TreeMultiset.AvlNode<E> rebalance() {
         switch (this.balanceFactor()) {
            case -2:
               Objects.requireNonNull(this.right);
               if (this.right.balanceFactor() > 0) {
                  this.right = this.right.rotateRight();
               }

               return this.rotateLeft();
            case 2:
               Objects.requireNonNull(this.left);
               if (this.left.balanceFactor() < 0) {
                  this.left = this.left.rotateLeft();
               }

               return this.rotateRight();
            default:
               this.recomputeHeight();
               return this;
         }
      }

      private int balanceFactor() {
         return height(this.left) - height(this.right);
      }

      private TreeMultiset.AvlNode<E> rotateLeft() {
         Preconditions.checkState(this.right != null);
         TreeMultiset.AvlNode var1 = this.right;
         this.right = var1.left;
         var1.left = this;
         var1.totalCount = this.totalCount;
         var1.distinctElements = this.distinctElements;
         this.recompute();
         var1.recomputeHeight();
         return var1;
      }

      private TreeMultiset.AvlNode<E> rotateRight() {
         Preconditions.checkState(this.left != null);
         TreeMultiset.AvlNode var1 = this.left;
         this.left = var1.right;
         var1.right = this;
         var1.totalCount = this.totalCount;
         var1.distinctElements = this.distinctElements;
         this.recompute();
         var1.recomputeHeight();
         return var1;
      }

      private static long totalCount(TreeMultiset.AvlNode<?> var0) {
         return var0 == null ? 0L : var0.totalCount;
      }

      private static int height(TreeMultiset.AvlNode<?> var0) {
         return var0 == null ? 0 : var0.height;
      }

      private TreeMultiset.AvlNode<E> ceiling(Comparator<? super E> var1, @ParametricNullness E var2) {
         int var3 = var1.compare(var2, this.getElement());
         if (var3 < 0) {
            return this.left == null ? this : MoreObjects.firstNonNull(this.left.ceiling(var1, (E)var2), this);
         } else if (var3 == 0) {
            return this;
         } else {
            return this.right == null ? null : this.right.ceiling(var1, (E)var2);
         }
      }

      private TreeMultiset.AvlNode<E> floor(Comparator<? super E> var1, @ParametricNullness E var2) {
         int var3 = var1.compare(var2, this.getElement());
         if (var3 > 0) {
            return this.right == null ? this : MoreObjects.firstNonNull(this.right.floor(var1, (E)var2), this);
         } else if (var3 == 0) {
            return this;
         } else {
            return this.left == null ? null : this.left.floor(var1, (E)var2);
         }
      }

      @ParametricNullness
      E getElement() {
         return NullnessCasts.uncheckedCastNullableTToT(this.elem);
      }

      int getCount() {
         return this.elemCount;
      }

      @Override
      public String toString() {
         return Multisets.immutableEntry(this.getElement(), this.getCount()).toString();
      }
   }

   private static final class Reference<T> {
      private T value;

      private Reference() {
      }

      public T get() {
         return this.value;
      }

      public void checkAndSet(T var1, T var2) {
         if (this.value != var1) {
            throw new ConcurrentModificationException();
         } else {
            this.value = (T)var2;
         }
      }

      void clear() {
         this.value = null;
      }
   }
}
