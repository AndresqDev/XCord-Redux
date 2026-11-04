package org.eclipse.sisu.bean;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class BeanPropertySetter<T> implements PrivilegedAction<Void>, BeanProperty<T> {
   private final Method method;

   BeanPropertySetter(Method var1) {
      this.method = var1;
   }

   @Override
   public <A extends Annotation> A getAnnotation(Class<A> var1) {
      return this.method.getAnnotation(var1);
   }

   @Override
   public TypeLiteral<T> getType() {
      return TypeLiteral.get(this.method.getGenericParameterTypes()[0]);
   }

   @Override
   public String getName() {
      String var1 = this.method.getName();
      return Character.toLowerCase(var1.charAt(3)) + var1.substring(4);
   }

   @Override
   public <B> void set(B var1, T var2) {
      if (!this.method.isAccessible()) {
         AccessController.doPrivileged(this);
      }

      BeanScheduler.detectCycle(var2);

      try {
         this.method.invoke(var1, var2);
      } catch (Exception var5) {
         Object var4 = var5 instanceof InvocationTargetException ? var5.getCause() : var5;
         throw new ProvisionException("Error injecting: " + this.method, (Throwable)var4);
      } catch (LinkageError var6) {
         throw new ProvisionException("Error injecting: " + this.method, var6);
      }
   }

   @Override
   public int hashCode() {
      return this.method.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof BeanPropertySetter ? this.method.equals(((BeanPropertySetter)var1).method) : false;
      }
   }

   @Override
   public String toString() {
      return this.method.toString();
   }

   public Void run() {
      this.method.setAccessible(true);
      return null;
   }
}
