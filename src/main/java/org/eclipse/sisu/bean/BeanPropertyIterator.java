package org.eclipse.sisu.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class BeanPropertyIterator<T> implements Iterator<BeanProperty<T>> {
   private final Iterator<Member> memberIterator;
   private BeanProperty<T> nextProperty;

   BeanPropertyIterator(Iterable<Member> var1) {
      this.memberIterator = var1.iterator();
   }

   @Override
   public boolean hasNext() {
      while (this.nextProperty == null) {
         if (!this.memberIterator.hasNext()) {
            return false;
         }

         Member var1 = this.memberIterator.next();
         int var2 = var1.getModifiers();
         if (!Modifier.isStatic(var2) && !Modifier.isAbstract(var2) && !var1.isSynthetic()) {
            if (var1 instanceof Method) {
               if (isSetter(var1)) {
                  this.nextProperty = new BeanPropertySetter<>((Method)var1);
               }
            } else if (var1 instanceof Field) {
               this.nextProperty = new BeanPropertyField<>((Field)var1);
            }
         }
      }

      return true;
   }

   public BeanProperty<T> next() {
      if (this.hasNext()) {
         BeanProperty var1 = this.nextProperty;
         this.nextProperty = null;
         return var1;
      } else {
         throw new NoSuchElementException();
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }

   private static boolean isSetter(Member var0) {
      String var1 = var0.getName();
      return var1.startsWith("set") && var1.length() > 3 && Character.isUpperCase(var1.charAt(3)) && ((Method)var0).getParameterTypes().length == 1;
   }
}
