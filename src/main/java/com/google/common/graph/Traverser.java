package com.google.common.graph;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.DoNotMock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

@DoNotMock("Call forGraph or forTree, passing a lambda or a Graph with the desired edges (built with GraphBuilder)")
@ElementTypesAreNonnullByDefault
@Beta
public abstract class Traverser<N> {
   private final SuccessorsFunction<N> successorFunction;

   private Traverser(SuccessorsFunction<N> var1) {
      this.successorFunction = Preconditions.checkNotNull(var1);
   }

   public static <N> Traverser<N> forGraph(final SuccessorsFunction<N> var0) {
      return new Traverser<N>(var0) {
         @Override
         Traverser.Traversal<N> newTraversal() {
            return Traverser.Traversal.inGraph(var0);
         }
      };
   }

   public static <N> Traverser<N> forTree(final SuccessorsFunction<N> var0) {
      if (var0 instanceof BaseGraph) {
         Preconditions.checkArgument(((BaseGraph)var0).isDirected(), "Undirected graphs can never be trees.");
      }

      if (var0 instanceof Network) {
         Preconditions.checkArgument(((Network)var0).isDirected(), "Undirected networks can never be trees.");
      }

      return new Traverser<N>(var0) {
         @Override
         Traverser.Traversal<N> newTraversal() {
            return Traverser.Traversal.inTree(var0);
         }
      };
   }

   public final Iterable<N> breadthFirst(N var1) {
      return this.breadthFirst(ImmutableSet.of((N)var1));
   }

   public final Iterable<N> breadthFirst(Iterable<? extends N> var1) {
      final ImmutableSet var2 = this.validate(var1);
      return new Iterable<N>() {
         @Override
         public Iterator<N> iterator() {
            return Traverser.this.newTraversal().breadthFirst(var2.iterator());
         }
      };
   }

   public final Iterable<N> depthFirstPreOrder(N var1) {
      return this.depthFirstPreOrder(ImmutableSet.of((N)var1));
   }

   public final Iterable<N> depthFirstPreOrder(Iterable<? extends N> var1) {
      final ImmutableSet var2 = this.validate(var1);
      return new Iterable<N>() {
         @Override
         public Iterator<N> iterator() {
            return Traverser.this.newTraversal().preOrder(var2.iterator());
         }
      };
   }

   public final Iterable<N> depthFirstPostOrder(N var1) {
      return this.depthFirstPostOrder(ImmutableSet.of((N)var1));
   }

   public final Iterable<N> depthFirstPostOrder(Iterable<? extends N> var1) {
      final ImmutableSet var2 = this.validate(var1);
      return new Iterable<N>() {
         @Override
         public Iterator<N> iterator() {
            return Traverser.this.newTraversal().postOrder(var2.iterator());
         }
      };
   }

   abstract Traverser.Traversal<N> newTraversal();

   private ImmutableSet<N> validate(Iterable<? extends N> var1) {
      ImmutableSet var2 = ImmutableSet.copyOf(var1);

      for (Object var4 : var2) {
         this.successorFunction.successors((N)var4);
      }

      return var2;
   }

   private static enum InsertionOrder {
      FRONT {
         @Override
         <T> void insertInto(Deque<T> var1, T var2) {
            var1.addFirst(var2);
         }
      },
      BACK {
         @Override
         <T> void insertInto(Deque<T> var1, T var2) {
            var1.addLast(var2);
         }
      };

      private InsertionOrder() {
      }

      abstract <T> void insertInto(Deque<T> var1, T var2);
   }

   private abstract static class Traversal<N> {
      final SuccessorsFunction<N> successorFunction;

      Traversal(SuccessorsFunction<N> var1) {
         this.successorFunction = var1;
      }

      static <N> Traverser.Traversal<N> inGraph(SuccessorsFunction<N> var0) {
         final HashSet var1 = new HashSet();
         return new Traverser.Traversal<N>(var0) {
            @Override
            N visitNext(Deque<Iterator<? extends N>> var1x) {
               Iterator var2 = (Iterator)var1x.getFirst();

               while (var2.hasNext()) {
                  Object var3 = var2.next();
                  Objects.requireNonNull(var3);
                  if (var1.add(var3)) {
                     return (N)var3;
                  }
               }

               var1x.removeFirst();
               return null;
            }
         };
      }

      static <N> Traverser.Traversal<N> inTree(SuccessorsFunction<N> var0) {
         return new Traverser.Traversal<N>(var0) {
            @Override
            N visitNext(Deque<Iterator<? extends N>> var1) {
               Iterator var2 = (Iterator)var1.getFirst();
               if (var2.hasNext()) {
                  return Preconditions.checkNotNull((N)var2.next());
               } else {
                  var1.removeFirst();
                  return null;
               }
            }
         };
      }

      final Iterator<N> breadthFirst(Iterator<? extends N> var1) {
         return this.topDown(var1, Traverser.InsertionOrder.BACK);
      }

      final Iterator<N> preOrder(Iterator<? extends N> var1) {
         return this.topDown(var1, Traverser.InsertionOrder.FRONT);
      }

      private Iterator<N> topDown(Iterator<? extends N> var1, final Traverser.InsertionOrder var2) {
         final ArrayDeque var3 = new ArrayDeque();
         var3.add(var1);
         return new AbstractIterator<N>() {
            @Override
            protected N computeNext() {
               do {
                  Object var1 = Traversal.this.visitNext(var3);
                  if (var1 != null) {
                     Iterator var2x = Traversal.this.successorFunction.successors((N)var1).iterator();
                     if (var2x.hasNext()) {
                        var2.insertInto(var3, var2x);
                     }

                     return (N)var1;
                  }
               } while (!var3.isEmpty());

               return (N)this.endOfData();
            }
         };
      }

      final Iterator<N> postOrder(Iterator<? extends N> var1) {
         final ArrayDeque var2 = new ArrayDeque();
         final ArrayDeque var3 = new ArrayDeque();
         var3.add(var1);
         return new AbstractIterator<N>() {
            @Override
            protected N computeNext() {
               for (Object var1 = Traversal.this.visitNext(var3); var1 != null; var1 = Traversal.this.visitNext(var3)) {
                  Iterator var2x = Traversal.this.successorFunction.successors((N)var1).iterator();
                  if (!var2x.hasNext()) {
                     return (N)var1;
                  }

                  var3.addFirst(var2x);
                  var2.push(var1);
               }

               return (N)(!var2.isEmpty() ? var2.pop() : this.endOfData());
            }
         };
      }

      abstract N visitNext(Deque<Iterator<? extends N>> var1);
   }
}
