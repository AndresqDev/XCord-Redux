package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;

final class RankedBindings<T> implements Iterable<Binding<T>>, BindingSubscriber<T> {
   final transient RankedSequence<Binding<T>> bindings = new RankedSequence<>();
   final transient TypeLiteral<T> type;
   final transient RankedSequence<BindingPublisher> pendingPublishers;
   final Collection<BeanCache<?, T>> cachedBeans = Weak.elements();

   RankedBindings(TypeLiteral<T> var1, RankedSequence<BindingPublisher> var2) {
      this.type = var1;
      this.pendingPublishers = new RankedSequence<>(var2);
   }

   @Override
   public TypeLiteral<T> type() {
      return this.type;
   }

   @Override
   public void add(Binding<T> var1, int var2) {
      this.bindings.insert(var1, var2);
   }

   @Override
   public void remove(Binding<T> var1) {
      if (this.bindings.removeThis(var1)) {
         synchronized (this.cachedBeans) {
            for (BeanCache var3 : this.cachedBeans) {
               var3.remove(var1);
            }
         }
      }
   }

   @Override
   public Iterable<Binding<T>> bindings() {
      return this.bindings.snapshot();
   }

   public RankedBindings<T>.Itr iterator() {
      return new RankedBindings.Itr();
   }

   <Q extends Annotation> BeanCache<Q, T> newBeanCache() {
      BeanCache var1 = new BeanCache();
      synchronized (this.cachedBeans) {
         this.cachedBeans.add(var1);
         return var1;
      }
   }

   void add(BindingPublisher var1, int var2) {
      this.pendingPublishers.insert(var1, var2);
   }

   void remove(BindingPublisher var1) {
      synchronized (var1) {
         if (!this.pendingPublishers.removeThis(var1)) {
            var1.unsubscribe(this);
         }
      }
   }

   final class Itr implements Iterator<Binding<T>> {
      private final RankedSequence<Binding<T>>.Itr itr = RankedBindings.this.bindings.iterator();

      @Override
      public boolean hasNext() {
         for (BindingPublisher var1 = RankedBindings.this.pendingPublishers.peek();
            var1 != null && !this.itr.hasNext(var1.maxBindingRank());
            var1 = RankedBindings.this.pendingPublishers.peek()
         ) {
            synchronized (var1) {
               if (var1 == RankedBindings.this.pendingPublishers.peek()) {
                  var1.subscribe(RankedBindings.this);
                  RankedBindings.this.pendingPublishers.removeThis(var1);
               }
            }
         }

         return this.itr.hasNext();
      }

      public Binding<T> next() {
         return this.itr.next();
      }

      public int rank() {
         return this.itr.rank();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
