package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.eclipse.sisu.BeanEntry;

final class LocatedBeans<Q extends Annotation, T> implements Iterable<BeanEntry<Q, T>> {
   final Key<T> key;
   final RankedBindings<T> explicitBindings;
   final ImplicitBindings implicitBindings;
   final QualifyingStrategy strategy;
   final BeanCache<Q, T> beans;

   LocatedBeans(Key<T> var1, RankedBindings<T> var2, ImplicitBindings var3) {
      this.key = var1;
      this.explicitBindings = var2;
      this.implicitBindings = var3;
      this.strategy = QualifyingStrategy.selectFor(var1);
      this.beans = var2.newBeanCache();
   }

   @Override
   public Iterator<BeanEntry<Q, T>> iterator() {
      return new LocatedBeans.Itr();
   }

   final class Itr implements Iterator<BeanEntry<Q, T>> {
      private final RankedBindings<T>.Itr itr = LocatedBeans.this.explicitBindings.iterator();
      private final Map<Binding<T>, BeanEntry<Q, T>> readCache = LocatedBeans.this.beans.flush();
      private boolean checkImplicitBindings = LocatedBeans.this.implicitBindings != null;
      private BeanEntry<Q, T> nextBean;

      @Override
      public boolean hasNext() {
         if (this.nextBean != null) {
            return true;
         } else {
            while (this.itr.hasNext()) {
               Binding var1 = this.itr.next();
               if (this.readCache != null && (this.nextBean = this.readCache.get(var1)) != null) {
                  return true;
               }

               Annotation var2 = LocatedBeans.this.strategy.qualifies(LocatedBeans.this.key, var1);
               if (var2 != null) {
                  this.nextBean = LocatedBeans.this.beans.create((Q)var2, var1, this.itr.rank());
                  return true;
               }
            }

            if (this.checkImplicitBindings) {
               Binding var3 = LocatedBeans.this.implicitBindings.get(LocatedBeans.this.key.getTypeLiteral());
               if (var3 != null) {
                  this.nextBean = LocatedBeans.this.beans.create((Q)QualifyingStrategy.DEFAULT_QUALIFIER, var3, Integer.MIN_VALUE);
                  return true;
               }
            }

            return false;
         }
      }

      public BeanEntry<Q, T> next() {
         if (this.hasNext()) {
            this.checkImplicitBindings = false;
            BeanEntry var1 = this.nextBean;
            this.nextBean = null;
            return var1;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
