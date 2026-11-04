package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Scopes;
import com.google.inject.name.Named;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Description;
import xcord.Provider;

final class LazyBeanEntry<Q extends Annotation, T> implements BeanEntry<Q, T> {
   private final Q qualifier;
   final Binding<T> binding;
   private final Provider<T> lazyValue;
   private final int rank;

   LazyBeanEntry(Q var1, Binding<T> var2, int var3) {
      if (var1 != null && Named.class == var1.annotationType()) {
         this.qualifier = (Q)(new LazyBeanEntry.JsrNamed((Named)var1));
      } else {
         this.qualifier = (Q)var1;
      }

      this.binding = var2;
      this.rank = var3;
      if (Scopes.isSingleton(var2)) {
         this.lazyValue = var2.getProvider();
      } else {
         this.lazyValue = Guice4.lazy(var2);
      }
   }

   @Override
   public Q getKey() {
      return this.qualifier;
   }

   @Override
   public T getValue() {
      return (T)this.lazyValue.get();
   }

   @Override
   public T setValue(T var1) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Provider<T> getProvider() {
      return this.binding.getProvider();
   }

   @Override
   public String getDescription() {
      Description var1 = Sources.getAnnotation(this.binding, Description.class);
      return var1 != null ? var1.value() : null;
   }

   @Override
   public Class<T> getImplementationClass() {
      return (Class<T>)Implementations.find(this.binding);
   }

   @Override
   public Object getSource() {
      return Guice4.getDeclaringSource(this.binding);
   }

   @Override
   public int getRank() {
      return this.rank;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder().append(this.getKey()).append('=');

      try {
         Class var2 = this.getImplementationClass();
         var1.append(var2 != null ? var2 : this.getProvider());
      } catch (RuntimeException var3) {
         var1.append(var3);
      }

      return var1.toString();
   }

   private static final class JsrNamed implements xcord.Named, Named {
      private final String value;

      JsrNamed(Named var1) {
         this.value = var1.value();
      }

      public String value() {
         return this.value;
      }

      public Class<? extends Annotation> annotationType() {
         return xcord.Named.class;
      }

      @Override
      public int hashCode() {
         return 127 * "value".hashCode() ^ this.value.hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 instanceof Named) {
            return this.value.equals(((Named)var1).value());
         } else {
            return var1 instanceof xcord.Named ? this.value.equals(((xcord.Named)var1).value()) : false;
         }
      }

      @Override
      public String toString() {
         return "@" + xcord.Named.class.getName() + "(value=" + this.value + ")";
      }
   }
}
