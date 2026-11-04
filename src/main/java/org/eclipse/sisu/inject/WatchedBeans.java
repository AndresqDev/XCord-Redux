package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

final class WatchedBeans<Q extends Annotation, T, W> implements BindingSubscriber<T> {
   private final BeanCache<Q, T> beans = new BeanCache<>();
   private final Key<T> key;
   private final Mediator<Q, T, W> mediator;
   private final QualifyingStrategy strategy;
   private final Reference<W> watcherRef;

   WatchedBeans(Key<T> var1, Mediator<Q, T, W> var2, W var3) {
      this.key = var1;
      this.mediator = var2;
      this.strategy = QualifyingStrategy.selectFor(var1);
      this.watcherRef = (Reference<W>)(new WeakReference<>(var3));
   }

   @Override
   public TypeLiteral<T> type() {
      return this.key.getTypeLiteral();
   }

   @Override
   public void add(Binding<T> var1, int var2) {
      Annotation var3 = this.strategy.qualifies(this.key, var1);
      if (var3 != null) {
         Object var4 = this.watcherRef.get();
         if (var4 != null) {
            BeanEntry var5 = this.beans.create((Q)var3, var1, var2);

            try {
               this.mediator.add(var5, (W)var4);
            } catch (Throwable var7) {
               Logs.catchThrowable(var7);
               Logs.warn("Problem adding: <> to: " + this.detail(var4), var5, var7);
            }
         }
      }
   }

   @Override
   public void remove(Binding<T> var1) {
      BeanEntry var2 = this.beans.remove(var1);
      if (var2 != null) {
         Object var3 = this.watcherRef.get();
         if (var3 != null) {
            try {
               this.mediator.remove(var2, (W)var3);
            } catch (Throwable var5) {
               Logs.catchThrowable(var5);
               Logs.warn("Problem removing: <> from: " + this.detail(var3), var2, var5);
            }
         }
      }
   }

   @Override
   public Iterable<Binding<T>> bindings() {
      return this.beans.bindings();
   }

   private String detail(Object var1) {
      return Logs.identityToString(var1) + " via: " + Logs.identityToString(this.mediator);
   }
}
