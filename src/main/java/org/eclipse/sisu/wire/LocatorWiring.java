package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.ImplementedBy;
import com.google.inject.Key;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Dynamic;
import org.eclipse.sisu.Hidden;
import org.eclipse.sisu.inject.Legacy;
import org.eclipse.sisu.inject.Sources;
import org.eclipse.sisu.inject.TypeArguments;

public final class LocatorWiring implements Wiring {
   private static final Hidden HIDDEN_WIRING = Sources.hide(LocatorWiring.class.getName());
   private final BeanProviders beanProviders;
   private final Binder binder;

   public LocatorWiring(Binder var1) {
      this.beanProviders = new BeanProviders(var1);
      this.binder = var1.withSource(HIDDEN_WIRING);
   }

   @Override
   public boolean wire(Key<?> var1) {
      Class var2 = var1.getTypeLiteral().getRawType();
      if (Map.class == var2) {
         this.bindMapImport(var1);
      } else if (List.class == var2 || Collection.class == var2 || Iterable.class == var2) {
         this.bindListImport(var1);
      } else if (Set.class == var2) {
         this.bindSetImport(var1);
      } else {
         this.bindBeanImport(var1);
      }

      return true;
   }

   private void bindMapImport(Key var1) {
      TypeLiteral[] var2 = TypeArguments.get(var1.getTypeLiteral());
      if (2 == var2.length && var1.getAnnotation() == null) {
         Class var3 = var2[0].getRawType();
         if (String.class == var3) {
            this.binder.bind(var1).toProvider(this.beanProviders.stringMapOf(var2[1]));
         } else if (var3.isAnnotation()) {
            this.binder.bind(var1).toProvider(this.beanProviders.mapOf(Key.get(var2[1], var3)));
         } else if (Annotation.class == var3) {
            this.binder.bind(var1).toProvider(this.beanProviders.mapOf(Key.get(var2[1])));
         }
      }
   }

   private void bindListImport(Key var1) {
      TypeLiteral[] var2 = TypeArguments.get(var1.getTypeLiteral());
      if (1 == var2.length && var1.getAnnotation() == null) {
         TypeLiteral var3 = var2[0];
         if (BeanEntry.class != var3.getRawType() && org.sonatype.inject.BeanEntry.class != var3.getRawType()) {
            this.binder.bind(var1).toProvider(this.beanProviders.listOf(Key.get(var3)));
         } else {
            Provider var4 = this.getBeanEntriesProvider(var3);
            if (var4 != null) {
               this.binder.bind(var1).toProvider(var4);
            }
         }
      }
   }

   private Provider getBeanEntriesProvider(TypeLiteral var1) {
      TypeLiteral[] var2 = TypeArguments.get(var1);
      if (2 == var2.length) {
         Class var3 = var2[0].getRawType();
         Key var4 = var3.isAnnotation() ? Key.get(var2[1], var3) : Key.get(var2[1]);
         Provider var5 = this.beanProviders.beanEntriesOf(var4);
         return BeanEntry.class == var1.getRawType() ? var5 : Legacy.adapt(var5);
      } else {
         return null;
      }
   }

   private void bindSetImport(Key var1) {
      TypeLiteral[] var2 = TypeArguments.get(var1.getTypeLiteral());
      if (1 == var2.length && var1.getAnnotation() == null) {
         this.binder.bind(var1).toProvider(this.beanProviders.setOf(Key.get(var2[0])));
      }
   }

   private <T> void bindBeanImport(Key<T> var1) {
      Annotation var2 = var1.getAnnotation();
      if (var2 instanceof Named) {
         if (((Named)var2).value().length() == 0) {
            this.binder.bind(var1).toProvider(this.beanProviders.firstOf(Key.get(var1.getTypeLiteral(), Named.class)));
         } else {
            this.binder.bind(var1).toProvider(this.beanProviders.placeholderOf(var1));
         }
      } else if (var2 instanceof Dynamic) {
         Provider var3 = this.beanProviders.firstOf(Key.get(var1.getTypeLiteral()));
         this.binder.bind(var1).toInstance(GlueLoader.dynamicGlue(var1.getTypeLiteral(), var3));
      } else {
         this.binder.bind(var1).toProvider(this.beanProviders.firstOf(var1));
         if (var1.getAnnotationType() == null) {
            this.bindImplicitType(var1.getTypeLiteral());
         }
      }
   }

   private void bindImplicitType(TypeLiteral var1) {
      try {
         Class var2 = var1.getRawType();
         if (TypeArguments.isConcrete(var2)) {
            Member var3 = InjectionPoint.forConstructorOf(var1).getMember();
            this.binder.bind(TypeArguments.implicitKey(var2)).toConstructor((Constructor)var3);
         } else {
            ImplementedBy var7 = var2.getAnnotation(ImplementedBy.class);
            if (var7 != null) {
               this.binder.bind(TypeArguments.implicitKey(var2)).to(var7.value());
            } else {
               ProvidedBy var4 = var2.getAnnotation(ProvidedBy.class);
               if (var4 != null) {
                  this.binder.bind(TypeArguments.implicitKey(var2)).toProvider(var4.value());
               }
            }
         }
      } catch (RuntimeException var5) {
      } catch (LinkageError var6) {
      }
   }
}
