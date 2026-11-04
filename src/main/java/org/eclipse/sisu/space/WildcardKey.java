package org.eclipse.sisu.space;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import xcord.Provider;
import xcord.Qualifier;

final class WildcardKey {
   private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);

   private WildcardKey() {
   }

   public static Key<Object> get(Class<?> var0, Annotation var1) {
      return Key.get(OBJECT_TYPE_LITERAL, new WildcardKey.QualifiedImpl(var0, var1));
   }

   @Qualifier
   @Retention(RetentionPolicy.RUNTIME)
   private @interface Qualified {
      Class<?> value();
   }

   private static final class QualifiedImpl implements Provider<Annotation>, WildcardKey.Qualified {
      private final Class<?> value;
      private final Annotation qualifier;

      QualifiedImpl(Class<?> var1, Annotation var2) {
         this.value = var1;
         this.qualifier = var2;
      }

      @Override
      public Class<?> value() {
         return this.value;
      }

      public Annotation get() {
         return this.qualifier;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
         return WildcardKey.Qualified.class;
      }

      @Override
      public int hashCode() {
         return this.value.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return var1 instanceof WildcardKey.QualifiedImpl ? this.value == ((WildcardKey.QualifiedImpl)var1).value : false;
         }
      }

      @Override
      public String toString() {
         return "*";
      }
   }
}
