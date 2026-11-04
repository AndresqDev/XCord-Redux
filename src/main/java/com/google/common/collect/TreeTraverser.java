package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.Consumer;

@Deprecated
@ElementTypesAreNonnullByDefault
@Beta
@GwtCompatible
public abstract class TreeTraverser<T> {
   @Deprecated
   public static <T> TreeTraverser<T> using(final Function<T, ? extends Iterable<T>> var0) {
      Preconditions.checkNotNull(var0);
      return new TreeTraverser<T>() {
         @Override
         public Iterable<T> children(T var1) {
            return (Iterable<T>)var0.apply(var1);
         }
      };
   }

   public abstract Iterable<T> children(T var1);

   @Deprecated
   public final FluentIterable<T> preOrderTraversal(final T var1) {
      Preconditions.checkNotNull(var1);
      return new FluentIterable<T>() {
         public UnmodifiableIterator<T> iterator() {
            return (UnmodifiableIterator<T>)TreeTraverser.this.preOrderIterator(var1);
         }

         @Override
         public void forEach(final Consumer<? super T> var1x) {
            Preconditions.checkNotNull(var1x);
            (new Consumer<T>() {
               @Override
               public void accept(T var1xx) {
                  var1x.accept(var1xx);
                  TreeTraverser.this.children(var1xx).forEach(this);
               }
            }).accept((T)var1);
         }
      };
   }

   UnmodifiableIterator<T> preOrderIterator(T var1) {
      return new TreeTraverser.PreOrderIterator(var1);
   }

   @Deprecated
   public final FluentIterable<T> postOrderTraversal(final T var1) {
      Preconditions.checkNotNull(var1);
      return new FluentIterable<T>() {
         public UnmodifiableIterator<T> iterator() {
            return (UnmodifiableIterator<T>)TreeTraverser.this.postOrderIterator(var1);
         }

         @Override
         public void forEach(final Consumer<? super T> var1x) {
            Preconditions.checkNotNull(var1x);
            (new Consumer<T>() {
               @Override
               public void accept(T var1xx) {
                  TreeTraverser.this.children(var1xx).forEach(this);
                  var1x.accept(var1xx);
               }
            }).accept((T)var1);
         }
      };
   }

   UnmodifiableIterator<T> postOrderIterator(T var1) {
      return new TreeTraverser.PostOrderIterator(var1);
   }

   @Deprecated
   public final FluentIterable<T> breadthFirstTraversal(final T var1) {
      Preconditions.checkNotNull(var1);
      return new FluentIterable<T>() {
         public UnmodifiableIterator<T> iterator() {
            return TreeTraverser.this.new BreadthFirstIterator(var1);
         }
      };
   }

   private final class BreadthFirstIterator extends UnmodifiableIterator<T> implements PeekingIterator<T> {
      private final Queue<T> queue = new ArrayDeque<>();

      BreadthFirstIterator(T var2) {
         this.queue.add((T)var2);
      }

      @Override
      public boolean hasNext() {
         return !this.queue.isEmpty();
      }

      @Override
      public T peek() {
         return this.queue.element();
      }

      @Override
      public T next() {
         Object var1 = this.queue.remove();
         Iterables.addAll(this.queue, TreeTraverser.this.children((T)var1));
         return (T)var1;
      }
   }

   private final class PostOrderIterator extends AbstractIterator<T> {
      private final ArrayDeque<TreeTraverser.PostOrderNode<T>> stack = new ArrayDeque<>();

      PostOrderIterator(T var2) {
         this.stack.addLast(this.expand((T)var2));
      }

      @Override
      protected T computeNext() {
         while (!this.stack.isEmpty()) {
            TreeTraverser.PostOrderNode var1 = this.stack.getLast();
            if (!var1.childIterator.hasNext()) {
               this.stack.removeLast();
               return var1.root;
            }

            Object var2 = var1.childIterator.next();
            this.stack.addLast(this.expand((T)var2));
         }

         return (T)this.endOfData();
      }

      private TreeTraverser.PostOrderNode<T> expand(T var1) {
         return new TreeTraverser.PostOrderNode<>((T)var1, TreeTraverser.this.children((T)var1).iterator());
      }
   }

   private static final class PostOrderNode<T> {
      final T root;
      final Iterator<T> childIterator;

      PostOrderNode(T var1, Iterator<T> var2) {
         this.root = Preconditions.checkNotNull((T)var1);
         this.childIterator = Preconditions.checkNotNull(var2);
      }
   }

   private final class PreOrderIterator extends UnmodifiableIterator<T> {
      private final Deque<Iterator<T>> stack = new ArrayDeque<>();

      PreOrderIterator(T var2) {
         this.stack.addLast(Iterators.singletonIterator(Preconditions.checkNotNull((T)var2)));
      }

      @Override
      public boolean hasNext() {
         return !this.stack.isEmpty();
      }

      @Override
      public T next() {
         Iterator var1 = this.stack.getLast();
         Object var2 = Preconditions.checkNotNull(var1.next());
         if (!var1.hasNext()) {
            this.stack.removeLast();
         }

         Iterator var3 = TreeTraverser.this.children((T)var2).iterator();
         if (var3.hasNext()) {
            this.stack.addLast(var3);
         }

         return (T)var2;
      }
   }
}
