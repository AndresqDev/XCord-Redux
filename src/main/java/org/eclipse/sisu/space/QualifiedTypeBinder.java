package org.eclipse.sisu.space;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.enterprise.inject.Typed;
import org.eclipse.sisu.EagerSingleton;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.Legacy;
import org.eclipse.sisu.inject.TypeArguments;
import xcord.Provider;
import xcord.Singleton;

public final class QualifiedTypeBinder implements QualifiedTypeListener {
   private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL;
   private static final boolean HAS_JSR299_TYPED;
   private final Binder rootBinder;
   private MediationListener mediationListener;
   private Object currentSource;
   private Binder binder;

   static {
      boolean var0;
      try {
         var0 = Typed.class.isAnnotation();
      } catch (LinkageError var1) {
         var0 = false;
      }

      HAS_JSR299_TYPED = var0;
      OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
   }

   public QualifiedTypeBinder(Binder var1) {
      this.rootBinder = var1;
      this.binder = var1;
   }

   @Override
   public void hear(Class var1, Object var2) {
      if (this.currentSource != var2) {
         if (var2 != null) {
            this.binder = this.rootBinder.withSource(var2);
            this.currentSource = var2;
         } else {
            this.binder = this.rootBinder;
            this.currentSource = null;
         }
      }

      if (TypeArguments.isConcrete(var1)) {
         if (Module.class.isAssignableFrom(var1)) {
            this.installModule(var1);
         } else if (Mediator.class.isAssignableFrom(var1)) {
            this.registerMediator(var1);
         } else if (org.sonatype.inject.Mediator.class.isAssignableFrom(var1)) {
            this.registerLegacyMediator(var1);
         } else if (Provider.class.isAssignableFrom(var1)) {
            this.bindProviderType(var1);
         } else {
            this.bindQualifiedType(var1);
         }
      }
   }

   private void installModule(Class<Module> var1) {
      Module var2 = this.newInstance(var1);
      if (var2 != null) {
         this.binder.install(var2);
      }
   }

   private void registerMediator(Class<Mediator> var1) {
      TypeLiteral[] var2 = resolveTypeArguments(var1, Mediator.class);
      if (var2.length != 3) {
         this.binder.addError(var1 + " has wrong number of type arguments", new Object[0]);
      } else {
         Mediator var3 = this.newInstance(var1);
         if (var3 != null) {
            this.mediate(watchedKey(var2[1], var2[0].getRawType()), var3, var2[2].getRawType());
         }
      }
   }

   private void registerLegacyMediator(Class<org.sonatype.inject.Mediator> var1) {
      TypeLiteral[] var2 = resolveTypeArguments(var1, org.sonatype.inject.Mediator.class);
      if (var2.length != 3) {
         this.binder.addError(var1 + " has wrong number of type arguments", new Object[0]);
      } else {
         Mediator var3 = Legacy.adapt(this.newInstance(var1));
         if (var3 != null) {
            this.mediate(watchedKey(var2[1], var2[0].getRawType()), var3, var2[2].getRawType());
         }
      }
   }

   private void mediate(Key var1, Mediator var2, Class var3) {
      if (this.mediationListener == null) {
         this.mediationListener = new MediationListener(this.binder);
         this.binder.bindListener(this.mediationListener, this.mediationListener);
      }

      this.mediationListener.mediate(var1, var2, var3);
   }

   private void bindProviderType(Class<?> var1) {
      TypeLiteral[] var2 = resolveTypeArguments(var1, Provider.class);
      if (var2.length != 1) {
         this.binder.addError(var1 + " has wrong number of type arguments", new Object[0]);
      } else {
         this.binder.bind(var1).in(Scopes.SINGLETON);
         Named var3 = getBindingName(var1);
         Class[] var4 = getBindingTypes(var1);
         Key var5 = getBindingKey(var2[0], var3);
         ScopedBindingBuilder var6 = this.binder.bind(var5).toProvider(var1);
         if (isEagerSingleton(var1)) {
            var6.asEagerSingleton();
         } else if (isSingleton(var1)) {
            var6.in(Scopes.SINGLETON);
         }

         if (var4 != null) {
            for (Class var7 : var4) {
               this.binder.bind(var5.ofType(var7)).to(var5);
            }
         }
      }
   }

   private void bindQualifiedType(Class<?> var1) {
      AnnotatedBindingBuilder var2 = this.binder.bind(var1);
      if (isEagerSingleton(var1)) {
         var2.asEagerSingleton();
      }

      Named var3 = getBindingName(var1);
      Class[] var4 = getBindingTypes(var1);
      if (var4 != null) {
         Key var5 = getBindingKey(OBJECT_TYPE_LITERAL, var3);

         for (Class var6 : var4) {
            this.binder.bind(var5.ofType(var6)).to(var1);
         }
      } else {
         this.binder.bind(WildcardKey.get(var1, var3)).to(var1);
      }
   }

   private <T> T newInstance(Class<T> var1) {
      try {
         final Constructor var2 = var1.getDeclaredConstructor();
         if (!var2.isAccessible()) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  var2.setAccessible(true);
                  return null;
               }
            });
         }

         return (T)var2.newInstance();
      } catch (Exception var4) {
         Object var3 = var4 instanceof InvocationTargetException ? var4.getCause() : var4;
         this.binder.addError("Error creating instance of: " + var1 + " reason: " + var3, new Object[0]);
         return null;
      } catch (LinkageError var5) {
         this.binder.addError("Error creating instance of: " + var1 + " reason: " + var5, new Object[0]);
         return null;
      }
   }

   private static TypeLiteral<?>[] resolveTypeArguments(Class<?> var0, Class<?> var1) {
      return TypeArguments.get(TypeLiteral.get(var0).getSupertype(var1));
   }

   private static <T> Key<T> getBindingKey(TypeLiteral<T> var0, Annotation var1) {
      return var1 != null ? Key.get(var0, var1) : Key.get(var0);
   }

   private static Named getBindingName(Class<?> var0) {
      xcord.Named var1 = var0.getAnnotation(xcord.Named.class);
      if (var1 != null) {
         try {
            String var2 = var1.value();
            if (var2.length() > 0) {
               return "default".equals(var2) ? null : Names.named(var2);
            }
         } catch (IncompleteAnnotationException var4) {
         }
      } else {
         Named var5 = var0.getAnnotation(Named.class);
         if (var5 != null) {
            String var3 = var5.value();
            if (var3.length() > 0) {
               return "default".equals(var3) ? null : var5;
            }
         }
      }

      return var0.getSimpleName().startsWith("Default") ? null : Names.named(var0.getName());
   }

   private static Class<?>[] getBindingTypes(Class<?> var0) {
      for (Class var1 = var0; var1 != null && var1 != Object.class; var1 = var1.getSuperclass()) {
         if (HAS_JSR299_TYPED) {
            Typed var2 = var1.getAnnotation(Typed.class);
            if (var2 != null) {
               return var2.value().length > 0 ? var2.value() : var1.getInterfaces();
            }
         }

         org.eclipse.sisu.Typed var3 = var1.getAnnotation(org.eclipse.sisu.Typed.class);
         if (var3 != null) {
            return var3.value().length > 0 ? var3.value() : var1.getInterfaces();
         }
      }

      return null;
   }

   private static boolean isSingleton(Class<?> var0) {
      return var0.isAnnotationPresent(Singleton.class) || var0.isAnnotationPresent(com.google.inject.Singleton.class);
   }

   private static boolean isEagerSingleton(Class<?> var0) {
      return var0.isAnnotationPresent(EagerSingleton.class) || var0.isAnnotationPresent(org.sonatype.inject.EagerSingleton.class);
   }

   private static <T> Key<T> watchedKey(TypeLiteral<T> var0, Class var1) {
      return var1.isAnnotation() ? Key.get(var0, var1) : Key.get(var0);
   }
}
