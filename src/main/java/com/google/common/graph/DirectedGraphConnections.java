package com.google.common.graph;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

@ElementTypesAreNonnullByDefault
final class DirectedGraphConnections<N, V> implements GraphConnections<N, V> {
   private static final Object PRED = new Object();
   private final Map<N, Object> adjacentNodeValues;
   private final List<DirectedGraphConnections.NodeConnection<N>> orderedNodeConnections;
   private int predecessorCount;
   private int successorCount;

   private DirectedGraphConnections(Map<N, Object> var1, List<DirectedGraphConnections.NodeConnection<N>> var2, int var3, int var4) {
      this.adjacentNodeValues = Preconditions.checkNotNull(var1);
      this.orderedNodeConnections = var2;
      this.predecessorCount = Graphs.checkNonNegative(var3);
      this.successorCount = Graphs.checkNonNegative(var4);
      Preconditions.checkState(var3 <= var1.size() && var4 <= var1.size());
   }

   static <N, V> DirectedGraphConnections<N, V> of(ElementOrder<N> var0) {
      byte var1 = 4;
      ArrayList var2;
      switch (var0.type()) {
         case UNORDERED:
            var2 = null;
            break;
         case STABLE:
            var2 = new ArrayList();
            break;
         default:
            throw new AssertionError(var0.type());
      }

      return new DirectedGraphConnections<>(new HashMap<>(var1, 1.0F), var2, 0, 0);
   }

   static <N, V> DirectedGraphConnections<N, V> ofImmutable(N var0, Iterable<EndpointPair<N>> var1, Function<N, V> var2) {
      Preconditions.checkNotNull(var0);
      Preconditions.checkNotNull(var2);
      HashMap var3 = new HashMap();
      ImmutableList.Builder var4 = ImmutableList.builder();
      int var5 = 0;
      int var6 = 0;

      for (EndpointPair var8 : var1) {
         if (var8.nodeU().equals(var0) && var8.nodeV().equals(var0)) {
            var3.put(var0, new DirectedGraphConnections.PredAndSucc(var2.apply(var0)));
            var4.add(new DirectedGraphConnections.NodeConnection.Pred<>(var0));
            var4.add(new DirectedGraphConnections.NodeConnection.Succ<>(var0));
            var5++;
            var6++;
         } else if (var8.nodeV().equals(var0)) {
            Object var9 = var8.nodeU();
            Object var10 = var3.put(var9, PRED);
            if (var10 != null) {
               var3.put(var9, new DirectedGraphConnections.PredAndSucc(var10));
            }

            var4.add(new DirectedGraphConnections.NodeConnection.Pred<>(var9));
            var5++;
         } else {
            Preconditions.checkArgument(var8.nodeU().equals(var0));
            Object var12 = var8.nodeV();
            Object var13 = var2.apply(var12);
            Object var11 = var3.put(var12, var13);
            if (var11 != null) {
               Preconditions.checkArgument(var11 == PRED);
               var3.put(var12, new DirectedGraphConnections.PredAndSucc(var13));
            }

            var4.add(new DirectedGraphConnections.NodeConnection.Succ<>(var12));
            var6++;
         }
      }

      return new DirectedGraphConnections<>(var3, var4.build(), var5, var6);
   }

   @Override
   public Set<N> adjacentNodes() {
      return (Set<N>)(this.orderedNodeConnections == null ? Collections.unmodifiableSet(this.adjacentNodeValues.keySet()) : new AbstractSet<N>() {
         public UnmodifiableIterator<N> iterator() {
            final Iterator var1 = DirectedGraphConnections.this.orderedNodeConnections.iterator();
            final HashSet var2 = new HashSet();
            return new AbstractIterator<N>(this) {
               @Override
               protected N computeNext() {
                  while (var1.hasNext()) {
                     DirectedGraphConnections.NodeConnection var1x = (DirectedGraphConnections.NodeConnection)var1.next();
                     boolean var2x = var2.add(var1x.node);
                     if (var2x) {
                        return var1x.node;
                     }
                  }

                  return (N)this.endOfData();
               }
            };
         }

         @Override
         public int size() {
            return DirectedGraphConnections.this.adjacentNodeValues.size();
         }

         @Override
         public boolean contains(Object var1) {
            return DirectedGraphConnections.this.adjacentNodeValues.containsKey(var1);
         }
      });
   }

   @Override
   public Set<N> predecessors() {
      return new AbstractSet<N>() {
         public UnmodifiableIterator<N> iterator() {
            if (DirectedGraphConnections.this.orderedNodeConnections == null) {
               final Iterator var2 = DirectedGraphConnections.this.adjacentNodeValues.entrySet().iterator();
               return new AbstractIterator<N>(this) {
                  @Override
                  protected N computeNext() {
                     while (var2.hasNext()) {
                        Entry var1 = (Entry)var2.next();
                        if (DirectedGraphConnections.isPredecessor(var1.getValue())) {
                           return (N)var1.getKey();
                        }
                     }

                     return (N)this.endOfData();
                  }
               };
            } else {
               final Iterator var1 = DirectedGraphConnections.this.orderedNodeConnections.iterator();
               return new AbstractIterator<N>(this) {
                  @Override
                  protected N computeNext() {
                     while (var1.hasNext()) {
                        DirectedGraphConnections.NodeConnection var1x = (DirectedGraphConnections.NodeConnection)var1.next();
                        if (var1x instanceof DirectedGraphConnections.NodeConnection.Pred) {
                           return var1x.node;
                        }
                     }

                     return (N)this.endOfData();
                  }
               };
            }
         }

         @Override
         public int size() {
            return DirectedGraphConnections.this.predecessorCount;
         }

         @Override
         public boolean contains(Object var1) {
            return DirectedGraphConnections.isPredecessor(DirectedGraphConnections.this.adjacentNodeValues.get(var1));
         }
      };
   }

   @Override
   public Set<N> successors() {
      return new AbstractSet<N>() {
         public UnmodifiableIterator<N> iterator() {
            if (DirectedGraphConnections.this.orderedNodeConnections == null) {
               final Iterator var2 = DirectedGraphConnections.this.adjacentNodeValues.entrySet().iterator();
               return new AbstractIterator<N>(this) {
                  @Override
                  protected N computeNext() {
                     while (var2.hasNext()) {
                        Entry var1 = (Entry)var2.next();
                        if (DirectedGraphConnections.isSuccessor(var1.getValue())) {
                           return (N)var1.getKey();
                        }
                     }

                     return (N)this.endOfData();
                  }
               };
            } else {
               final Iterator var1 = DirectedGraphConnections.this.orderedNodeConnections.iterator();
               return new AbstractIterator<N>(this) {
                  @Override
                  protected N computeNext() {
                     while (var1.hasNext()) {
                        DirectedGraphConnections.NodeConnection var1x = (DirectedGraphConnections.NodeConnection)var1.next();
                        if (var1x instanceof DirectedGraphConnections.NodeConnection.Succ) {
                           return var1x.node;
                        }
                     }

                     return (N)this.endOfData();
                  }
               };
            }
         }

         @Override
         public int size() {
            return DirectedGraphConnections.this.successorCount;
         }

         @Override
         public boolean contains(Object var1) {
            return DirectedGraphConnections.isSuccessor(DirectedGraphConnections.this.adjacentNodeValues.get(var1));
         }
      };
   }

   @Override
   public Iterator<EndpointPair<N>> incidentEdgeIterator(final N var1) {
      Preconditions.checkNotNull(var1);
      final Iterator var2;
      if (this.orderedNodeConnections == null) {
         var2 = Iterators.concat(Iterators.transform(this.predecessors().iterator(), new Function<N, EndpointPair<N>>(this) {
            public EndpointPair<N> apply(N var1x) {
               return EndpointPair.ordered((N)var1x, (N)var1);
            }
         }), Iterators.transform(this.successors().iterator(), new Function<N, EndpointPair<N>>(this) {
            public EndpointPair<N> apply(N var1x) {
               return EndpointPair.ordered((N)var1, (N)var1x);
            }
         }));
      } else {
         var2 = Iterators.transform(
            this.orderedNodeConnections.iterator(),
            new Function<DirectedGraphConnections.NodeConnection<N>, EndpointPair<N>>(this) {
               public EndpointPair<N> apply(DirectedGraphConnections.NodeConnection<N> var1x) {
                  return var1x instanceof DirectedGraphConnections.NodeConnection.Succ
                     ? EndpointPair.ordered((N)var1, var1x.node)
                     : EndpointPair.ordered(var1x.node, (N)var1);
               }
            }
         );
      }

      final AtomicBoolean var3 = new AtomicBoolean(false);
      return new AbstractIterator<EndpointPair<N>>(this) {
         protected EndpointPair<N> computeNext() {
            while (var2.hasNext()) {
               EndpointPair var1 = (EndpointPair)var2.next();
               if (var1.nodeU().equals(var1.nodeV())) {
                  if (var3.getAndSet(true)) {
                     continue;
                  }

                  return var1;
               }

               return var1;
            }

            return this.endOfData();
         }
      };
   }

   @Override
   public V value(N var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = this.adjacentNodeValues.get(var1);
      if (var2 == PRED) {
         return null;
      } else {
         return (V)(var2 instanceof DirectedGraphConnections.PredAndSucc ? ((DirectedGraphConnections.PredAndSucc)var2).successorValue : var2);
      }
   }

   @Override
   public void removePredecessor(N var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = this.adjacentNodeValues.get(var1);
      boolean var3;
      if (var2 == PRED) {
         this.adjacentNodeValues.remove(var1);
         var3 = true;
      } else if (var2 instanceof DirectedGraphConnections.PredAndSucc) {
         this.adjacentNodeValues.put((N)var1, ((DirectedGraphConnections.PredAndSucc)var2).successorValue);
         var3 = true;
      } else {
         var3 = false;
      }

      if (var3) {
         Graphs.checkNonNegative(--this.predecessorCount);
         if (this.orderedNodeConnections != null) {
            this.orderedNodeConnections.remove(new DirectedGraphConnections.NodeConnection.Pred<>(var1));
         }
      }
   }

   @Override
   public V removeSuccessor(Object var1) {
      Preconditions.checkNotNull(var1);
      Object var2 = this.adjacentNodeValues.get(var1);
      Object var3;
      if (var2 == null || var2 == PRED) {
         var3 = null;
      } else if (var2 instanceof DirectedGraphConnections.PredAndSucc) {
         this.adjacentNodeValues.put((N)var1, PRED);
         var3 = ((DirectedGraphConnections.PredAndSucc)var2).successorValue;
      } else {
         this.adjacentNodeValues.remove(var1);
         var3 = var2;
      }

      if (var3 != null) {
         Graphs.checkNonNegative(--this.successorCount);
         if (this.orderedNodeConnections != null) {
            this.orderedNodeConnections.remove(new DirectedGraphConnections.NodeConnection.Succ<>(var1));
         }
      }

      return (V)(var3 == null ? null : var3);
   }

   @Override
   public void addPredecessor(N var1, V var2) {
      Object var3 = this.adjacentNodeValues.put((N)var1, PRED);
      boolean var4;
      if (var3 == null) {
         var4 = true;
      } else if (var3 instanceof DirectedGraphConnections.PredAndSucc) {
         this.adjacentNodeValues.put((N)var1, var3);
         var4 = false;
      } else if (var3 != PRED) {
         this.adjacentNodeValues.put((N)var1, new DirectedGraphConnections.PredAndSucc(var3));
         var4 = true;
      } else {
         var4 = false;
      }

      if (var4) {
         Graphs.checkPositive(++this.predecessorCount);
         if (this.orderedNodeConnections != null) {
            this.orderedNodeConnections.add(new DirectedGraphConnections.NodeConnection.Pred<>((N)var1));
         }
      }
   }

   @Override
   public V addSuccessor(N var1, V var2) {
      Object var3 = this.adjacentNodeValues.put((N)var1, var2);
      Object var4;
      if (var3 == null) {
         var4 = null;
      } else if (var3 instanceof DirectedGraphConnections.PredAndSucc) {
         this.adjacentNodeValues.put((N)var1, new DirectedGraphConnections.PredAndSucc(var2));
         var4 = ((DirectedGraphConnections.PredAndSucc)var3).successorValue;
      } else if (var3 == PRED) {
         this.adjacentNodeValues.put((N)var1, new DirectedGraphConnections.PredAndSucc(var2));
         var4 = null;
      } else {
         var4 = var3;
      }

      if (var4 == null) {
         Graphs.checkPositive(++this.successorCount);
         if (this.orderedNodeConnections != null) {
            this.orderedNodeConnections.add(new DirectedGraphConnections.NodeConnection.Succ<>((N)var1));
         }
      }

      return (V)(var4 == null ? null : var4);
   }

   private static boolean isPredecessor(Object var0) {
      return var0 == PRED || var0 instanceof DirectedGraphConnections.PredAndSucc;
   }

   private static boolean isSuccessor(Object var0) {
      return var0 != PRED && var0 != null;
   }

   private abstract static class NodeConnection<N> {
      final N node;

      NodeConnection(N var1) {
         this.node = Preconditions.checkNotNull((N)var1);
      }

      static final class Pred<N> extends DirectedGraphConnections.NodeConnection<N> {
         Pred(N var1) {
            super((N)var1);
         }

         @Override
         public boolean equals(Object var1) {
            return var1 instanceof DirectedGraphConnections.NodeConnection.Pred
               ? this.node.equals(((DirectedGraphConnections.NodeConnection.Pred)var1).node)
               : false;
         }

         @Override
         public int hashCode() {
            return DirectedGraphConnections.NodeConnection.Pred.class.hashCode() + this.node.hashCode();
         }
      }

      static final class Succ<N> extends DirectedGraphConnections.NodeConnection<N> {
         Succ(N var1) {
            super((N)var1);
         }

         @Override
         public boolean equals(Object var1) {
            return var1 instanceof DirectedGraphConnections.NodeConnection.Succ
               ? this.node.equals(((DirectedGraphConnections.NodeConnection.Succ)var1).node)
               : false;
         }

         @Override
         public int hashCode() {
            return DirectedGraphConnections.NodeConnection.Succ.class.hashCode() + this.node.hashCode();
         }
      }
   }

   private static final class PredAndSucc {
      private final Object successorValue;

      PredAndSucc(Object var1) {
         this.successorValue = var1;
      }
   }
}
