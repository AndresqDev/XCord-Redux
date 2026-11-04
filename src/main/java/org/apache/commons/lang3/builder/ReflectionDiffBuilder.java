package org.apache.commons.lang3.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ReflectionDiffBuilder implements Builder<DiffResult> {
   private final Object left;
   private final Object right;
   private final DiffBuilder diffBuilder;

   public <T> ReflectionDiffBuilder(T var1, T var2, ToStringStyle var3) {
      this.left = var1;
      this.right = var2;
      this.diffBuilder = new DiffBuilder(var1, var2, var3);
   }

   public DiffResult build() {
      if (this.left.equals(this.right)) {
         return this.diffBuilder.build();
      } else {
         this.appendFields(this.left.getClass());
         return this.diffBuilder.build();
      }
   }

   private void appendFields(Class<?> var1) {
      for (Field var5 : FieldUtils.getAllFields(var1)) {
         if (this.accept(var5)) {
            try {
               this.diffBuilder.append(var5.getName(), FieldUtils.readField(var5, this.left, true), FieldUtils.readField(var5, this.right, true));
            } catch (IllegalAccessException var7) {
               throw new InternalError("Unexpected IllegalAccessException: " + var7.getMessage());
            }
         }
      }
   }

   private boolean accept(Field var1) {
      if (var1.getName().indexOf(36) != -1) {
         return false;
      } else {
         return Modifier.isTransient(var1.getModifiers()) ? false : !Modifier.isStatic(var1.getModifiers());
      }
   }
}
