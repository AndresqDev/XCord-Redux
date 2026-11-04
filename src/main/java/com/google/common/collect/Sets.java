package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.math.IntMath;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.DoNotCall;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

@ElementTypesAreNonnullByDefault
@GwtCompatible(
   emulated = true
)
public final class Sets {
   private Sets() {
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(E var0, E... var1) {
      return ImmutableEnumSet.asImmutable(EnumSet.of((E)var0, (E[])var1));
   }

   @GwtCompatible(
      serializable = true
   )
   public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(Iterable<E> var0) {
      if (var0 instanceof ImmutableEnumSet) {
         return (ImmutableEnumSet)var0;
      } else if (var0 instanceof Collection) {
         Collection var3 = (Collection)var0;
         return var3.isEmpty() ? ImmutableSet.of() : ImmutableEnumSet.asImmutable(EnumSet.copyOf(var3));
      } else {
         Iterator var1 = var0.iterator();
         if (var1.hasNext()) {
            EnumSet var2 = EnumSet.of((E)var1.next());
            Iterators.addAll(var2, var1);
            return ImmutableEnumSet.asImmutable(var2);
         } else {
            return ImmutableSet.of();
         }
      }
   }

   public static <E extends Enum<E>> Collector<E, ?, ImmutableSet<E>> toImmutableEnumSet() {
      return CollectCollectors.toImmutableEnumSet();
   }

   public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> var0, Class<E> var1) {
      EnumSet var2 = EnumSet.noneOf(var1);
      Iterables.addAll(var2, var0);
      return var2;
   }

   public static <E> HashSet<E> newHashSet() {
      return new HashSet<>();
   }

   public static <E> HashSet<E> newHashSet(E... var0) {
      HashSet var1 = newHashSetWithExpectedSize(var0.length);
      Collections.addAll(var1, (E[])var0);
      return var1;
   }

   public static <E> HashSet<E> newHashSet(Iterable<? extends E> var0) {
      return var0 instanceof Collection ? new HashSet<>((Collection<? extends E>)var0) : newHashSet(var0.iterator());
   }

   public static <E> HashSet<E> newHashSet(Iterator<? extends E> var0) {
      HashSet var1 = newHashSet();
      Iterators.addAll(var1, var0);
      return var1;
   }

   public static <E> HashSet<E> newHashSetWithExpectedSize(int var0) {
      return new HashSet<>(Maps.capacity(var0));
   }

   public static <E> Set<E> newConcurrentHashSet() {
      return Platform.newConcurrentHashSet();
   }

   public static <E> Set<E> newConcurrentHashSet(Iterable<? extends E> var0) {
      Set var1 = newConcurrentHashSet();
      Iterables.addAll(var1, var0);
      return var1;
   }

   public static <E> LinkedHashSet<E> newLinkedHashSet() {
      return new LinkedHashSet<>();
   }

   public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> var0) {
      if (var0 instanceof Collection) {
         return new LinkedHashSet<>((Collection<? extends E>)var0);
      } else {
         LinkedHashSet var1 = newLinkedHashSet();
         Iterables.addAll(var1, var0);
         return var1;
      }
   }

   public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int var0) {
      return new LinkedHashSet<>(Maps.capacity(var0));
   }

   public static <E extends Comparable> TreeSet<E> newTreeSet() {
      return new TreeSet<>();
   }

   public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> var0) {
      TreeSet var1 = newTreeSet();
      Iterables.addAll(var1, var0);
      return var1;
   }

   public static <E> TreeSet<E> newTreeSet(Comparator<? super E> var0) {
      return new TreeSet<>(Preconditions.checkNotNull(var0));
   }

   public static <E> Set<E> newIdentityHashSet() {
      return Collections.newSetFromMap(Maps.newIdentityHashMap());
   }

   @GwtIncompatible
   public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
      return new CopyOnWriteArraySet<>();
   }

   @GwtIncompatible
   public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> var0) {
      Object var1 = var0 instanceof Collection ? (Collection)var0 : Lists.newArrayList(var0);
      return new CopyOnWriteArraySet<>((Collection<? extends E>)var1);
   }

   public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> var0) {
      if (var0 instanceof EnumSet) {
         return EnumSet.complementOf((EnumSet<E>)var0);
      } else {
         Preconditions.checkArgument(!var0.isEmpty(), "collection is empty; use the other version of this method");
         Class var1 = ((Enum)var0.iterator().next()).getDeclaringClass();
         return makeComplementByHand(var0, var1);
      }
   }

   public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> var0, Class<E> var1) {
      Preconditions.checkNotNull(var0);
      return var0 instanceof EnumSet ? EnumSet.complementOf((EnumSet<E>)var0) : makeComplementByHand(var0, var1);
   }

   private static <E extends Enum<E>> EnumSet<E> makeComplementByHand(Collection<E> var0, Class<E> var1) {
      EnumSet var2 = EnumSet.allOf(var1);
      var2.removeAll(var0);
      return var2;
   }

   @Deprecated
   public static <E> Set<E> newSetFromMap(Map<E, Boolean> var0) {
      return Collections.newSetFromMap(var0);
   }

   public static <E> Sets.SetView<E> union(final Set<? extends E> var0, final Set<? extends E> var1) {
      Preconditions.checkNotNull(var0, "set1");
      Preconditions.checkNotNull(var1, "set2");
      return new Sets.SetView<E>() {
         @Override
         public int size() {
            int var1x = var0.size();

            for (Object var3 : var1) {
               if (!var0.contains(var3)) {
                  var1x++;
               }
            }

            return var1x;
         }

         @Override
         public boolean isEmpty() {
            return var0.isEmpty() && var1.isEmpty();
         }

         @Override
         public UnmodifiableIterator<E> iterator() {
            return new AbstractIterator<E>() {
               final Iterator<? extends E> itr1 = var0.iterator();
               final Iterator<? extends E> itr2 = var1.iterator();

               @Override
               protected E computeNext() {
                  if (this.itr1.hasNext()) {
                     return (E)this.itr1.next();
                  } else {
                     while (this.itr2.hasNext()) {
                        Object var1x = this.itr2.next();
                        if (!var0.contains(var1x)) {
                           return (E)var1x;
                        }
                     }

                     return (E)this.endOfData();
                  }
               }
            };
         }

         @Override
         public Stream<E> stream() {
            return Stream.concat(var0.stream(), var1.stream().filter(var1xx -> !var0.contains(var1xx)));
         }

         @Override
         public Stream<E> parallelStream() {
            return (Stream<E>)this.stream().parallel();
         }

         @Override
         public boolean contains(Object var1x) {
            return var0.contains(var1x) || var1.contains(var1x);
         }

         @Override
         public <S extends Set<E>> S copyInto(S var1x) {
            var1x.addAll(var0);
            var1x.addAll(var1);
            return (S)var1x;
         }

         @Override
         public ImmutableSet<E> immutableCopy() {
            return new ImmutableSet.Builder<E>().addAll(var0).addAll(var1).build();
         }
      };
   }

   public static <E> Sets.SetView<E> intersection(final Set<E> var0, final Set<?> var1) {
      Preconditions.checkNotNull(var0, "set1");
      Preconditions.checkNotNull(var1, "set2");
      return new Sets.SetView<E>() {
         @Override
         public UnmodifiableIterator<E> iterator() {
            return new AbstractIterator<E>() {
               final Iterator<E> itr = var0.iterator();

               @Override
               protected E computeNext() {
                  while (this.itr.hasNext()) {
                     Object var1x = this.itr.next();
                     if (var1.contains(var1x)) {
                        return (E)var1x;
                     }
                  }

                  return (E)this.endOfData();
               }
            };
         }

         @Override
         public Stream<E> stream() {
            return var0.stream().filter(var1::contains);
         }

         @Override
         public Stream<E> parallelStream() {
            return var0.parallelStream().filter(var1::contains);
         }

         @Override
         public int size() {
            int var1x = 0;

            for (Object var3 : var0) {
               if (var1.contains(var3)) {
                  var1x++;
               }
            }

            return var1x;
         }

         @Override
         public boolean isEmpty() {
            return Collections.disjoint(var1, var0);
         }

         @Override
         public boolean contains(Object var1x) {
            return var0.contains(var1x) && var1.contains(var1x);
         }

         @Override
         public boolean containsAll(Collection<?> var1x) {
            return var0.containsAll(var1x) && var1.containsAll(var1x);
         }
      };
   }

   public static <E> Sets.SetView<E> difference(final Set<E> var0, final Set<?> var1) {
      Preconditions.checkNotNull(var0, "set1");
      Preconditions.checkNotNull(var1, "set2");
      return new Sets.SetView<E>() {
         @Override
         public UnmodifiableIterator<E> iterator() {
            return new AbstractIterator<E>() {
               final Iterator<E> itr = var0.iterator();

               @Override
               protected E computeNext() {
                  while (this.itr.hasNext()) {
                     Object var1x = this.itr.next();
                     if (!var1.contains(var1x)) {
                        return (E)var1x;
                     }
                  }

                  return (E)this.endOfData();
               }
            };
         }

         @Override
         public Stream<E> stream() {
            return var0.stream().filter(var1xx -> !var1.contains(var1xx));
         }

         @Override
         public Stream<E> parallelStream() {
            return var0.parallelStream().filter(var1xx -> !var1.contains(var1xx));
         }

         @Override
         public int size() {
            int var1x = 0;

            for (Object var3 : var0) {
               if (!var1.contains(var3)) {
                  var1x++;
               }
            }

            return var1x;
         }

         @Override
         public boolean isEmpty() {
            return var1.containsAll(var0);
         }

         @Override
         public boolean contains(Object var1x) {
            return var0.contains(var1x) && !var1.contains(var1x);
         }
      };
   }

   public static <E> Sets.SetView<E> symmetricDifference(final Set<? extends E> var0, final Set<? extends E> var1) {
      Preconditions.checkNotNull(var0, "set1");
      Preconditions.checkNotNull(var1, "set2");
      return new Sets.SetView<E>() {
         @Override
         public UnmodifiableIterator<E> iterator() {
            final Iterator var1x = var0.iterator();
            final Iterator var2 = var1.iterator();
            return new AbstractIterator<E>() {
               @Override
               public E computeNext() {
                  while (var1x.hasNext()) {
                     Object var2x = var1x.next();
                     if (!var1.contains(var2x)) {
                        return (E)var2x;
                     }
                  }

                  while (var2.hasNext()) {
                     Object var1xx = var2.next();
                     if (!var0.contains(var1xx)) {
                        return (E)var1xx;
                     }
                  }

                  return (E)this.endOfData();
               }
            };
         }

         @Override
         public int size() {
            int var1x = 0;

            for (Object var3 : var0) {
               if (!var1.contains(var3)) {
                  var1x++;
               }
            }

            for (Object var5 : var1) {
               if (!var0.contains(var5)) {
                  var1x++;
               }
            }

            return var1x;
         }

         @Override
         public boolean isEmpty() {
            return var0.equals(var1);
         }

         @Override
         public boolean contains(Object var1x) {
            return var0.contains(var1x) ^ var1.contains(var1x);
         }
      };
   }

   public static <E> Set<E> filter(Set<E> var0, Predicate<? super E> var1) {
      if (var0 instanceof SortedSet) {
         return filter((SortedSet<E>)var0, var1);
      } else if (var0 instanceof Sets.FilteredSet) {
         Sets.FilteredSet var2 = (Sets.FilteredSet)var0;
         Predicate var3 = Predicates.and(var2.predicate, var1);
         return new Sets.FilteredSet<>((Set<E>)var2.unfiltered, var3);
      } else {
         return new Sets.FilteredSet<>(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1));
      }
   }

   public static <E> SortedSet<E> filter(SortedSet<E> var0, Predicate<? super E> var1) {
      if (var0 instanceof Sets.FilteredSet) {
         Sets.FilteredSet var2 = (Sets.FilteredSet)var0;
         Predicate var3 = Predicates.and(var2.predicate, var1);
         return new Sets.FilteredSortedSet<>((SortedSet<E>)var2.unfiltered, var3);
      } else {
         return new Sets.FilteredSortedSet<>(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1));
      }
   }

   @GwtIncompatible
   public static <E> NavigableSet<E> filter(NavigableSet<E> var0, Predicate<? super E> var1) {
      if (var0 instanceof Sets.FilteredSet) {
         Sets.FilteredSet var2 = (Sets.FilteredSet)var0;
         Predicate var3 = Predicates.and(var2.predicate, var1);
         return new Sets.FilteredNavigableSet<>((NavigableSet<E>)var2.unfiltered, var3);
      } else {
         return new Sets.FilteredNavigableSet<>(Preconditions.checkNotNull(var0), Preconditions.checkNotNull(var1));
      }
   }

   public static <B> Set<List<B>> cartesianProduct(List<? extends Set<? extends B>> var0) {
      return Sets.CartesianSet.create(var0);
   }

   @SafeVarargs
   public static <B> Set<List<B>> cartesianProduct(Set<? extends B>... var0) {
      return cartesianProduct(Arrays.asList(var0));
   }

   @GwtCompatible(
      serializable = false
   )
   public static <E> Set<Set<E>> powerSet(Set<E> var0) {
      return new Sets.PowerSet<>(var0);
   }

   @Beta
   public static <E> Set<Set<E>> combinations(Set<E> var0, final int var1) {
      final ImmutableMap var2 = Maps.indexMap(var0);
      CollectPreconditions.checkNonnegative(var1, "size");
      Preconditions.checkArgument(var1 <= var2.size(), "size (%s) must be <= set.size() (%s)", var1, var2.size());
      if (var1 == 0) {
         return ImmutableSet.of(ImmutableSet.of());
      } else {
         return (Set<Set<E>>)(var1 == var2.size()
            ? ImmutableSet.of(var2.keySet())
            : new AbstractSet<Set<E>>() {
               @Override
               public boolean contains(Object var1x) {
                  if (!(var1x instanceof Set)) {
                     return false;
                  } else {
                     Set var2x = (Set)var1x;
                     return var2x.size() == var1 && var2.keySet().containsAll(var2x);
                  }
               }

               @Override
               public Iterator<Set<E>> iterator() {
                  return new AbstractIterator<Set<E>>() {
                     final BitSet bits = new BitSet(var2.size());

                     protected Set<E> computeNext() {
                        if (this.bits.isEmpty()) {
                           this.bits.set(0, var1);
                        } else {
                           int var1x = this.bits.nextSetBit(0);
                           int var2x = this.bits.nextClearBit(var1x);
                           if (var2x == var2.size()) {
                              return this.endOfData();
                           }

                           this.bits.set(0, var2x - var1x - 1);
                           this.bits.clear(var2x - var1x - 1, var2x);
                           this.bits.set(var2x);
                        }

                        final BitSet var3 = (BitSet)this.bits.clone();
                        return new AbstractSet<E>() {
                           @Override
                           public boolean contains(Object var1x) {
                              Integer var2x = (Integer)var2.get(var1x);
                              return var2x != null && var3.get(var2x);
                           }

                           @Override
                           public Iterator<E> iterator() {
                              return new AbstractIterator<E>() {
                                 int i = -1;

                                 @Override
                                 protected E computeNext() {
                                    this.i = var3.nextSetBit(this.i + 1);
                                    return (E)(this.i == -1 ? this.endOfData() : var2.keySet().asList().get(this.i));
                                 }
                              };
                           }

                           @Override
                           public int size() {
                              return var1;
                           }
                        };
                     }
                  };
               }

               @Override
               public int size() {
                  return IntMath.binomial(var2.size(), var1);
               }

               @Override
               public String toString() {
                  String var1x = String.valueOf(var2.keySet());
                  int var2x = var1;
                  return new StringBuilder(32 + String.valueOf(var1x).length())
                     .append("Sets.combinations(")
                     .append(var1x)
                     .append(", ")
                     .append(var2x)
                     .append(")")
                     .toString();
               }
            });
      }
   }

   static int hashCodeImpl(Set<?> var0) {
      int var1 = 0;

      for (Object var3 : var0) {
         var1 += var3 != null ? var3.hashCode() : 0;
         var1 = ~(~var1);
      }

      return var1;
   }

   static boolean equalsImpl(Set<?> var0, Object var1) {
      if (var0 == var1) {
         return true;
      } else if (var1 instanceof Set) {
         Set var2 = (Set)var1;

         try {
            return var0.size() == var2.size() && var0.containsAll(var2);
         } catch (ClassCastException | NullPointerException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> var0) {
      return (NavigableSet<E>)(!(var0 instanceof ImmutableCollection) && !(var0 instanceof Sets.UnmodifiableNavigableSet)
         ? new Sets.UnmodifiableNavigableSet<>(var0)
         : var0);
   }

   @GwtIncompatible
   public static <E> NavigableSet<E> synchronizedNavigableSet(NavigableSet<E> var0) {
      return Synchronized.navigableSet(var0);
   }

   static boolean removeAllImpl(Set<?> var0, Iterator<?> var1) {
      boolean var2 = false;

      while (var1.hasNext()) {
         var2 |= var0.remove(var1.next());
      }

      return var2;
   }

   static boolean removeAllImpl(Set<?> var0, Collection<?> var1) {
      Preconditions.checkNotNull(var1);
      if (var1 instanceof Multiset) {
         var1 = ((Multiset)var1).elementSet();
      }

      return var1 instanceof Set && var1.size() > var0.size()
         ? Iterators.removeAll(var0.iterator(), (Collection<?>)var1)
         : removeAllImpl(var0, var1.iterator());
   }

   @Beta
   @GwtIncompatible
   public static <K extends Comparable<? super K>> NavigableSet<K> subSet(NavigableSet<K> var0, Range<K> var1) {
      if (var0.comparator() != null && var0.comparator() != Ordering.natural() && var1.hasLowerBound() && var1.hasUpperBound()) {
         Preconditions.checkArgument(
            var0.comparator().compare(var1.lowerEndpoint(), var1.upperEndpoint()) <= 0,
            "set is using a custom comparator which is inconsistent with the natural ordering."
         );
      }

      if (var1.hasLowerBound() && var1.hasUpperBound()) {
         return (NavigableSet<K>)var0.subSet(
            var1.lowerEndpoint(), var1.lowerBoundType() == BoundType.CLOSED, var1.upperEndpoint(), var1.upperBoundType() == BoundType.CLOSED
         );
      } else if (var1.hasLowerBound()) {
         return (NavigableSet<K>)var0.tailSet(var1.lowerEndpoint(), var1.lowerBoundType() == BoundType.CLOSED);
      } else {
         return (NavigableSet<K>)(var1.hasUpperBound()
            ? var0.headSet(var1.upperEndpoint(), var1.upperBoundType() == BoundType.CLOSED)
            : Preconditions.checkNotNull(var0));
      }
   }

   private static final class CartesianSet<E> extends ForwardingCollection<List<E>> implements Set<List<E>> {
      private final transient ImmutableList<ImmutableSet<E>> axes;
      private final transient CartesianList<E> delegate;

      static <E> Set<List<E>> create(List<? extends Set<? extends E>> var0) {
         ImmutableList.Builder var1 = new ImmutableList.Builder(var0.size());

         for (Set var3 : var0) {
            ImmutableSet var4 = ImmutableSet.copyOf(var3);
            if (var4.isEmpty()) {
               return ImmutableSet.of();
            }

            var1.add(var4);
         }

         final ImmutableList var5 = var1.build();
         ImmutableList var6 = new ImmutableList<List<E>>() {
            @Override
            public int size() {
               return var5.size();
            }

            public List<E> get(int var1) {
               return ((ImmutableSet)var5.get(var1)).asList();
            }

            @Override
            boolean isPartialView() {
               return true;
            }
         };
         return new Sets.CartesianSet<>(var5, new CartesianList<>(var6));
      }

      private CartesianSet(ImmutableList<ImmutableSet<E>> var1, CartesianList<E> var2) {
         this.axes = var1;
         this.delegate = var2;
      }

      @Override
      protected Collection<List<E>> delegate() {
         return this.delegate;
      }

      @Override
      public boolean contains(Object var1) {
         if (!(var1 instanceof List)) {
            return false;
         } else {
            List var2 = (List)var1;
            if (var2.size() != this.axes.size()) {
               return false;
            } else {
               int var3 = 0;

               for (Object var5 : var2) {
                  if (!this.axes.get(var3).contains(var5)) {
                     return false;
                  }

                  var3++;
               }

               return true;
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Sets.CartesianSet) {
            Sets.CartesianSet var2 = (Sets.CartesianSet)var1;
            return this.axes.equals(var2.axes);
         } else {
            return super.equals(var1);
         }
      }

      @Override
      public int hashCode() {
         int var1 = this.size() - 1;

         for (int var2 = 0; var2 < this.axes.size(); var2++) {
            var1 *= 31;
            var1 = ~(~var1);
         }

         int var6 = 1;

         for (Set var4 : this.axes) {
            var6 = 31 * var6 + this.size() / var4.size() * var4.hashCode();
            var6 = ~(~var6);
         }

         var6 += var1;
         return ~(~var6);
      }
   }

   @GwtIncompatible
   static class DescendingSet<E> extends ForwardingNavigableSet<E> {
      private final NavigableSet<E> forward;

      DescendingSet(NavigableSet<E> var1) {
         this.forward = var1;
      }

      @Override
      protected NavigableSet<E> delegate() {
         return this.forward;
      }

      @Override
      public E lower(@ParametricNullness E var1) {
         return this.forward.higher((E)var1);
      }

      @Override
      public E floor(@ParametricNullness E var1) {
         return this.forward.ceiling((E)var1);
      }

      @Override
      public E ceiling(@ParametricNullness E var1) {
         return this.forward.floor((E)var1);
      }

      @Override
      public E higher(@ParametricNullness E var1) {
         return this.forward.lower((E)var1);
      }

      @Override
      public E pollFirst() {
         return this.forward.pollLast();
      }

      @Override
      public E pollLast() {
         return this.forward.pollFirst();
      }

      @Override
      public NavigableSet<E> descendingSet() {
         return this.forward;
      }

      @Override
      public Iterator<E> descendingIterator() {
         return this.forward.iterator();
      }

      @Override
      public NavigableSet<E> subSet(@ParametricNullness E var1, boolean var2, @ParametricNullness E var3, boolean var4) {
         return this.forward.subSet((E)var3, var4, (E)var1, var2).descendingSet();
      }

      @Override
      public SortedSet<E> subSet(@ParametricNullness E var1, @ParametricNullness E var2) {
         return this.standardSubSet((E)var1, (E)var2);
      }

      @Override
      public NavigableSet<E> headSet(@ParametricNullness E var1, boolean var2) {
         return this.forward.tailSet((E)var1, var2).descendingSet();
      }

      @Override
      public SortedSet<E> headSet(@ParametricNullness E var1) {
         return this.standardHeadSet((E)var1);
      }

      @Override
      public NavigableSet<E> tailSet(@ParametricNullness E var1, boolean var2) {
         return this.forward.headSet((E)var1, var2).descendingSet();
      }

      @Override
      public SortedSet<E> tailSet(@ParametricNullness E var1) {
         return this.standardTailSet((E)var1);
      }

      @Override
      public Comparator<? super E> comparator() {
         Comparator var1 = this.forward.comparator();
         return var1 == null ? Ordering.natural().reverse() : reverse(var1);
      }

      private static <T> Ordering<T> reverse(Comparator<T> var0) {
         return Ordering.from(var0).reverse();
      }

      @ParametricNullness
      @Override
      public E first() {
         return this.forward.last();
      }

      @ParametricNullness
      @Override
      public E last() {
         return this.forward.first();
      }

      @Override
      public Iterator<E> iterator() {
         return this.forward.descendingIterator();
      }

      @Override
      public Object[] toArray() {
         return this.standardToArray();
      }

      @Override
      public <T> T[] toArray(T[] var1) {
         return (T[])this.standardToArray(var1);
      }

      @Override
      public String toString() {
         return this.standardToString();
      }
   }

   @GwtIncompatible
   private static class FilteredNavigableSet<E> extends Sets.FilteredSortedSet<E> implements NavigableSet<E> {
      FilteredNavigableSet(NavigableSet<E> var1, Predicate<? super E> var2) {
         super(var1, var2);
      }

      NavigableSet<E> unfiltered() {
         return (NavigableSet<E>)this.unfiltered;
      }

      @Override
      public E lower(@ParametricNullness E var1) {
         return Iterators.find(this.unfiltered().headSet((E)var1, false).descendingIterator(), this.predicate, null);
      }

      @Override
      public E floor(@ParametricNullness E var1) {
         return Iterators.find(this.unfiltered().headSet((E)var1, true).descendingIterator(), this.predicate, null);
      }

      @Override
      public E ceiling(@ParametricNullness E var1) {
         return Iterables.find(this.unfiltered().tailSet((E)var1, true), this.predicate, null);
      }

      @Override
      public E higher(@ParametricNullness E var1) {
         return Iterables.find(this.unfiltered().tailSet((E)var1, false), this.predicate, null);
      }

      @Override
      public E pollFirst() {
         return Iterables.removeFirstMatching(this.unfiltered(), this.predicate);
      }

      @Override
      public E pollLast() {
         return Iterables.removeFirstMatching(this.unfiltered().descendingSet(), this.predicate);
      }

      @Override
      public NavigableSet<E> descendingSet() {
         return Sets.filter(this.unfiltered().descendingSet(), this.predicate);
      }

      @Override
      public Iterator<E> descendingIterator() {
         return Iterators.filter(this.unfiltered().descendingIterator(), this.predicate);
      }

      @ParametricNullness
      @Override
      public E last() {
         return Iterators.find(this.unfiltered().descendingIterator(), this.predicate);
      }

      @Override
      public NavigableSet<E> subSet(@ParametricNullness E var1, boolean var2, @ParametricNullness E var3, boolean var4) {
         return Sets.filter(this.unfiltered().subSet((E)var1, var2, (E)var3, var4), this.predicate);
      }

      @Override
      public NavigableSet<E> headSet(@ParametricNullness E var1, boolean var2) {
         return Sets.filter(this.unfiltered().headSet((E)var1, var2), this.predicate);
      }

      @Override
      public NavigableSet<E> tailSet(@ParametricNullness E var1, boolean var2) {
         return Sets.filter(this.unfiltered().tailSet((E)var1, var2), this.predicate);
      }
   }

   private static class FilteredSet<E> extends Collections2.FilteredCollection<E> implements Set<E> {
      FilteredSet(Set<E> var1, Predicate<? super E> var2) {
         super(var1, var2);
      }

      @Override
      public boolean equals(Object var1) {
         return Sets.equalsImpl(this, var1);
      }

      @Override
      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }
   }

   private static class FilteredSortedSet<E> extends Sets.FilteredSet<E> implements SortedSet<E> {
      FilteredSortedSet(SortedSet<E> var1, Predicate<? super E> var2) {
         super(var1, var2);
      }

      @Override
      public Comparator<? super E> comparator() {
         return ((SortedSet)this.unfiltered).comparator();
      }

      @Override
      public SortedSet<E> subSet(@ParametricNullness E var1, @ParametricNullness E var2) {
         return new Sets.FilteredSortedSet<>(((SortedSet)this.unfiltered).subSet((E)var1, (E)var2), this.predicate);
      }

      @Override
      public SortedSet<E> headSet(@ParametricNullness E var1) {
         return new Sets.FilteredSortedSet<>(((SortedSet)this.unfiltered).headSet((E)var1), this.predicate);
      }

      @Override
      public SortedSet<E> tailSet(@ParametricNullness E var1) {
         return new Sets.FilteredSortedSet<>(((SortedSet)this.unfiltered).tailSet((E)var1), this.predicate);
      }

      @ParametricNullness
      @Override
      public E first() {
         return Iterators.find(this.unfiltered.iterator(), this.predicate);
      }

      @ParametricNullness
      @Override
      public E last() {
         SortedSet var1 = (SortedSet)this.unfiltered;

         while (true) {
            Object var2 = var1.last();
            if (this.predicate.apply((E)var2)) {
               return (E)var2;
            }

            var1 = var1.headSet(var2);
         }
      }
   }

   abstract static class ImprovedAbstractSet<E> extends AbstractSet<E> {
      @Override
      public boolean removeAll(Collection<?> var1) {
         return Sets.removeAllImpl(this, var1);
      }

      @Override
      public boolean retainAll(Collection<?> var1) {
         return super.retainAll(Preconditions.checkNotNull(var1));
      }
   }

   private static final class PowerSet<E> extends AbstractSet<Set<E>> {
      final ImmutableMap<E, Integer> inputSet;

      PowerSet(Set<E> var1) {
         Preconditions.checkArgument(var1.size() <= 30, "Too many elements to create power set: %s > 30", var1.size());
         this.inputSet = Maps.indexMap(var1);
      }

      @Override
      public int size() {
         return 1 << this.inputSet.size();
      }

      @Override
      public boolean isEmpty() {
         return false;
      }

      @Override
      public Iterator<Set<E>> iterator() {
         return new AbstractIndexedListIterator<Set<E>>(this.size()) {
            protected Set<E> get(int var1) {
               return new Sets.SubSet<>(PowerSet.this.inputSet, var1);
            }
         };
      }

      @Override
      public boolean contains(Object var1) {
         if (var1 instanceof Set) {
            Set var2 = (Set)var1;
            return this.inputSet.keySet().containsAll(var2);
         } else {
            return false;
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 instanceof Sets.PowerSet) {
            Sets.PowerSet var2 = (Sets.PowerSet)var1;
            return this.inputSet.keySet().equals(var2.inputSet.keySet());
         } else {
            return super.equals(var1);
         }
      }

      @Override
      public int hashCode() {
         return this.inputSet.keySet().hashCode() << this.inputSet.size() - 1;
      }

      @Override
      public String toString() {
         String var1 = String.valueOf(this.inputSet);
         return new StringBuilder(10 + String.valueOf(var1).length()).append("powerSet(").append(var1).append(")").toString();
      }
   }

   public abstract static class SetView<E> extends AbstractSet<E> {
      private SetView() {
      }

      public ImmutableSet<E> immutableCopy() {
         return ImmutableSet.copyOf(this);
      }

      @CanIgnoreReturnValue
      public <S extends Set<E>> S copyInto(S var1) {
         var1.addAll(this);
         return (S)var1;
      }

      @Deprecated
      @CanIgnoreReturnValue
      @DoNotCall("Always throws UnsupportedOperationException")
      @Override
      public final boolean add(@ParametricNullness E var1) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @CanIgnoreReturnValue
      @DoNotCall("Always throws UnsupportedOperationException")
      @Override
      public final boolean remove(Object var1) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @CanIgnoreReturnValue
      @DoNotCall("Always throws UnsupportedOperationException")
      @Override
      public final boolean addAll(Collection<? extends E> var1) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @CanIgnoreReturnValue
      @DoNotCall("Always throws UnsupportedOperationException")
      @Override
      public final boolean removeAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @CanIgnoreReturnValue
      @DoNotCall("Always throws UnsupportedOperationException")
      @Override
      public final boolean removeIf(java.util.function.Predicate<? super E> var1) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @CanIgnoreReturnValue
      @DoNotCall("Always throws UnsupportedOperationException")
      @Override
      public final boolean retainAll(Collection<?> var1) {
         throw new UnsupportedOperationException();
      }

      @Deprecated
      @DoNotCall("Always throws UnsupportedOperationException")
      @Override
      public final void clear() {
         throw new UnsupportedOperationException();
      }

      public abstract UnmodifiableIterator<E> iterator();
   }

   private static final class SubSet<E> extends AbstractSet<E> {
      private final ImmutableMap<E, Integer> inputSet;
      private final int mask;

      SubSet(ImmutableMap<E, Integer> var1, int var2) {
         this.inputSet = var1;
         this.mask = var2;
      }

      @Override
      public Iterator<E> iterator() {
         return new UnmodifiableIterator<E>() {
            final ImmutableList<E> elements = SubSet.this.inputSet.keySet().asList();
            int remainingSetBits = SubSet.this.mask;

            @Override
            public boolean hasNext() {
               return this.remainingSetBits != 0;
            }

            @Override
            public E next() {
               int var1 = Integer.numberOfTrailingZeros(this.remainingSetBits);
               if (var1 == 32) {
                  throw new NoSuchElementException();
               } else {
                  this.remainingSetBits &= ~(1 << var1);
                  return this.elements.get(var1);
               }
            }
         };
      }

      @Override
      public int size() {
         return Integer.bitCount(this.mask);
      }

      @Override
      public boolean contains(Object var1) {
         Integer var2 = this.inputSet.get(var1);
         return var2 != null && (this.mask & 1 << var2) != 0;
      }
   }

   static final class UnmodifiableNavigableSet<E> extends ForwardingSortedSet<E> implements Serializable, NavigableSet<E> {
      private final NavigableSet<E> delegate;
      private final SortedSet<E> unmodifiableDelegate;
      private transient Sets.UnmodifiableNavigableSet<E> descendingSet;
      private static final long serialVersionUID = 0L;

      UnmodifiableNavigableSet(NavigableSet<E> var1) {
         this.delegate = Preconditions.checkNotNull(var1);
         this.unmodifiableDelegate = Collections.unmodifiableSortedSet(var1);
      }

      @Override
      protected SortedSet<E> delegate() {
         return this.unmodifiableDelegate;
      }

      @Override
      public boolean removeIf(java.util.function.Predicate<? super E> var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Stream<E> stream() {
         return this.delegate.stream();
      }

      @Override
      public Stream<E> parallelStream() {
         return this.delegate.parallelStream();
      }

      @Override
      public void forEach(Consumer<? super E> var1) {
         this.delegate.forEach(var1);
      }

      @Override
      public E lower(@ParametricNullness E var1) {
         return this.delegate.lower((E)var1);
      }

      @Override
      public E floor(@ParametricNullness E var1) {
         return this.delegate.floor((E)var1);
      }

      @Override
      public E ceiling(@ParametricNullness E var1) {
         return this.delegate.ceiling((E)var1);
      }

      @Override
      public E higher(@ParametricNullness E var1) {
         return this.delegate.higher((E)var1);
      }

      @Override
      public E pollFirst() {
         throw new UnsupportedOperationException();
      }

      @Override
      public E pollLast() {
         throw new UnsupportedOperationException();
      }

      @Override
      public NavigableSet<E> descendingSet() {
         Sets.UnmodifiableNavigableSet var1 = this.descendingSet;
         if (var1 == null) {
            var1 = this.descendingSet = new Sets.UnmodifiableNavigableSet<>(this.delegate.descendingSet());
            var1.descendingSet = this;
         }

         return var1;
      }

      @Override
      public Iterator<E> descendingIterator() {
         return Iterators.unmodifiableIterator(this.delegate.descendingIterator());
      }

      @Override
      public NavigableSet<E> subSet(@ParametricNullness E var1, boolean var2, @ParametricNullness E var3, boolean var4) {
         return Sets.unmodifiableNavigableSet(this.delegate.subSet((E)var1, var2, (E)var3, var4));
      }

      @Override
      public NavigableSet<E> headSet(@ParametricNullness E var1, boolean var2) {
         return Sets.unmodifiableNavigableSet(this.delegate.headSet((E)var1, var2));
      }

      @Override
      public NavigableSet<E> tailSet(@ParametricNullness E var1, boolean var2) {
         return Sets.unmodifiableNavigableSet(this.delegate.tailSet((E)var1, var2));
      }
   }
}
