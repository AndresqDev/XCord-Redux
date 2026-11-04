package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ProviderKeyBinding;
import java.lang.annotation.Annotation;
import xcord.Provider;

enum QualifyingStrategy {
   UNRESTRICTED {
      @Override
      final Annotation qualifies(Key<?> var1, Binding<?> var2) {
         Annotation var3 = qualify(var2.getKey());
         return var3 != null ? var3 : BLANK_QUALIFIER;
      }
   },
   NAMED {
      @Override
      final Annotation qualifies(Key<?> var1, Binding<?> var2) {
         Annotation var3 = qualify(var2.getKey());
         return var3 instanceof Named ? var3 : null;
      }
   },
   NAMED_WITH_ATTRIBUTES {
      @Override
      final Annotation qualifies(Key<?> var1, Binding<?> var2) {
         Annotation var3 = qualify(var2.getKey());
         if (var1.getAnnotation().equals(var3)) {
            return var3;
         } else {
            if (var2 instanceof ConstructorBinding && var2.getKey().getAnnotationType() == null) {
               Class var4 = var2.getKey().getTypeLiteral().getRawType();
               xcord.Named var5 = var4.getAnnotation(xcord.Named.class);
               if (var5 != null && var5.value().equals(((Named)var1.getAnnotation()).value()) && var4.equals(Implementations.find(var2))) {
                  return var1.getAnnotation();
               }
            }

            return null;
         }
      }
   },
   MARKED {
      @Override
      final Annotation qualifies(Key<?> var1, Binding<?> var2) {
         final Class var3 = var1.getAnnotationType();
         Annotation var4 = qualify(var2.getKey());
         if (var3.isInstance(var4)) {
            return var4;
         } else if (var3.equals(var2.getKey().getAnnotationType()) && var3.getDeclaredMethods().length == 0) {
            return new Annotation() {
               @Override
               public Class<? extends Annotation> annotationType() {
                  return var3;
               }
            };
         } else if (var2 instanceof ProviderKeyBinding) {
            Key var6 = ((ProviderKeyBinding)var2).getProviderKey();
            return var6.getTypeLiteral().getRawType().getAnnotation(var3);
         } else {
            Class var5 = Implementations.find(var2);
            return var5 != null ? var5.getAnnotation(var3) : null;
         }
      }
   },
   MARKED_WITH_ATTRIBUTES {
      @Override
      final Annotation qualifies(Key<?> var1, Binding<?> var2) {
         Annotation var3 = MARKED.qualifies(var1, var2);
         return var1.getAnnotation().equals(var3) ? var3 : null;
      }
   };

   static final Annotation DEFAULT_QUALIFIER = Names.named("default");
   static final Annotation BLANK_QUALIFIER = Names.named("");

   private QualifyingStrategy() {
   }

   abstract Annotation qualifies(Key<?> var1, Binding<?> var2);

   static final QualifyingStrategy selectFor(Key<?> var0) {
      Class var1 = var0.getAnnotationType();
      if (var1 == null) {
         return UNRESTRICTED;
      } else if (Named.class == var1) {
         return var0.hasAttributes() ? NAMED_WITH_ATTRIBUTES : NAMED;
      } else {
         return var0.hasAttributes() ? MARKED_WITH_ATTRIBUTES : MARKED;
      }
   }

   static final Annotation qualify(Key<?> var0) {
      if (var0.getAnnotationType() == null) {
         return DEFAULT_QUALIFIER;
      } else {
         Annotation var1 = var0.getAnnotation();
         if (var1 instanceof Provider) {
            Object var2 = ((Provider)var1).get();
            return var2 instanceof Annotation ? (Annotation)var2 : DEFAULT_QUALIFIER;
         } else {
            return var1;
         }
      }
   }
}
