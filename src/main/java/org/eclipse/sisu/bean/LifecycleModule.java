package org.eclipse.sisu.bean;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public final class LifecycleModule implements Module {
   private final Matcher<TypeLiteral<?>> matcher = new AbstractMatcher<TypeLiteral<?>>() {
      public boolean matches(TypeLiteral<?> var1) {
         return LifecycleModule.this.manager.manage(var1.getRawType());
      }
   };
   private final TypeListener typeListener = new TypeListener() {
      private final InjectionListener<Object> listener = new InjectionListener<Object>() {
         public void afterInjection(Object var1) {
            LifecycleModule.this.manager.manage(var1);
         }
      };

      public <B> void hear(TypeLiteral<B> var1, TypeEncounter<B> var2) {
         var2.register(this.listener);
      }
   };
   final BeanManager manager;

   public LifecycleModule() {
      this(new LifecycleManager());
   }

   public LifecycleModule(BeanManager var1) {
      this.manager = var1;
   }

   public void configure(Binder var1) {
      var1.bind(BeanManager.class).toInstance(this.manager);
      var1.bindListener(this.matcher, this.typeListener);
   }
}
