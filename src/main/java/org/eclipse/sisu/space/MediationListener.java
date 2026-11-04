package org.eclipse.sisu.space;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.BeanLocator;
import xcord.Provider;

final class MediationListener extends AbstractMatcher<TypeLiteral<?>> implements InjectionListener<Object>, TypeListener {
   private final List<MediationListener.Mediation<?, ?, ?>> mediation = new ArrayList<>();
   private final Provider<BeanLocator> locator;

   public MediationListener(Binder var1) {
      this.locator = var1.getProvider(BeanLocator.class);
   }

   public void mediate(Key var1, Mediator var2, Class var3) {
      this.mediation.add(new MediationListener.Mediation(var1, var2, var3));
   }

   public boolean matches(TypeLiteral<?> var1) {
      for (MediationListener.Mediation var2 : this.mediation) {
         if (var2.watcherType.isAssignableFrom(var1.getRawType())) {
            return true;
         }
      }

      return false;
   }

   public <T> void hear(TypeLiteral<T> var1, TypeEncounter<T> var2) {
      var2.register(this);
   }

   public void afterInjection(Object var1) {
      for (MediationListener.Mediation var2 : this.mediation) {
         if (var2.watcherType.isInstance(var1)) {
            ((BeanLocator)this.locator.get()).watch(var2.watchedKey, var2.mediator, var1);
         }
      }
   }

   private static final class Mediation<Q extends Annotation, T, W> {
      final Key<T> watchedKey;
      final Mediator<Q, T, W> mediator;
      final Class<W> watcherType;

      Mediation(Key<T> var1, Mediator<Q, T, W> var2, Class<W> var3) {
         this.watchedKey = var1;
         this.mediator = var2;
         this.watcherType = var3;
      }
   }
}
