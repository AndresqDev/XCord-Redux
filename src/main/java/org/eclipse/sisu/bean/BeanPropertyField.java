package org.eclipse.sisu.bean;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class BeanPropertyField<T> implements PrivilegedAction<Void>, BeanProperty<T> {
   private final Field field;

   BeanPropertyField(Field var1) {
      this.field = var1;
   }

   @Override
   public <A extends Annotation> A getAnnotation(Class<A> var1) {
      return this.field.getAnnotation(var1);
   }

   @Override
   public TypeLiteral<T> getType() {
      return TypeLiteral.get(this.field.getGenericType());
   }

   @Override
   public String getName() {
      return this.field.getName();
   }

   @Override
   public <B> void set(B var1, T var2) {
      if (!this.field.isAccessible()) {
         AccessController.doPrivileged(this);
      }

      BeanScheduler.detectCycle(var2);

      try {
         this.field.set(var1, var2);
      } catch (Exception var4) {
         throw new ProvisionException("Error injecting: " + this.field, var4);
      } catch (LinkageError var5) {
         throw new ProvisionException("Error injecting: " + this.field, var5);
      }
   }

   @Override
   public int hashCode() {
      return this.field.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof BeanPropertyField ? this.field.equals(((BeanPropertyField)var1).field) : false;
      }
   }

   @Override
   public String toString() {
      return this.field.toString();
   }

   public Void run() {
      this.field.setAccessible(true);
      return null;
   }
}
