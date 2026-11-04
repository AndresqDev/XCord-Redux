package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.servlet.InstanceFilterBinding;
import com.google.inject.servlet.InstanceServletBinding;
import com.google.inject.servlet.LinkedFilterBinding;
import com.google.inject.servlet.LinkedServletBinding;
import com.google.inject.servlet.ServletModuleTargetVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.UntargettedBinding;
import java.lang.annotation.Annotation;
import javax.annotation.Priority;
import org.eclipse.sisu.Description;
import xcord.Provider;

final class Implementations {
   private static final boolean HAS_GUICE_SERVLET;
   private static final boolean HAS_JSR250_PRIORITY;

   static {
      boolean var0;
      try {
         var0 = BindingTargetVisitor.class.isInstance(Implementations.ServletFinder.THIS);
      } catch (LinkageError var3) {
         var0 = false;
      }

      HAS_GUICE_SERVLET = var0;

      boolean var1;
      try {
         var1 = Priority.class.isAnnotation();
      } catch (LinkageError var2) {
         var1 = false;
      }

      HAS_JSR250_PRIORITY = var1;
   }

   private Implementations() {
   }

   public static Class<?> find(Binding<?> var0) {
      return (Class<?>)var0.acceptTargetVisitor(Implementations.ClassFinder.THIS);
   }

   public static <T extends Annotation> T getAnnotation(Binding<?> var0, Class<T> var1) {
      boolean var2 = org.eclipse.sisu.Priority.class.equals(var1);
      Class var3 = (Class)var0.acceptTargetVisitor(HAS_GUICE_SERVLET && var2 ? Implementations.ServletFinder.THIS : Implementations.ClassFinder.THIS);
      Annotation var4 = null;
      if (var3 != null) {
         var4 = var3.getAnnotation(var1);
         if (var4 == null) {
            if (HAS_JSR250_PRIORITY && var2) {
               var4 = adaptJsr250(var0, var3);
            } else if (Description.class.equals(var1)) {
               var4 = adaptLegacy(var0, var3);
            }
         }
      }

      return (T)var4;
   }

   private static <T extends Annotation> T adaptJsr250(Binding<?> var0, Class<?> var1) {
      Priority var2 = var1.getAnnotation(Priority.class);
      return (T)(var2 != null ? new PrioritySource(var0.getSource(), var2.value()) : null);
   }

   private static <T extends Annotation> T adaptLegacy(Binding<?> var0, Class<?> var1) {
      org.sonatype.inject.Description var2 = var1.getAnnotation(org.sonatype.inject.Description.class);
      return (T)(var2 != null ? new DescriptionSource(var0.getSource(), var2.value()) : null);
   }

   static class ClassFinder extends DefaultBindingTargetVisitor<Object, Class<?>> {
      static final BindingTargetVisitor<Object, Class<?>> THIS = new Implementations.ClassFinder();

      public Class<?> visit(UntargettedBinding<?> var1) {
         return var1.getKey().getTypeLiteral().getRawType();
      }

      public Class<?> visit(LinkedKeyBinding<?> var1) {
         return var1.getLinkedKey().getTypeLiteral().getRawType();
      }

      public Class<?> visit(ConstructorBinding<?> var1) {
         return var1.getConstructor().getDeclaringType().getRawType();
      }

      public Class<?> visit(InstanceBinding<?> var1) {
         return var1.getInstance().getClass();
      }

      public Class<?> visit(ProviderInstanceBinding<?> var1) {
         Provider var2 = Guice4.getProviderInstance(var1);
         if (var2 instanceof DeferredProvider) {
            try {
               return ((DeferredProvider)var2).getImplementationClass().load();
            } catch (TypeNotPresentException var3) {
            }
         }

         return null;
      }

      public Class<?> visit(ExposedBinding<?> var1) {
         return (Class<?>)var1.getPrivateElements().getInjector().getBinding(var1.getKey()).acceptTargetVisitor(this);
      }
   }

   static final class ServletFinder extends Implementations.ClassFinder implements ServletModuleTargetVisitor<Object, Class<?>> {
      static final BindingTargetVisitor<Object, Class<?>> THIS = new Implementations.ServletFinder();

      public Class<?> visit(InstanceFilterBinding var1) {
         return var1.getFilterInstance().getClass();
      }

      public Class<?> visit(InstanceServletBinding var1) {
         return var1.getServletInstance().getClass();
      }

      public Class<?> visit(LinkedFilterBinding var1) {
         return var1.getLinkedKey().getTypeLiteral().getRawType();
      }

      public Class<?> visit(LinkedServletBinding var1) {
         return var1.getLinkedKey().getTypeLiteral().getRawType();
      }
   }
}
