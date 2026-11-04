package org.eclipse.sisu.wire;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.spi.UntargettedBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.eclipse.sisu.inject.DeferredProvider;
import org.eclipse.sisu.inject.Guice4;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.TypeArguments;

final class DependencyAnalyzer extends DefaultBindingTargetVisitor<Object, Boolean> {
   private static final Set<Class<?>> RESTRICTED_CLASSES = new HashSet<>(
      Arrays.asList(
         AbstractModule.class,
         Binder.class,
         Binding.class,
         Injector.class,
         Key.class,
         Logger.class,
         MembersInjector.class,
         Module.class,
         Provider.class,
         Scope.class,
         TypeLiteral.class
      )
   );
   private final Map<TypeLiteral<?>, Boolean> analyzedTypes = new HashMap<>();
   private final Set<Key<?>> requiredKeys = new HashSet<>();

   DependencyAnalyzer() {
      this.requiredKeys.add(ParameterKeys.PROPERTIES);
   }

   public Set<Key<?>> findMissingKeys(Set<Key<?>> var1) {
      HashSet var2 = new HashSet();

      while (this.requiredKeys.size() > 0) {
         ArrayList var3 = new ArrayList<>(this.requiredKeys);
         this.requiredKeys.clear();

         for (Key var4 : var3) {
            if (!var1.contains(var4) && var2.add(var4)) {
               this.analyzeImplicitBindings(var4.getTypeLiteral());
            }
         }
      }

      return var2;
   }

   public Boolean visit(UntargettedBinding<?> var1) {
      return this.analyzeImplementation(var1.getKey().getTypeLiteral(), true);
   }

   public Boolean visit(LinkedKeyBinding<?> var1) {
      Key var2 = var1.getLinkedKey();
      return var2.getAnnotationType() == null ? this.analyzeImplementation(var2.getTypeLiteral(), true) : Boolean.TRUE;
   }

   public Boolean visit(ProviderKeyBinding<?> var1) {
      Key var2 = var1.getProviderKey();
      return var2.getAnnotationType() == null ? this.analyzeImplementation(var2.getTypeLiteral(), true) : Boolean.TRUE;
   }

   public Boolean visit(ProviderInstanceBinding<?> var1) {
      xcord.Provider var2 = Guice4.getProviderInstance(var1);
      if (var2 instanceof DeferredProvider) {
         try {
            Class var3 = ((DeferredProvider)var2).getImplementationClass().load();
            this.analyzeImplementation(TypeLiteral.get(var3), false);
         } catch (TypeNotPresentException var4) {
         }

         return Boolean.TRUE;
      } else {
         return this.analyzeDependencies(var1.getDependencies());
      }
   }

   public Boolean visitOther(Binding<?> var1) {
      return var1 instanceof HasDependencies ? this.analyzeDependencies(((HasDependencies)var1).getDependencies()) : Boolean.TRUE;
   }

   public <T> Boolean visit(ProviderLookup<T> var1) {
      this.requireKey(var1.getKey());
      return Boolean.TRUE;
   }

   public Boolean visit(StaticInjectionRequest var1) {
      return this.analyzeInjectionPoints(var1.getInjectionPoints());
   }

   public Boolean visit(InjectionRequest<?> var1) {
      return this.analyzeInjectionPoints(var1.getInjectionPoints());
   }

   private void requireKey(Key<?> var1) {
      if (!this.requiredKeys.contains(var1)) {
         Class var2 = var1.getTypeLiteral().getRawType();
         if (xcord.Provider.class == var2 || Provider.class == var2) {
            this.requireKey(var1.ofType(TypeArguments.get(var1.getTypeLiteral(), 0)));
         } else if (!RESTRICTED_CLASSES.contains(var2)) {
            this.requiredKeys.add(var1);
         }
      }
   }

   private Boolean analyzeImplementation(TypeLiteral<?> var1, boolean var2) {
      Boolean var3 = this.analyzedTypes.get(var1);
      if (var3 == null) {
         var3 = Boolean.TRUE;
         if (TypeArguments.isConcrete(var1) && !var1.toString().startsWith("java")) {
            try {
               boolean var4 = this.analyzeInjectionPoints(InjectionPoint.forInstanceMethodsAndFields(var1));
               if (!this.analyzeDependencies(InjectionPoint.forConstructorOf(var1).getDependencies()) || !var4) {
                  var3 = Boolean.FALSE;
               }
            } catch (RuntimeException var5) {
               if (var2) {
                  Logs.trace("Potential problem: {}", var1, var5);
               }

               var3 = Boolean.FALSE;
            } catch (LinkageError var6) {
               if (var2) {
                  Logs.trace("Potential problem: {}", var1, var6);
               }

               var3 = Boolean.FALSE;
            }
         }

         this.analyzedTypes.put(var1, var3);
      }

      return var3;
   }

   private boolean analyzeInjectionPoints(Set<InjectionPoint> var1) {
      boolean var2 = true;

      for (InjectionPoint var3 : var1) {
         var2 &= this.analyzeDependencies(var3.getDependencies());
      }

      return var2;
   }

   private boolean analyzeDependencies(Collection<Dependency<?>> var1) {
      boolean var2 = true;

      for (Dependency var3 : var1) {
         Key var5 = var3.getKey();
         if (var5.hasAttributes() && "Assisted".equals(var5.getAnnotationType().getSimpleName())) {
            var2 = false;
         } else {
            this.requireKey(var5);
         }
      }

      return var2;
   }

   private void analyzeImplicitBindings(TypeLiteral<?> var1) {
      if (!this.analyzedTypes.containsKey(var1)) {
         Class var2 = var1.getRawType();
         if (TypeArguments.isConcrete(var2)) {
            this.analyzeImplementation(var1, false);
         } else {
            this.analyzedTypes.put(var1, Boolean.TRUE);
            ImplementedBy var3 = var2.getAnnotation(ImplementedBy.class);
            if (var3 != null) {
               this.requireKey(Key.get(var3.value()));
            } else {
               ProvidedBy var4 = var2.getAnnotation(ProvidedBy.class);
               if (var4 != null) {
                  this.requireKey(Key.get(var4.value()));
               }
            }
         }
      }
   }
}
